package br.com.fiap.notificacao.infrastructure.messaging;

import java.time.LocalDateTime;

/**
 * Espelha o contrato de evento publicado pelo servico de agendamento.
 * Mantido como uma copia local (comunicacao desacoplada via mensageria)
 * para que os dois servicos possam evoluir de forma independente.
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
