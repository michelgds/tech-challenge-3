package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    List<Usuario> findAll(int page, int size, String nome);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
}
