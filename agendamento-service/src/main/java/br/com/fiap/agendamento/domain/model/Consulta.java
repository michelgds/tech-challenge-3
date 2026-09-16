package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Consulta {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime dataHora;
    private StatusConsulta status;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
