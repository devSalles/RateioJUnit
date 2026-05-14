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

    boolean existsByIdAndDespesasPagasIsNotEmpty(Long idParticipante);

    //Verifica se o participante já participou da despesa
    boolean existsByIdAndDivisaoIsNotEmpty(Long idParticipante);

    //Verifica se está devendedo
    boolean existsByIdAndSaldoDevedorIsNotEmpty(Long idParticipante);

    //Verifica se possui crédito
    boolean existsByIdAndSaldoCredorIsNotEmpty(Long idParticipante);
}
