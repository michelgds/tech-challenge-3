package br.com.fiap.agendamento.infrastructure.persistence.jdbc;

import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.model.StatusConsulta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ConsultaRowMapper implements RowMapper<Consulta> {
    @Override
    public Consulta mapRow(ResultSet rs, int rowNum) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setId(rs.getLong("id"));
        consulta.setPacienteId(rs.getLong("paciente_id"));
        consulta.setMedicoId(rs.getLong("medico_id"));
        consulta.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        consulta.setStatus(StatusConsulta.valueOf(rs.getString("status")));
        consulta.setObservacoes(rs.getString("observacoes"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp atualizacao = rs.getTimestamp("data_atualizacao");
        consulta.setDataCriacao(criacao != null ? criacao.toLocalDateTime() : null);
        consulta.setDataAtualizacao(atualizacao != null ? atualizacao.toLocalDateTime() : null);
        return consulta;
    }
}
