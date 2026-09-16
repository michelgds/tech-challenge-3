package br.com.fiap.agendamento.application.usecase.usuario;

import br.com.fiap.agendamento.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.agendamento.domain.exception.BusinessException;
import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private CriarUsuarioUseCase criarUsuarioUseCase;

    @BeforeEach
    void setUp() {
        criarUsuarioUseCase = new CriarUsuarioUseCase(usuarioRepository, passwordEncoder);
    }

    @Test
    void shouldPersistUsuarioWithEncodedPasswordWhenEmailAndLoginAreUnique() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Paciente Teste", "paciente@email.com", "paciente1", "senha123", Role.PACIENTE, null);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(usuarioRepository.existsByLogin(dto.login())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = criarUsuarioUseCase.execute(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getSenha()).isEqualTo("hash-senha");
        assertThat(salvo.getNome()).isEqualTo(dto.nome());
        assertThat(salvo.getRole()).isEqualTo(Role.PACIENTE);
        assertThat(resultado).isEqualTo(salvo);
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Paciente Teste", "paciente@email.com", "paciente1", "senha123", Role.PACIENTE, null);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThatThrownBy(() -> criarUsuarioUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um usuário cadastrado com este email");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenLoginAlreadyExists() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Paciente Teste", "paciente@email.com", "paciente1", "senha123", Role.PACIENTE, null);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(usuarioRepository.existsByLogin(dto.login())).thenReturn(true);

        assertThatThrownBy(() -> criarUsuarioUseCase.execute(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um usuário cadastrado com este login");

        verify(usuarioRepository, never()).save(any());
    }
}
