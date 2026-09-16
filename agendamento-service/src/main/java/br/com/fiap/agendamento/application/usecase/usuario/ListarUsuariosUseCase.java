package br.com.fiap.agendamento.application.usecase.usuario;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarUsuariosUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> execute(int page, int size, String nome) {
        return usuarioRepository.findAll(page, size, nome);
    }
}
