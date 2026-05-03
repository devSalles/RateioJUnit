package RateioJUnit.repository;

import RateioJUnit.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    List<Participante> findByNome(String nomeParticipante);

    Optional<Participante> findByEmail(String emailParticipante);

    boolean existsByEmail(String email);

    //Verifica se participante possui despesa pagas
    boolean existsBydespesasPagas(Long idParticipante);

    //Verifica se participante possui despesas no histórico
    boolean existsByDespesasPagasPagador(Long idParticipante);
}
