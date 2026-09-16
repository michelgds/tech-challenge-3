package br.com.fiap.agendamento.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracao de seguranca da aplicacao: autenticacao HTTP Basic com
 * controle de acesso por perfil (MEDICO, ENFERMEIRO, PACIENTE), conforme
 * requisito da Fase 3 do Tech Challenge.
 *
 * <ul>
 *     <li>Médicos: podem visualizar e editar o histórico de consultas.</li>
 *     <li>Enfermeiros: podem registrar consultas e acessar o histórico.</li>
 *     <li>Pacientes: podem visualizar apenas as suas consultas.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/v1/usuarios").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/v1/consultas").hasAnyRole("MEDICO", "ENFERMEIRO")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/v1/consultas/**").hasAnyRole("MEDICO", "ENFERMEIRO")
                        .requestMatchers("/graphql", "/graphiql/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(basic -> {});
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
