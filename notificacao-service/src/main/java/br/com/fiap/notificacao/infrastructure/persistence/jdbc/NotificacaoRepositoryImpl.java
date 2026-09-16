package br.com.fiap.notificacao.infrastructure.persistence.jdbc;

import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificacaoRepositoryImpl implements NotificacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificacaoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Notificacao> findAll(int page, int size, Long pacienteId) {
        int offset = Math.max(page - 1, 0) * size;
        if (pacienteId != null) {
            return jdbcTemplate.query(
                    "SELECT * FROM notificacoes WHERE paciente_id = ? ORDER BY data_envio DESC LIMIT ? OFFSET ?",
                    new NotificacaoRowMapper(), pacienteId, size, offset);
        }
        return jdbcTemplate.query("SELECT * FROM notificacoes ORDER BY data_envio DESC LIMIT ? OFFSET ?",
                new NotificacaoRowMapper(), size, offset);
    }

    @Override
    public Optional<Notificacao> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM notificacoes WHERE id = ?", new NotificacaoRowMapper(), id)
                .stream().findFirst();
    }

    @Override
    public Notificacao save(Notificacao notificacao) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO notificacoes (consulta_id, paciente_id, medico_id, data_hora_consulta, tipo_evento, mensagem, status, data_envio) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setLong(1, notificacao.getConsultaId());
            ps.setLong(2, notificacao.getPacienteId());
            ps.setLong(3, notificacao.getMedicoId());
            ps.setTimestamp(4, Timestamp.valueOf(notificacao.getDataHoraConsulta()));
            ps.setString(5, notificacao.getTipoEvento());
            ps.setString(6, notificacao.getMensagem());
            ps.setString(7, notificacao.getStatus().name());
            ps.setTimestamp(8, Timestamp.valueOf(notificacao.getDataEnvio()));
            return ps;
        }, keyHolder);
        notificacao.setId(keyHolder.getKey().longValue());
        return notificacao;
    }
}
