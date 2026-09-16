package br.com.fiap.notificacao.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara a topologia RabbitMQ do lado do consumidor: a exchange de topico
 * "consultas.exchange" (tambem declarada pelo servico de agendamento) e a
 * fila "notificacoes.queue", vinculada a todas as routing keys "consulta.*"
 * (consulta.criada e consulta.editada).
 */
@Configuration
public class RabbitMQTopologyConfig {

    @Bean
    public TopicExchange consultasExchange() {
        return new TopicExchange(RabbitMQConfig.CONSULTAS_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificacoesQueue() {
        return new Queue(RabbitMQConfig.NOTIFICACOES_QUEUE, true);
    }

    @Bean
    public Binding notificacoesBinding(Queue notificacoesQueue, TopicExchange consultasExchange) {
        return BindingBuilder.bind(notificacoesQueue).to(consultasExchange).with(RabbitMQConfig.ROUTING_KEY_PATTERN);
    }
}
