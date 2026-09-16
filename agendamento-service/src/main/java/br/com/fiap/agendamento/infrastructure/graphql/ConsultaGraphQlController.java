package br.com.fiap.agendamento.infrastructure.graphql;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaResponseDTO;
import br.com.fiap.agendamento.application.mapper.ConsultaMapper;
import br.com.fiap.agendamento.application.usecase.consulta.BuscarConsultaPorIdUseCase;
import br.com.fiap.agendamento.application.usecase.consulta.ListarConsultasFuturasPorPacienteUseCase;
import br.com.fiap.agendamento.application.usecase.consulta.ListarHistoricoPorPacienteUseCase;
import br.com.fiap.agendamento.infrastructure.security.AuthenticatedUser;
import br.com.fiap.agendamento.infrastructure.security.SecurityUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Expoe consultas/historico de pacientes via GraphQL, conforme requisito
 * de "Consultas e Historico do Paciente com GraphQL" da Fase 3.
 */
@Controller
public class ConsultaGraphQlController {

    private final ListarHistoricoPorPacienteUseCase listarHistoricoPorPacienteUseCase;
    private final ListarConsultasFuturasPorPacienteUseCase listarConsultasFuturasPorPacienteUseCase;
    private final BuscarConsultaPorIdUseCase buscarConsultaPorIdUseCase;

    public ConsultaGraphQlController(ListarHistoricoPorPacienteUseCase listarHistoricoPorPacienteUseCase,
                                      ListarConsultasFuturasPorPacienteUseCase listarConsultasFuturasPorPacienteUseCase,
                                      BuscarConsultaPorIdUseCase buscarConsultaPorIdUseCase) {
        this.listarHistoricoPorPacienteUseCase = listarHistoricoPorPacienteUseCase;
        this.listarConsultasFuturasPorPacienteUseCase = listarConsultasFuturasPorPacienteUseCase;
        this.buscarConsultaPorIdUseCase = buscarConsultaPorIdUseCase;
    }

    @QueryMapping
    public List<ConsultaResponseDTO> historicoPorPaciente(@Argument Long pacienteId, Authentication authentication) {
        var usuarioAutenticado = SecurityUtils.toUsuarioAutenticado((AuthenticatedUser) authentication.getPrincipal());
        return listarHistoricoPorPacienteUseCase.execute(pacienteId, usuarioAutenticado).stream()
                .map(ConsultaMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping
    public List<ConsultaResponseDTO> consultasFuturasPorPaciente(@Argument Long pacienteId, Authentication authentication) {
        var usuarioAutenticado = SecurityUtils.toUsuarioAutenticado((AuthenticatedUser) authentication.getPrincipal());
        return listarConsultasFuturasPorPacienteUseCase.execute(pacienteId, usuarioAutenticado).stream()
                .map(ConsultaMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping
    public ConsultaResponseDTO consulta(@Argument Long id, Authentication authentication) {
        var usuarioAutenticado = SecurityUtils.toUsuarioAutenticado((AuthenticatedUser) authentication.getPrincipal());
        return ConsultaMapper.toResponseDTO(buscarConsultaPorIdUseCase.execute(id, usuarioAutenticado));
    }
}
