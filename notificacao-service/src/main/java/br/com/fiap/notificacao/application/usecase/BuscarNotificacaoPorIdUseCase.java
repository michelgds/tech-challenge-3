package br.com.fiap.notificacao.application.usecase;

import br.com.fiap.notificacao.domain.exception.ResourceNotFoundException;
import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarNotificacaoPorIdUseCase implements UseCase {

    private final NotificacaoRepository notificacaoRepository;

    public BuscarNotificacaoPorIdUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public Notificacao execute(Long id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com o id: " + id));
    }
}
