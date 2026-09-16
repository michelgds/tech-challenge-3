package br.com.fiap.agendamento.domain.exception;

/**
 * Lancada quando um usuario autenticado tenta acessar um recurso de outro
 * paciente sem possuir o perfil (MEDICO/ENFERMEIRO) que permite tal acesso.
 */
public class AccessDeniedBusinessException extends RuntimeException {
    public AccessDeniedBusinessException(String message) {
        super(message);
    }
}
