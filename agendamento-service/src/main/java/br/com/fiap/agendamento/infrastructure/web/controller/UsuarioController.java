package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.agendamento.application.dto.usuario.UsuarioResponseDTO;
import br.com.fiap.agendamento.application.mapper.UsuarioMapper;
import br.com.fiap.agendamento.application.usecase.usuario.BuscarUsuarioPorIdUseCase;
import br.com.fiap.agendamento.application.usecase.usuario.CriarUsuarioUseCase;
import br.com.fiap.agendamento.application.usecase.usuario.ListarUsuariosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuários", description = "Cadastro de médicos, enfermeiros e pacientes")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final CriarUsuarioUseCase criarUsuarioUseCase;

    public UsuarioController(ListarUsuariosUseCase listarUsuariosUseCase,
                              BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
                              CriarUsuarioUseCase criarUsuarioUseCase) {
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.criarUsuarioUseCase = criarUsuarioUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar usuários com paginação e filtro opcional por nome")
    public ResponseEntity<List<UsuarioResponseDTO>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
    ) {
        logger.info("GET /v1/usuarios - page={}, size={}, nome={}", page, size, nome);
        var usuarios = listarUsuariosUseCase.execute(page, size, nome).stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /v1/usuarios/{}", id);
        var usuario = buscarUsuarioPorIdUseCase.execute(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDTO(usuario));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário (médico, enfermeiro ou paciente)",
            description = "Endpoint público, utilizado para o cadastro inicial de qualquer perfil de usuário.")
    public ResponseEntity<UsuarioResponseDTO> save(@Valid @RequestBody UsuarioRequestDTO dto) {
        logger.info("POST /v1/usuarios - role={}", dto.role());
        var usuario = criarUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponseDTO(usuario));
    }
}
