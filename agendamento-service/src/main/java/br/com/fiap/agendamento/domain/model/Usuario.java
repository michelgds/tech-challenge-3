package br.com.fiap.agendamento.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Role role;
    private String especialidade;
}
