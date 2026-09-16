package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaUpdateDTO;
import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoDTO;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Caso de uso: editar uma consulta existente (data/hora, status ou observacoes).
 * Somente medicos e enfermeiros podem executa-lo. Publica evento assincrono
 * de edicao para o servico de notificacoes.
 */
@Component
public class AtualizarConsultaUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;
    private final ConsultaEventoPublisher consultaEventoPublisher;

    public AtualizarConsultaUseCase(ConsultaRepository consultaRepository, ConsultaEventoPublisher consultaEventoPublisher) {
        this.consultaRepository = consultaRepository;
        this.consultaEventoPublisher = consultaEventoPublisher;
    }

    public Consulta execute(Long id, ConsultaUpdateDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com o id: " + id));

        consulta.setDataHora(dto.dataHora());
        consulta.setStatus(dto.status());
        consulta.setObservacoes(dto.observacoes());
        consulta.setDataAtualizacao(LocalDateTime.now());

        Consulta atualizada = consultaRepository.save(consulta);

        consultaEventoPublisher.publicarConsultaEditada(new ConsultaEventoDTO(
                atualizada.getId(), atualizada.getPacienteId(), atualizada.getMedicoId(), atualizada.getDataHora(),
                atualizada.getStatus().name(), ConsultaEventoDTO.TIPO_EDICAO));

        return atualizada;
    }
}
