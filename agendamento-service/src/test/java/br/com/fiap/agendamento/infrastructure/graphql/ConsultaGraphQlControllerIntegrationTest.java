package br.com.fiap.agendamento.infrastructure.graphql;

import br.com.fiap.agendamento.application.dto.consulta.ConsultaRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa as queries GraphQL de historico/consultas futuras respeitando as
 * regras de acesso por perfil (medico/enfermeiro veem qualquer paciente,
 * paciente ve somente o proprio historico).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ConsultaGraphQlControllerIntegrationTest {

    private static final long PACIENTE_ID = 3L;
    private static final long MEDICO_ID = 1L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void criarConsulta() throws Exception {
        ConsultaRequestDTO dto = new ConsultaRequestDTO(PACIENTE_ID, MEDICO_ID, LocalDateTime.now().plusDays(4), "Consulta GraphQL");
        mockMvc.perform(post("/v1/consultas")
                        .with(httpBasic("medico", "medico123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnHistoricoWhenRequestedByMedico() {
        GraphQlTester tester = buildTester("medico", "medico123");

        List<?> resultado = tester.document("query($pacienteId: ID!) { historicoPorPaciente(pacienteId: $pacienteId) { id status } }")
                .variable("pacienteId", PACIENTE_ID)
                .execute()
                .path("historicoPorPaciente")
                .entityList(Object.class)
                .get();

        assertThat(resultado).isNotEmpty();
    }

    @Test
    void shouldDenyHistoricoWhenPacienteRequestsAnotherPatient() {
        GraphQlTester tester = buildTester("paciente", "paciente123");

        tester.document("query { historicoPorPaciente(pacienteId: 999) { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
    }

    @Test
    void shouldReturnOwnFutureConsultasWhenPacienteRequestsSelf() {
        GraphQlTester tester = buildTester("paciente", "paciente123");

        List<?> resultado = tester.document("query($pacienteId: ID!) { consultasFuturasPorPaciente(pacienteId: $pacienteId) { id } }")
                .variable("pacienteId", PACIENTE_ID)
                .execute()
                .path("consultasFuturasPorPaciente")
                .entityList(Object.class)
                .get();

        assertThat(resultado).isNotEmpty();
    }

    private GraphQlTester buildTester(String username, String password) {
        MockMvc securedMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply((MockMvcConfigurer) springSecurity())
                .defaultRequest((MockHttpServletRequestBuilder) post("/graphql").with(httpBasic(username, password)))
                .build();
        var webTestClientBuilder = org.springframework.test.web.servlet.client.MockMvcWebTestClient.bindTo(securedMockMvc)
                .baseUrl("/graphql");
        return HttpGraphQlTester.builder(webTestClientBuilder).build();
    }
}
