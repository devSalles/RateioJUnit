package RateioJUnit.repository;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaldoRepository  extends JpaRepository<Saldo, Long> {

    List<Saldo> findByCredor(Long idCredor);

    List<Saldo> findByDevedor(Long idDevedor);

    List<Saldo> findByCredorIdOrDevedorId(Long idCredor, Long idDevedor);

    List<Saldo> findByCredorIdAndDevedorId(Long idDevedor, Long idCredor);
}
