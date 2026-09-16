package br.com.fiap.notificacao.domain.repository;

import br.com.fiap.notificacao.domain.model.Notificacao;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository {
    List<Notificacao> findAll(int page, int size, Long pacienteId);
    Optional<Notificacao> findById(Long id);
    Notificacao save(Notificacao notificacao);
}
