package br.com.fiap.notificacao.infrastructure.messaging;

import br.com.fiap.notificacao.application.usecase.ProcessarLembreteConsultaUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consome de forma assincrona os eventos de consulta criada/editada
 * publicados pelo servico de agendamento e aciona o envio do lembrete.
 */
@Component
public class ConsultaEventoListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaEventoListener.class);

    private final ProcessarLembreteConsultaUseCase processarLembreteConsultaUseCase;

    public ConsultaEventoListener(ProcessarLembreteConsultaUseCase processarLembreteConsultaUseCase) {
        this.processarLembreteConsultaUseCase = processarLembreteConsultaUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICACOES_QUEUE)
    public void receber(ConsultaEventoDTO evento) {
        logger.info("Evento de consulta recebido: consultaId={}, tipoEvento={}", evento.consultaId(), evento.tipoEvento());
        processarLembreteConsultaUseCase.execute(evento);
    }
}
