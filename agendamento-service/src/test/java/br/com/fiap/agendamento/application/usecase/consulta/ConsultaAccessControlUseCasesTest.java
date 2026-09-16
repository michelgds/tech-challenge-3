package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.domain.exception.AccessDeniedBusinessException;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.StatusConsulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaAccessControlUseCasesTest {

    @Mock
    private ConsultaRepository consultaRepository;

    private BuscarConsultaPorIdUseCase buscarConsultaPorIdUseCase;
    private ListarConsultasUseCase listarConsultasUseCase;
    private ListarHistoricoPorPacienteUseCase listarHistoricoPorPacienteUseCase;
    private ListarConsultasFuturasPorPacienteUseCase listarConsultasFuturasPorPacienteUseCase;

    @BeforeEach
    void setUp() {
        buscarConsultaPorIdUseCase = new BuscarConsultaPorIdUseCase(consultaRepository);
        listarConsultasUseCase = new ListarConsultasUseCase(consultaRepository);
        listarHistoricoPorPacienteUseCase = new ListarHistoricoPorPacienteUseCase(consultaRepository);
        listarConsultasFuturasPorPacienteUseCase = new ListarConsultasFuturasPorPacienteUseCase(consultaRepository);
    }

    @Test
    void buscarPorIdShouldReturnConsultaWhenPacienteRequestsOwnConsulta() {
        Consulta consulta = consulta(1L, 10L);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThat(buscarConsultaPorIdUseCase.execute(1L, new UsuarioAutenticado(10L, Role.PACIENTE))).isEqualTo(consulta);
    }

    @Test
    void buscarPorIdShouldThrowWhenPacienteRequestsAnotherPatientsConsulta() {
        Consulta consulta = consulta(1L, 10L);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> buscarConsultaPorIdUseCase.execute(1L, new UsuarioAutenticado(999L, Role.PACIENTE)))
                .isInstanceOf(AccessDeniedBusinessException.class);
    }

    @Test
    void buscarPorIdShouldThrowWhenConsultaDoesNotExist() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarConsultaPorIdUseCase.execute(1L, new UsuarioAutenticado(1L, Role.MEDICO)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listarShouldReturnAllConsultasForMedico() {
        List<Consulta> consultas = List.of(consulta(1L, 10L));
        when(consultaRepository.findAll(1, 10)).thenReturn(consultas);

        assertThat(listarConsultasUseCase.execute(1, 10, new UsuarioAutenticado(5L, Role.MEDICO))).isEqualTo(consultas);
    }

    @Test
    void listarShouldReturnOnlyOwnConsultasForPaciente() {
        List<Consulta> consultas = List.of(consulta(1L, 10L));
        when(consultaRepository.findByPacienteId(10L)).thenReturn(consultas);

        assertThat(listarConsultasUseCase.execute(1, 10, new UsuarioAutenticado(10L, Role.PACIENTE))).isEqualTo(consultas);
    }

    @Test
    void historicoShouldThrowWhenPacienteRequestsAnotherPatientsHistory() {
        assertThatThrownBy(() -> listarHistoricoPorPacienteUseCase.execute(10L, new UsuarioAutenticado(999L, Role.PACIENTE)))
                .isInstanceOf(AccessDeniedBusinessException.class);
    }

    @Test
    void historicoShouldReturnConsultasWhenEnfermeiroRequests() {
        List<Consulta> consultas = List.of(consulta(1L, 10L));
        when(consultaRepository.findByPacienteId(10L)).thenReturn(consultas);

        assertThat(listarHistoricoPorPacienteUseCase.execute(10L, new UsuarioAutenticado(3L, Role.ENFERMEIRO))).isEqualTo(consultas);
    }

    @Test
    void futurasShouldThrowWhenPacienteRequestsAnotherPatientsAppointments() {
        assertThatThrownBy(() -> listarConsultasFuturasPorPacienteUseCase.execute(10L, new UsuarioAutenticado(999L, Role.PACIENTE)))
                .isInstanceOf(AccessDeniedBusinessException.class);
    }

    @Test
    void futurasShouldReturnConsultasWhenPacienteRequestsOwnAppointments() {
        List<Consulta> consultas = List.of(consulta(1L, 10L));
        when(consultaRepository.findFuturasByPacienteId(10L)).thenReturn(consultas);

        assertThat(listarConsultasFuturasPorPacienteUseCase.execute(10L, new UsuarioAutenticado(10L, Role.PACIENTE))).isEqualTo(consultas);
    }

    private Consulta consulta(Long id, Long pacienteId) {
        Consulta consulta = new Consulta();
        consulta.setId(id);
        consulta.setPacienteId(pacienteId);
        consulta.setMedicoId(2L);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setStatus(StatusConsulta.AGENDADA);
        return consulta;
    }
}
