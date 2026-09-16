package br.com.fiap.agendamento.application.mapper;

import br.com.fiap.agendamento.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.agendamento.application.dto.usuario.UsuarioResponseDTO;
import br.com.fiap.agendamento.domain.model.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLogin(dto.login());
        usuario.setSenha(dto.senha());
        usuario.setRole(dto.role());
        usuario.setEspecialidade(dto.especialidade());
        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                usuario.getRole(),
                usuario.getEspecialidade()
        );
    }
}
