package br.com.fiap.notificacao.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldRequireAuthenticationToListNotificacoes() throws Exception {
        mockMvc.perform(get("/v1/notificacoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldListNotificacoesWhenAuthenticated() throws Exception {
        Long consultaId = inserirNotificacao(1L, 2L);

        mockMvc.perform(get("/v1/notificacoes").with(httpBasic("staff", "staff123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldFilterNotificacoesByPacienteId() throws Exception {
        inserirNotificacao(5L, 50L);

        mockMvc.perform(get("/v1/notificacoes")
                        .param("pacienteId", "50")
                        .with(httpBasic("staff", "staff123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(50));
    }

    @Test
    void shouldReturnNotificacaoById() throws Exception {
        Long consultaId = inserirNotificacao(7L, 70L);
        Long id = jdbcTemplate.queryForObject(
                "SELECT id FROM notificacoes WHERE consulta_id = ?", Long.class, consultaId);

        mockMvc.perform(get("/v1/notificacoes/" + id).with(httpBasic("staff", "staff123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultaId").value(consultaId));
    }

    @Test
    void shouldReturnNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/v1/notificacoes/999999").with(httpBasic("staff", "staff123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(get("/v1/notificacoes").with(httpBasic("staff", "senhaerrada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/v1/notificacoes/abc").with(httpBasic("staff", "staff123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Tipo de parâmetro inválido"));
    }

    @Test
    void shouldReturnBadRequestWhenPageParamIsNotNumeric() throws Exception {
        mockMvc.perform(get("/v1/notificacoes")
                        .param("page", "abc")
                        .with(httpBasic("staff", "staff123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Tipo de parâmetro inválido"));
    }

    private Long inserirNotificacao(long consultaId, long pacienteId) {
        jdbcTemplate.update("""
                        INSERT INTO notificacoes
                            (consulta_id, paciente_id, medico_id, data_hora_consulta, tipo_evento, mensagem, status, data_envio)
                        VALUES (?, ?, ?, ?, 'CRIACAO', 'mensagem de teste', 'ENVIADA', ?)
                        """,
                consultaId, pacienteId, 1L, LocalDateTime.now().plusDays(1), LocalDateTime.now());
        return consultaId;
    }
}
