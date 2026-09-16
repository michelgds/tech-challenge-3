package br.com.fiap.agendamento.application.mapper;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import br.com.fiap.agendamento.application.dto.consulta.ConsultaResponseDTO;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.model.StatusConsulta;

public final class ConsultaMapper {

    private ConsultaMapper() {
    }

    public static Consulta toEntity(ConsultaRequestDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setPacienteId(dto.pacienteId());
        consulta.setMedicoId(dto.medicoId());
        consulta.setDataHora(dto.dataHora());
        consulta.setObservacoes(dto.observacoes());
        consulta.setStatus(StatusConsulta.AGENDADA);
        return consulta;
    }

    public static ConsultaResponseDTO toResponseDTO(Consulta consulta) {
        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getPacienteId(),
                consulta.getMedicoId(),
                consulta.getDataHora(),
                consulta.getStatus(),
                consulta.getObservacoes(),
                consulta.getDataCriacao(),
                consulta.getDataAtualizacao()
        );
    }
}
