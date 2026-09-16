package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Consulta;

import java.util.List;
import java.util.Optional;

public interface ConsultaRepository {
    List<Consulta> findAll(int page, int size);
    List<Consulta> findByPacienteId(Long pacienteId);
    List<Consulta> findFuturasByPacienteId(Long pacienteId);
    Optional<Consulta> findById(Long id);
    Consulta save(Consulta consulta);
    void deleteById(Long id);
}
