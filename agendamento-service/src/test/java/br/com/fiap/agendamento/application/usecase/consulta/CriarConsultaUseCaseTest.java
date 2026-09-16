package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import br.com.fiap.agendamento.domain.exception.BusinessException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.StatusConsulta;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoDTO;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarConsultaUseCaseTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ConsultaEventoPublisher consultaEventoPublisher;

    private CriarConsultaUseCase criarConsultaUseCase;

    @BeforeEach
    void setUp() {
        criarConsultaUseCase = new CriarConsultaUseCase(consultaRepository, usuarioRepository, consultaEventoPublisher);
    }

    @Test
    void shouldCreateConsultaAndPublishEventWhenPacienteAndMedicoAreValid() {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(1L, 2L, LocalDateTime.now().plusDays(1), "Rotina");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, Role.PACIENTE)));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, Role.MEDICO)));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> {
            Consulta consulta = invocation.getArgument(0);
            consulta.setId(10L);
            return consulta;
        });

        Consulta resultado = criarConsultaUseCase.execute(dto);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
        ArgumentCaptor<ConsultaEventoDTO> captor = ArgumentCaptor.forClass(ConsultaEventoDTO.class);
        verify(consultaEventoPublisher).publicarConsultaCriada(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(ConsultaEventoDTO.TIPO_CRIACAO);
        assertThat(captor.getValue().consultaId()).isEqualTo(10L);
    }

    @Test
    void shouldThrowWhenPacienteDoesNotExist() {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(1L, 2L, LocalDateTime.now().plusDays(1), null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> criarConsultaUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Paciente informado não existe ou não possui perfil PACIENTE");

        verify(consultaRepository, never()).save(any());
        verify(consultaEventoPublisher, never()).publicarConsultaCriada(any());
    }

    @Test
    void shouldThrowWhenMedicoDoesNotExist() {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(1L, 2L, LocalDateTime.now().plusDays(1), null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, Role.PACIENTE)));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> criarConsultaUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Médico informado não existe ou não possui perfil MEDICO");

        verify(consultaRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPacienteIdReferencesUserWithDifferentRole() {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(1L, 2L, LocalDateTime.now().plusDays(1), null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, Role.MEDICO)));

        assertThatThrownBy(() -> criarConsultaUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Paciente informado não existe ou não possui perfil PACIENTE");
    }

    private Usuario usuario(Long id, Role role) {
        return new Usuario(id, "Nome", "email" + id + "@email.com", "login" + id, "hash", role, null);
    }
}
