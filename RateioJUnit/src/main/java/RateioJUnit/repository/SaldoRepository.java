package RateioJUnit.repository;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoRepository  extends JpaRepository<Saldo, Long> {


}
