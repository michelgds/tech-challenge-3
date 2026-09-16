package br.com.fiap.agendamento.infrastructure.persistence.jdbc;

import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ConsultaRepositoryImpl implements ConsultaRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConsultaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Consulta> findAll(int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return jdbcTemplate.query("SELECT * FROM consultas ORDER BY data_hora LIMIT ? OFFSET ?",
                new ConsultaRowMapper(), size, offset);
    }

    @Override
    public List<Consulta> findByPacienteId(Long pacienteId) {
        return jdbcTemplate.query("SELECT * FROM consultas WHERE paciente_id = ? ORDER BY data_hora",
                new ConsultaRowMapper(), pacienteId);
    }

    @Override
    public List<Consulta> findFuturasByPacienteId(Long pacienteId) {
        return jdbcTemplate.query(
                "SELECT * FROM consultas WHERE paciente_id = ? AND data_hora > NOW() AND status = 'AGENDADA' ORDER BY data_hora",
                new ConsultaRowMapper(), pacienteId);
    }

    @Override
    public Optional<Consulta> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM consultas WHERE id = ?", new ConsultaRowMapper(), id)
                .stream().findFirst();
    }

    @Override
    public Consulta save(Consulta consulta) {
        if (consulta.getId() == null) {
            return insert(consulta);
        }
        return update(consulta);
    }

    private Consulta insert(Consulta consulta) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO consultas (paciente_id, medico_id, data_hora, status, observacoes, data_criacao, data_atualizacao) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setLong(1, consulta.getPacienteId());
            ps.setLong(2, consulta.getMedicoId());
            ps.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            ps.setString(4, consulta.getStatus().name());
            ps.setString(5, consulta.getObservacoes());
            ps.setTimestamp(6, Timestamp.valueOf(consulta.getDataCriacao()));
            ps.setTimestamp(7, Timestamp.valueOf(consulta.getDataAtualizacao()));
            return ps;
        }, keyHolder);
        consulta.setId(keyHolder.getKey().longValue());
        return consulta;
    }

    private Consulta update(Consulta consulta) {
        jdbcTemplate.update(
                "UPDATE consultas SET paciente_id = ?, medico_id = ?, data_hora = ?, status = ?, observacoes = ?, data_atualizacao = ? WHERE id = ?",
                consulta.getPacienteId(), consulta.getMedicoId(), Timestamp.valueOf(consulta.getDataHora()),
                consulta.getStatus().name(), consulta.getObservacoes(), Timestamp.valueOf(consulta.getDataAtualizacao()),
                consulta.getId());
        return consulta;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM consultas WHERE id = ?", id);
    }
}
