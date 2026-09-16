package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.AccessDeniedBusinessException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso GraphQL: listar apenas as consultas futuras (AGENDADA e
 * com data/hora posterior a agora) de um paciente.
 */
@Component
public class ListarConsultasFuturasPorPacienteUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;

    public ListarConsultasFuturasPorPacienteUseCase(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public List<Consulta> execute(Long pacienteId, UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.isPaciente() && !pacienteId.equals(usuarioAutenticado.id())) {
            throw new AccessDeniedBusinessException("Paciente só pode visualizar as próprias consultas futuras");
        }
        return consultaRepository.findFuturasByPacienteId(pacienteId);
    }
}
