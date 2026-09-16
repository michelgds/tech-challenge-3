package br.com.fiap.notificacao.application.mapper;

import br.com.fiap.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.fiap.notificacao.domain.model.Notificacao;

public final class NotificacaoMapper {

    private NotificacaoMapper() {
    }

    public static NotificacaoResponseDTO toResponseDTO(Notificacao notificacao) {
        return new NotificacaoResponseDTO(
                notificacao.getId(),
                notificacao.getConsultaId(),
                notificacao.getPacienteId(),
                notificacao.getMedicoId(),
                notificacao.getDataHoraConsulta(),
                notificacao.getTipoEvento(),
                notificacao.getMensagem(),
                notificacao.getStatus(),
                notificacao.getDataEnvio()
        );
    }
}
