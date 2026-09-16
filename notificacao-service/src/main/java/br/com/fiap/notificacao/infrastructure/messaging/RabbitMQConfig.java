package br.com.fiap.notificacao.infrastructure.messaging;

public final class RabbitMQConfig {

    public static final String CONSULTAS_EXCHANGE = "consultas.exchange";
    public static final String NOTIFICACOES_QUEUE = "notificacoes.queue";
    public static final String ROUTING_KEY_PATTERN = "consulta.*";

    private RabbitMQConfig() {
    }
}
