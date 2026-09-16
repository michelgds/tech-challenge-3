package br.com.fiap.notificacao.infrastructure.web.controller;

import br.com.fiap.notificacao.application.dto.NotificacaoResponseDTO;
import br.com.fiap.notificacao.application.mapper.NotificacaoMapper;
import br.com.fiap.notificacao.application.usecase.BuscarNotificacaoPorIdUseCase;
import br.com.fiap.notificacao.application.usecase.ListarNotificacoesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notificacoes")
@Tag(name = "Notificações", description = "Consulta dos lembretes de consulta enviados aos pacientes")
public class NotificacaoController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoController.class);

    private final ListarNotificacoesUseCase listarNotificacoesUseCase;
    private final BuscarNotificacaoPorIdUseCase buscarNotificacaoPorIdUseCase;

    public NotificacaoController(ListarNotificacoesUseCase listarNotificacoesUseCase,
                                  BuscarNotificacaoPorIdUseCase buscarNotificacaoPorIdUseCase) {
        this.listarNotificacoesUseCase = listarNotificacoesUseCase;
        this.buscarNotificacaoPorIdUseCase = buscarNotificacaoPorIdUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar notificações enviadas",
            description = "Permite filtrar opcionalmente pelo id do paciente.")
    public ResponseEntity<List<NotificacaoResponseDTO>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long pacienteId
    ) {
        logger.info("GET /v1/notificacoes - page={}, size={}, pacienteId={}", page, size, pacienteId);
        var notificacoes = listarNotificacoesUseCase.execute(page, size, pacienteId).stream()
                .map(NotificacaoMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID")
    public ResponseEntity<NotificacaoResponseDTO> findById(@PathVariable Long id) {
        logger.info("GET /v1/notificacoes/{}", id);
        var notificacao = buscarNotificacaoPorIdUseCase.execute(id);
        return ResponseEntity.ok(NotificacaoMapper.toResponseDTO(notificacao));
    }
}
