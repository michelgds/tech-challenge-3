package br.com.fiap.agendamento.application.dto.consulta;

import br.com.fiap.agendamento.domain.model.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponseDTO(
        Long id,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        StatusConsulta status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
