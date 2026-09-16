package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaUpdateDTO;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.model.StatusConsulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarConsultaUseCaseTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private ConsultaEventoPublisher consultaEventoPublisher;

    private AtualizarConsultaUseCase atualizarConsultaUseCase;

    @BeforeEach
    void setUp() {
        atualizarConsultaUseCase = new AtualizarConsultaUseCase(consultaRepository, consultaEventoPublisher);
    }

    @Test
    void shouldUpdateConsultaAndPublishEditEventWhenConsultaExists() {
        Consulta existente = consulta(5L);
        ConsultaUpdateDTO dto = new ConsultaUpdateDTO(LocalDateTime.now().plusDays(2), StatusConsulta.REALIZADA, "Concluída");
        when(consultaRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consulta resultado = atualizarConsultaUseCase.execute(5L, dto);

        assertThat(resultado.getStatus()).isEqualTo(StatusConsulta.REALIZADA);
        assertThat(resultado.getObservacoes()).isEqualTo("Concluída");
        ArgumentCaptor<ConsultaEventoDTO> captor = ArgumentCaptor.forClass(ConsultaEventoDTO.class);
        verify(consultaEventoPublisher).publicarConsultaEditada(captor.capture());
        assertThat(captor.getValue().tipoEvento()).isEqualTo(ConsultaEventoDTO.TIPO_EDICAO);
    }

    @Test
    void shouldThrowWhenConsultaDoesNotExist() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());
        ConsultaUpdateDTO dto = new ConsultaUpdateDTO(LocalDateTime.now().plusDays(2), StatusConsulta.REALIZADA, null);

        assertThatThrownBy(() -> atualizarConsultaUseCase.execute(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Consulta não encontrada com o id: 99");
    }

    private Consulta consulta(Long id) {
        Consulta consulta = new Consulta();
        consulta.setId(id);
        consulta.setPacienteId(1L);
        consulta.setMedicoId(2L);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setDataCriacao(LocalDateTime.now().minusDays(1));
        consulta.setDataAtualizacao(LocalDateTime.now().minusDays(1));
        return consulta;
    }
}
