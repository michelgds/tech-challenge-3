package br.com.fiap.agendamento.application.usecase.usuario;

import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarUsuarioPorIdUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioPorIdUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o id: " + id));
    }
}
