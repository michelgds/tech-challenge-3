package br.com.fiap.agendamento.application.dto.usuario;

import br.com.fiap.agendamento.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "nome é obrigatório") String nome,
        @NotBlank(message = "email é obrigatório") @Email(message = "email inválido") String email,
        @NotBlank(message = "login é obrigatório") String login,
        @NotBlank(message = "senha é obrigatória") @Size(min = 6, message = "senha deve ter ao menos 6 caracteres") String senha,
        @NotNull(message = "role é obrigatório") Role role,
        String especialidade
) {
}
