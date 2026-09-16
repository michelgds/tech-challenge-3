package br.com.fiap.agendamento.infrastructure.persistence.jdbc;

import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.Usuario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRowMapper implements RowMapper<Usuario> {
    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setRole(Role.valueOf(rs.getString("role")));
        usuario.setEspecialidade(rs.getString("especialidade"));
        return usuario;
    }
}
