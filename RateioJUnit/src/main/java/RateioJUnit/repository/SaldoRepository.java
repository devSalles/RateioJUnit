package RateioJUnit.repository;

import RateioJUnit.entity.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoRepository  extends JpaRepository<Despesa, Long> {
}
