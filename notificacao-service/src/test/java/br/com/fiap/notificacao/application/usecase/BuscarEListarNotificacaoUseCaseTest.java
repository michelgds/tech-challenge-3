package br.com.fiap.notificacao.application.usecase;

import br.com.fiap.notificacao.domain.exception.ResourceNotFoundException;
import br.com.fiap.notificacao.domain.model.Notificacao;
import br.com.fiap.notificacao.domain.model.StatusNotificacao;
import br.com.fiap.notificacao.domain.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarEListarNotificacaoUseCaseTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    private BuscarNotificacaoPorIdUseCase buscarNotificacaoPorIdUseCase;
    private ListarNotificacoesUseCase listarNotificacoesUseCase;

    @BeforeEach
    void setUp() {
        buscarNotificacaoPorIdUseCase = new BuscarNotificacaoPorIdUseCase(notificacaoRepository);
        listarNotificacoesUseCase = new ListarNotificacoesUseCase(notificacaoRepository);
    }

    @Test
    void shouldReturnNotificacaoWhenIdExists() {
        Notificacao notificacao = notificacaoDeExemplo();
        when(notificacaoRepository.findById(1L)).thenReturn(Optional.of(notificacao));

        Notificacao resultado = buscarNotificacaoPorIdUseCase.execute(1L);

        assertThat(resultado).isEqualTo(notificacao);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(notificacaoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarNotificacaoPorIdUseCase.execute(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void shouldListNotificacoesUsingPaginationAndFilter() {
        Notificacao notificacao = notificacaoDeExemplo();
        when(notificacaoRepository.findAll(1, 10, 2L)).thenReturn(List.of(notificacao));

        List<Notificacao> resultado = listarNotificacoesUseCase.execute(1, 10, 2L);

        assertThat(resultado).hasSize(1).containsExactly(notificacao);
    }

    private Notificacao notificacaoDeExemplo() {
        Notificacao notificacao = new Notificacao();
        notificacao.setId(1L);
        notificacao.setConsultaId(10L);
        notificacao.setPacienteId(2L);
        notificacao.setMedicoId(3L);
        notificacao.setDataHoraConsulta(LocalDateTime.of(2025, 3, 1, 10, 0));
        notificacao.setTipoEvento("CRIACAO");
        notificacao.setMensagem("Sua consulta foi agendada.");
        notificacao.setStatus(StatusNotificacao.ENVIADA);
        notificacao.setDataEnvio(LocalDateTime.now());
        return notificacao;
    }
}
