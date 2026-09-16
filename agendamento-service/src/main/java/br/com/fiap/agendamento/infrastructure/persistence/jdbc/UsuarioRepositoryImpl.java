package br.com.fiap.agendamento.infrastructure.persistence.jdbc;

import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Usuario> findAll(int page, int size, String nome) {
        int offset = Math.max(page - 1, 0) * size;
        if (nome != null && !nome.isBlank()) {
            return jdbcTemplate.query(
                    "SELECT * FROM usuarios WHERE LOWER(nome) LIKE LOWER(?) ORDER BY id LIMIT ? OFFSET ?",
                    new UsuarioRowMapper(), "%" + nome + "%", size, offset);
        }
        return jdbcTemplate.query("SELECT * FROM usuarios ORDER BY id LIMIT ? OFFSET ?",
                new UsuarioRowMapper(), size, offset);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        List<Usuario> resultado = jdbcTemplate.query("SELECT * FROM usuarios WHERE id = ?", new UsuarioRowMapper(), id);
        return resultado.stream().findFirst();
    }

    @Override
    public Optional<Usuario> findByLogin(String login) {
        List<Usuario> resultado = jdbcTemplate.query("SELECT * FROM usuarios WHERE login = ?", new UsuarioRowMapper(), login);
        return resultado.stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByLogin(String login) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios WHERE login = ?", Integer.class, login);
        return count != null && count > 0;
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            return insert(usuario);
        }
        return update(usuario);
    }

    private Usuario insert(Usuario usuario) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO usuarios (nome, email, login, senha, role, especialidade) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getLogin());
            ps.setString(4, usuario.getSenha());
            ps.setString(5, usuario.getRole().name());
            ps.setString(6, usuario.getEspecialidade());
            return ps;
        }, keyHolder);
        usuario.setId(keyHolder.getKey().longValue());
        return usuario;
    }

    private Usuario update(Usuario usuario) {
        jdbcTemplate.update(
                "UPDATE usuarios SET nome = ?, email = ?, login = ?, senha = ?, role = ?, especialidade = ? WHERE id = ?",
                usuario.getNome(), usuario.getEmail(), usuario.getLogin(), usuario.getSenha(),
                usuario.getRole().name(), usuario.getEspecialidade(), usuario.getId());
        return usuario;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM usuarios WHERE id = ?", id);
    }
}
