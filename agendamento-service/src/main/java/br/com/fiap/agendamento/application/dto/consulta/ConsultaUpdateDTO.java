package br.com.fiap.agendamento.application.dto.consulta;

import br.com.fiap.agendamento.domain.model.StatusConsulta;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaUpdateDTO(
        @NotNull(message = "dataHora é obrigatória") LocalDateTime dataHora,
        @NotNull(message = "status é obrigatório") StatusConsulta status,
        String observacoes
) {
}
