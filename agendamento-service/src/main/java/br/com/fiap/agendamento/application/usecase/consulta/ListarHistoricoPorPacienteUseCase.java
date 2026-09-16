package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.AccessDeniedBusinessException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso GraphQL: listar o historico completo de atendimentos de um
 * paciente (todas as consultas, passadas e futuras).
 */
@Component
public class ListarHistoricoPorPacienteUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;

    public ListarHistoricoPorPacienteUseCase(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public List<Consulta> execute(Long pacienteId, UsuarioAutenticado usuarioAutenticado) {
        validarAcesso(pacienteId, usuarioAutenticado);
        return consultaRepository.findByPacienteId(pacienteId);
    }

    private void validarAcesso(Long pacienteId, UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.isPaciente() && !pacienteId.equals(usuarioAutenticado.id())) {
            throw new AccessDeniedBusinessException("Paciente só pode visualizar o próprio histórico de consultas");
        }
    }
}
