package br.com.fiap.notificacao.application.dto;

import br.com.fiap.notificacao.domain.model.StatusNotificacao;

import java.time.LocalDateTime;

public record NotificacaoResponseDTO(
        Long id,
        Long consultaId,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHoraConsulta,
        String tipoEvento,
        String mensagem,
        StatusNotificacao status,
        LocalDateTime dataEnvio
) {
}
