package br.com.fiap.agendamento.application.usecase.usuario;

import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarEListarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private ListarUsuariosUseCase listarUsuariosUseCase;

    @BeforeEach
    void setUp() {
        buscarUsuarioPorIdUseCase = new BuscarUsuarioPorIdUseCase(usuarioRepository);
        listarUsuariosUseCase = new ListarUsuariosUseCase(usuarioRepository);
    }

    @Test
    void shouldReturnUsuarioWhenFound() {
        Usuario usuario = usuario(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThat(buscarUsuarioPorIdUseCase.execute(1L)).isEqualTo(usuario);
    }

    @Test
    void shouldThrowWhenUsuarioNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarUsuarioPorIdUseCase.execute(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado com o id: 99");
    }

    @Test
    void shouldDelegateListagemToRepository() {
        List<Usuario> usuarios = List.of(usuario(1L));
        when(usuarioRepository.findAll(1, 10, "Ana")).thenReturn(usuarios);

        assertThat(listarUsuariosUseCase.execute(1, 10, "Ana")).isEqualTo(usuarios);
    }

    private Usuario usuario(Long id) {
        return new Usuario(id, "Ana", "ana@email.com", "ana", "hash", Role.MEDICO, "Cardiologia");
    }
}
