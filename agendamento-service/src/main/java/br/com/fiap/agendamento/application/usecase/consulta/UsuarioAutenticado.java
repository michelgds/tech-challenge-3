package br.com.fiap.agendamento.application.usecase.consulta;

/**
 * Representa o usuario autenticado que esta executando uma acao,
 * usado para aplicar as regras de autorizacao a nivel de caso de uso
 * (ex: um paciente so pode ver as proprias consultas).
 */
public record UsuarioAutenticado(Long id, br.com.fiap.agendamento.domain.model.Role role) {

    public boolean isPaciente() {
        return role == br.com.fiap.agendamento.domain.model.Role.PACIENTE;
    }

    public boolean isProfissionalSaude() {
        return role == br.com.fiap.agendamento.domain.model.Role.MEDICO
                || role == br.com.fiap.agendamento.domain.model.Role.ENFERMEIRO;
    }
}
