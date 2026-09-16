package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caso de uso: listar consultas com paginacao. Medicos e enfermeiros veem
 * todas as consultas; pacientes veem somente as proprias (historico completo).
 */
@Component
public class ListarConsultasUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;

    public ListarConsultasUseCase(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public List<Consulta> execute(int page, int size, UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.isPaciente()) {
            return consultaRepository.findByPacienteId(usuarioAutenticado.id());
        }
        return consultaRepository.findAll(page, size);
    }
}
