package br.com.fiap.notificacao.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Notificacao {
    private Long id;
    private Long consultaId;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime dataHoraConsulta;
    private String tipoEvento;
    private String mensagem;
    private StatusNotificacao status;
    private LocalDateTime dataEnvio;
}
