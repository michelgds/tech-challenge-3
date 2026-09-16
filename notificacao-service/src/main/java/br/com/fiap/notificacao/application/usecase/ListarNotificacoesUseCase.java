package br.com.fiap.notificacao.application.usecase;

import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarNotificacoesUseCase implements UseCase {

    private final NotificacaoRepository notificacaoRepository;

    public ListarNotificacoesUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Notificacao> execute(int page, int size, Long pacienteId) {
        return notificacaoRepository.findAll(page, size, pacienteId);
    }
}
