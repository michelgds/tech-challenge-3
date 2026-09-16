package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import br.com.fiap.agendamento.application.dto.consulta.ConsultaResponseDTO;
import br.com.fiap.agendamento.application.dto.consulta.ConsultaUpdateDTO;
import br.com.fiap.agendamento.application.mapper.ConsultaMapper;
import br.com.fiap.agendamento.application.usecase.consulta.AtualizarConsultaUseCase;
import br.com.fiap.agendamento.application.usecase.consulta.BuscarConsultaPorIdUseCase;
import br.com.fiap.agendamento.application.usecase.consulta.CriarConsultaUseCase;
import br.com.fiap.agendamento.application.usecase.consulta.ListarConsultasUseCase;
import br.com.fiap.agendamento.infrastructure.security.AuthenticatedUser;
import br.com.fiap.agendamento.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/consultas")
@Tag(name = "Consultas", description = "Agendamento e histórico de consultas médicas")
public class ConsultaController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);

    private final ListarConsultasUseCase listarConsultasUseCase;
    private final BuscarConsultaPorIdUseCase buscarConsultaPorIdUseCase;
    private final CriarConsultaUseCase criarConsultaUseCase;
    private final AtualizarConsultaUseCase atualizarConsultaUseCase;

    public ConsultaController(ListarConsultasUseCase listarConsultasUseCase,
                               BuscarConsultaPorIdUseCase buscarConsultaPorIdUseCase,
                               CriarConsultaUseCase criarConsultaUseCase,
                               AtualizarConsultaUseCase atualizarConsultaUseCase) {
        this.listarConsultasUseCase = listarConsultasUseCase;
        this.buscarConsultaPorIdUseCase = buscarConsultaPorIdUseCase;
        this.criarConsultaUseCase = criarConsultaUseCase;
        this.atualizarConsultaUseCase = atualizarConsultaUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar consultas",
            description = "Médicos e enfermeiros veem todas as consultas. Pacientes veem apenas as próprias.")
    public ResponseEntity<List<ConsultaResponseDTO>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        logger.info("GET /v1/consultas - page={}, size={}", page, size);
        var usuarioAutenticado = SecurityUtils.toUsuarioAutenticado((AuthenticatedUser) authentication.getPrincipal());
        var consultas = listarConsultasUseCase.execute(page, size, usuarioAutenticado).stream()
                .map(ConsultaMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID",
            description = "Pacientes só podem visualizar as próprias consultas.")
    public ResponseEntity<ConsultaResponseDTO> findById(@PathVariable Long id, Authentication authentication) {
        logger.info("GET /v1/consultas/{}", id);
        var usuarioAutenticado = SecurityUtils.toUsuarioAutenticado((AuthenticatedUser) authentication.getPrincipal());
        var consulta = buscarConsultaPorIdUseCase.execute(id, usuarioAutenticado);
        return ResponseEntity.ok(ConsultaMapper.toResponseDTO(consulta));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @Operation(summary = "Registrar nova consulta", description = "Somente médicos e enfermeiros podem agendar consultas.")
    public ResponseEntity<ConsultaResponseDTO> save(@Valid @RequestBody ConsultaRequestDTO dto) {
        logger.info("POST /v1/consultas - pacienteId={}, medicoId={}", dto.pacienteId(), dto.medicoId());
        var consulta = criarConsultaUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ConsultaMapper.toResponseDTO(consulta));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @Operation(summary = "Editar consulta existente", description = "Somente médicos e enfermeiros podem editar consultas.")
    public ResponseEntity<ConsultaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ConsultaUpdateDTO dto) {
        logger.info("PUT /v1/consultas/{}", id);
        var consulta = atualizarConsultaUseCase.execute(id, dto);
        return ResponseEntity.ok(ConsultaMapper.toResponseDTO(consulta));
    }
}
