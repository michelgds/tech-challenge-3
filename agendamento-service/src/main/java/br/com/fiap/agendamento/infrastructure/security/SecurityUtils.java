package br.com.fiap.agendamento.infrastructure.security;

import br.com.fiap.agendamento.application.usecase.consulta.UsuarioAutenticado;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UsuarioAutenticado toUsuarioAutenticado(AuthenticatedUser authenticatedUser) {
        return new UsuarioAutenticado(authenticatedUser.getId(), authenticatedUser.getUsuario().getRole());
    }
}
