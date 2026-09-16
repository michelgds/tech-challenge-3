package br.com.fiap.agendamento.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyConfig {

    @Bean
    public TopicExchange consultasExchange() {
        return new TopicExchange(RabbitMQConfig.CONSULTAS_EXCHANGE, true, false);
    }
}
