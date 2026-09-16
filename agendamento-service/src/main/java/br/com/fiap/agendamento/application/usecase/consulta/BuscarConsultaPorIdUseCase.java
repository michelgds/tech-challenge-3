package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.AccessDeniedBusinessException;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: buscar uma consulta por id. Pacientes so podem visualizar
 * as proprias consultas; medicos e enfermeiros podem visualizar qualquer uma.
 */
@Component
public class BuscarConsultaPorIdUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;

    public BuscarConsultaPorIdUseCase(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public Consulta execute(Long id, UsuarioAutenticado usuarioAutenticado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com o id: " + id));

        if (usuarioAutenticado.isPaciente() && !consulta.getPacienteId().equals(usuarioAutenticado.id())) {
            throw new AccessDeniedBusinessException("Paciente só pode visualizar as próprias consultas");
        }
        return consulta;
    }
}
