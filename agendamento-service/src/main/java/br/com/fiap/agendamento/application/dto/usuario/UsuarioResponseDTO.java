package br.com.fiap.agendamento.application.dto.usuario;

import br.com.fiap.agendamento.domain.model.Role;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String login,
        Role role,
        String especialidade
) {
}
