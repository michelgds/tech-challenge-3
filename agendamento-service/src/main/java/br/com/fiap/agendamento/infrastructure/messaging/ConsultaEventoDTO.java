package br.com.fiap.agendamento.infrastructure.messaging;

import java.time.LocalDateTime;

/**
 * Evento assincrono publicado pelo servico de agendamento sempre que uma
 * consulta e criada ou editada, consumido pelo servico de notificacoes.
 */
public record ConsultaEventoDTO(
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String status,
        String tipoEvento
) {
    public static final String TIPO_CRIACAO = "CRIACAO";
    public static final String TIPO_EDICAO = "EDICAO";
}
