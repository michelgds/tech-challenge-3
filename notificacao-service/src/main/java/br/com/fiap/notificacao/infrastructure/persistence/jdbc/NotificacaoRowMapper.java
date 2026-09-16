package br.com.fiap.notificacao.infrastructure.persistence.jdbc;

import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.model.StatusNotificacao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificacaoRowMapper implements RowMapper<Notificacao> {
    @Override
    public Notificacao mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notificacao notificacao = new Notificacao();
        notificacao.setId(rs.getLong("id"));
        notificacao.setConsultaId(rs.getLong("consulta_id"));
        notificacao.setPacienteId(rs.getLong("paciente_id"));
        notificacao.setMedicoId(rs.getLong("medico_id"));
        notificacao.setDataHoraConsulta(rs.getTimestamp("data_hora_consulta").toLocalDateTime());
        notificacao.setTipoEvento(rs.getString("tipo_evento"));
        notificacao.setMensagem(rs.getString("mensagem"));
        notificacao.setStatus(StatusNotificacao.valueOf(rs.getString("status")));
        notificacao.setDataEnvio(rs.getTimestamp("data_envio").toLocalDateTime());
        return notificacao;
    }
}
