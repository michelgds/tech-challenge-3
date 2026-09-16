package br.com.fiap.notificacao.infrastructure.messaging;

import br.com.fiap.notificacao.application.usecase.ProcessarLembreteConsultaUseCase;
import br.com.fiap.notificacao.domain.model.Notificacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa o listener isoladamente (sem broker real), garantindo que o evento
 * recebido é corretamente delegado ao caso de uso de processamento do lembrete.
 */
@ExtendWith(MockitoExtension.class)
class ConsultaEventoListenerTest {

    @Mock
    private ProcessarLembreteConsultaUseCase processarLembreteConsultaUseCase;

    @Test
    void shouldDelegateReceivedEventToUseCase() {
        ConsultaEventoListener listener = new ConsultaEventoListener(processarLembreteConsultaUseCase);
        ConsultaEventoDTO evento = new ConsultaEventoDTO(1L, 2L, 3L,
                LocalDateTime.now().plusDays(1), "AGENDADA", ConsultaEventoDTO.TIPO_CRIACAO);

        when(processarLembreteConsultaUseCase.execute(any(ConsultaEventoDTO.class)))
                .thenReturn(new Notificacao());

        listener.receber(evento);

        verify(processarLembreteConsultaUseCase).execute(evento);
    }
}
