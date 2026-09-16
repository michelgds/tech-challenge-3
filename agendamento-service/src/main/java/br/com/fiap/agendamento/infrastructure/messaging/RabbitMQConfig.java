package br.com.fiap.agendamento.infrastructure.messaging;

public final class RabbitMQConfig {

    public static final String CONSULTAS_EXCHANGE = "consultas.exchange";
    public static final String ROUTING_KEY_CRIADA = "consulta.criada";
    public static final String ROUTING_KEY_EDITADA = "consulta.editada";

    private RabbitMQConfig() {
    }
}
