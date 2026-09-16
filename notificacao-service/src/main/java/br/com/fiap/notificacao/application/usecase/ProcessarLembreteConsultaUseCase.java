package br.com.fiap.notificacao.application.usecase;

import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.model.StatusNotificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import br.com.fiap.notificacao.infrastructure.messaging.ConsultaEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Caso de uso: processar o evento assincrono de consulta criada/editada e
 * enviar (simular envio de) um lembrete de consulta ao paciente.
 */
@Component
public class ProcessarLembreteConsultaUseCase implements UseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessarLembreteConsultaUseCase.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificacaoRepository notificacaoRepository;

    public ProcessarLembreteConsultaUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public Notificacao execute(ConsultaEventoDTO evento) {
        String mensagem = montarMensagem(evento);

        Notificacao notificacao = new Notificacao();
        notificacao.setConsultaId(evento.consultaId());
        notificacao.setPacienteId(evento.pacienteId());
        notificacao.setMedicoId(evento.medicoId());
        notificacao.setDataHoraConsulta(evento.dataHora());
        notificacao.setTipoEvento(evento.tipoEvento());
        notificacao.setMensagem(mensagem);
        notificacao.setStatus(StatusNotificacao.ENVIADA);
        notificacao.setDataEnvio(LocalDateTime.now());

        Notificacao salva = notificacaoRepository.save(notificacao);
        logger.info("Lembrete enviado ao paciente {} referente à consulta {} ({})",
                evento.pacienteId(), evento.consultaId(), evento.tipoEvento());
        return salva;
    }

    private String montarMensagem(ConsultaEventoDTO evento) {
        String dataFormatada = evento.dataHora().format(FORMATTER);
        if (ConsultaEventoDTO.TIPO_CRIACAO.equals(evento.tipoEvento())) {
            return "Sua consulta foi agendada para " + dataFormatada + ". Compareça com 15 minutos de antecedência.";
        }
        return "Sua consulta foi atualizada. Novo horário: " + dataFormatada + ".";
    }
}
