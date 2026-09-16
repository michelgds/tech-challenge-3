package br.com.fiap.agendamento.application.usecase.consulta;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import br.com.fiap.agendamento.application.mapper.ConsultaMapper;
import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.BusinessException;
import br.com.fiap.agendamento.domain.model.Consulta;
import br.com.fiap.agendamento.domain.repository.ConsultaRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoDTO;
import br.com.fiap.agendamento.infrastructure.messaging.ConsultaEventoPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Caso de uso: registrar uma nova consulta. Somente medicos e enfermeiros
 * podem executa-lo (validado via Spring Security no controller/GraphQL).
 * Ao final, publica um evento assincrono para o servico de notificacoes.
 */
@Component
public class CriarConsultaUseCase implements UseCase {

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaEventoPublisher consultaEventoPublisher;

    public CriarConsultaUseCase(ConsultaRepository consultaRepository,
                                 UsuarioRepository usuarioRepository,
                                 ConsultaEventoPublisher consultaEventoPublisher) {
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
        this.consultaEventoPublisher = consultaEventoPublisher;
    }

    public Consulta execute(ConsultaRequestDTO dto) {
        usuarioRepository.findById(dto.pacienteId())
                .filter(u -> u.getRole() == br.com.fiap.agendamento.domain.model.Role.PACIENTE)
                .orElseThrow(() -> new BusinessException("Paciente informado não existe ou não possui perfil PACIENTE"));
        usuarioRepository.findById(dto.medicoId())
                .filter(u -> u.getRole() == br.com.fiap.agendamento.domain.model.Role.MEDICO)
                .orElseThrow(() -> new BusinessException("Médico informado não existe ou não possui perfil MEDICO"));

        Consulta consulta = ConsultaMapper.toEntity(dto);
        consulta.setDataCriacao(LocalDateTime.now());
        consulta.setDataAtualizacao(LocalDateTime.now());
        Consulta salva = consultaRepository.save(consulta);

        consultaEventoPublisher.publicarConsultaCriada(new ConsultaEventoDTO(
                salva.getId(), salva.getPacienteId(), salva.getMedicoId(), salva.getDataHora(),
                salva.getStatus().name(), ConsultaEventoDTO.TIPO_CRIACAO));

        return salva;
    }
}
