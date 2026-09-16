package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.usuario.UsuarioRequestDTO;
import br.com.fiap.agendamento.domain.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePacienteWithoutAuthentication() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Paciente " + uniqueSuffix(), "paciente" + uniqueSuffix() + "@email.com",
                "paciente" + uniqueSuffix(), "senha123", Role.PACIENTE, null);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("PACIENTE"));
    }

    @Test
    void shouldReturnValidationErrorForInvalidPayload() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("", "email-invalido", "", "123", null, null);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        String suffix = uniqueSuffix();
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Paciente " + suffix, "dup" + suffix + "@email.com",
                "login" + suffix, "senha123", Role.PACIENTE, null);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        UsuarioRequestDTO dtoDuplicado = new UsuarioRequestDTO("Outro Nome", "dup" + suffix + "@email.com",
                "outrologin" + suffix, "senha123", Role.PACIENTE, null);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoDuplicado)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldRequireAuthenticationToListUsuarios() throws Exception {
        mockMvc.perform(get("/v1/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldListUsuariosWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/usuarios").with(httpBasic("medico", "medico123")))
                .andExpect(status().isOk());
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
