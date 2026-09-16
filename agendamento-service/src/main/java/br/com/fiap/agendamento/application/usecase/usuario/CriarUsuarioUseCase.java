package br.com.fiap.agendamento.application.usecase.usuario;

import br.com.fiap.agendamento.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.agendamento.application.mapper.UsuarioMapper;
import br.com.fiap.agendamento.application.usecase.UseCase;
import br.com.fiap.agendamento.domain.exception.BusinessException;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: cadastrar um novo usuario (medico, enfermeiro ou paciente).
 */
@Component
public class CriarUsuarioUseCase implements UseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario execute(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com este email");
        }
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new BusinessException("Já existe um usuário cadastrado com este login");
        }
        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        return usuarioRepository.save(usuario);
    }
}
