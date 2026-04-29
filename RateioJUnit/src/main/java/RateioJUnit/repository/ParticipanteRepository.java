package RateioJUnit.repository;

import RateioJUnit.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    boolean existsByEmail(String email);
}
