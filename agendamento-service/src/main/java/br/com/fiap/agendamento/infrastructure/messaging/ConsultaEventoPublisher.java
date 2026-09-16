package br.com.fiap.agendamento.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Responsavel por publicar eventos de consulta (criacao/edicao) no RabbitMQ
 * para que o servico de notificacoes envie o lembrete ao paciente.
 */
@Component
public class ConsultaEventoPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaEventoPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public ConsultaEventoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarConsultaCriada(ConsultaEventoDTO evento) {
        publicar(RabbitMQConfig.ROUTING_KEY_CRIADA, evento);
    }

    public void publicarConsultaEditada(ConsultaEventoDTO evento) {
        publicar(RabbitMQConfig.ROUTING_KEY_EDITADA, evento);
    }

    private void publicar(String routingKey, ConsultaEventoDTO evento) {
        logger.info("Publicando evento de consulta [routingKey={}, consultaId={}]", routingKey, evento.consultaId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.CONSULTAS_EXCHANGE, routingKey, evento);
    }
}
