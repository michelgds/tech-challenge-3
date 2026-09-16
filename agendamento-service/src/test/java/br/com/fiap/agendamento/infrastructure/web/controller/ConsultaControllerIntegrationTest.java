package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import br.com.fiap.agendamento.application.dto.consulta.ConsultaUpdateDTO;
import br.com.fiap.agendamento.domain.model.StatusConsulta;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    private static final long PACIENTE_ID = 3L; // seed data.sql -> login 'paciente'
    private static final long MEDICO_ID = 1L;   // seed data.sql -> login 'medico'

    @Test
    void shouldRejectCreationWithoutAuthentication() throws Exception {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(PACIENTE_ID, MEDICO_ID, LocalDateTime.now().plusDays(3), "Rotina");

        mockMvc.perform(post("/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectCreationWhenAuthenticatedAsPaciente() throws Exception {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(PACIENTE_ID, MEDICO_ID, LocalDateTime.now().plusDays(3), "Rotina");

        mockMvc.perform(post("/v1/consultas")
                        .with(httpBasic("paciente", "paciente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldExecuteConsultaCrudFlowForMedico() throws Exception {
        ConsultaRequestDTO createDto = new ConsultaRequestDTO(PACIENTE_ID, MEDICO_ID, LocalDateTime.now().plusDays(5), "Primeira consulta");

        MvcResult createResult = mockMvc.perform(post("/v1/consultas")
                        .with(httpBasic("medico", "medico123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = readJson(createResult);
        long consultaId = created.path("id").asLong();
        assertThat(created.path("status").asText()).isEqualTo("AGENDADA");

        mockMvc.perform(get("/v1/consultas/{id}", consultaId).with(httpBasic("enfermeiro", "enfermeiro123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(consultaId));

        ConsultaUpdateDTO updateDto = new ConsultaUpdateDTO(LocalDateTime.now().plusDays(6), StatusConsulta.REALIZADA, "Atendimento concluído");

        mockMvc.perform(put("/v1/consultas/{id}", consultaId)
                        .with(httpBasic("enfermeiro", "enfermeiro123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REALIZADA"));
    }

    @Test
    void pacienteShouldOnlySeeOwnConsultaWhenFetchingById() throws Exception {
        ConsultaRequestDTO createDto = new ConsultaRequestDTO(PACIENTE_ID, MEDICO_ID, LocalDateTime.now().plusDays(10), "Consulta paciente dono");
        MvcResult createResult = mockMvc.perform(post("/v1/consultas")
                        .with(httpBasic("medico", "medico123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();
        long consultaId = readJson(createResult).path("id").asLong();

        mockMvc.perform(get("/v1/consultas/{id}", consultaId).with(httpBasic("paciente", "paciente123")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnValidationErrorForInvalidPayload() throws Exception {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(null, null, null, null);

        mockMvc.perform(post("/v1/consultas")
                        .with(httpBasic("medico", "medico123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }
}
