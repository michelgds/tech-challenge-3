package br.com.fiap.notificacao.application.usecase;

import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.model.StatusNotificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import br.com.fiap.notificacao.infrastructure.messaging.ConsultaEventoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessarLembreteConsultaUseCaseTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    private ProcessarLembreteConsultaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessarLembreteConsultaUseCase(notificacaoRepository);
    }

    @Test
    void shouldPersistNotificacaoWithMensagemDeCriacaoWhenTipoEventoIsCriacao() {
        ConsultaEventoDTO evento = new ConsultaEventoDTO(1L, 2L, 3L,
                LocalDateTime.of(2025, 1, 10, 14, 30), "AGENDADA", ConsultaEventoDTO.TIPO_CRIACAO);

        when(notificacaoRepository.save(any(Notificacao.class)))
                .thenAnswer(invocation -> {
                    Notificacao n = invocation.getArgument(0);
                    n.setId(99L);
                    return n;
                });

        Notificacao resultado = useCase.execute(evento);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        Notificacao salva = captor.getValue();

        assertThat(resultado.getId()).isEqualTo(99L);
        assertThat(salva.getConsultaId()).isEqualTo(1L);
        assertThat(salva.getPacienteId()).isEqualTo(2L);
        assertThat(salva.getMedicoId()).isEqualTo(3L);
        assertThat(salva.getTipoEvento()).isEqualTo(ConsultaEventoDTO.TIPO_CRIACAO);
        assertThat(salva.getStatus()).isEqualTo(StatusNotificacao.ENVIADA);
        assertThat(salva.getDataEnvio()).isNotNull();
        assertThat(salva.getMensagem()).contains("Sua consulta foi agendada para");
    }

    @Test
    void shouldPersistNotificacaoWithMensagemDeEdicaoWhenTipoEventoIsEdicao() {
        ConsultaEventoDTO evento = new ConsultaEventoDTO(4L, 5L, 6L,
                LocalDateTime.of(2025, 2, 20, 9, 0), "AGENDADA", ConsultaEventoDTO.TIPO_EDICAO);

        when(notificacaoRepository.save(any(Notificacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notificacao resultado = useCase.execute(evento);

        assertThat(resultado.getMensagem()).contains("Sua consulta foi atualizada");
    }
}
