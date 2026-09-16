package br.com.fiap.agendamento.infrastructure.security;

import br.com.fiap.agendamento.domain.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta o {@link Usuario} de dominio para o contrato do Spring Security,
 * expondo a role com o prefixo padrao "ROLE_" (ex: ROLE_MEDICO).
 */
public class AuthenticatedUser implements UserDetails {

    private final Usuario usuario;

    public AuthenticatedUser(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return usuario.getId();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getLogin();
    }
}
