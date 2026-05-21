package RateioJUnit.repository;

import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.entity.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespesaRepository  extends JpaRepository<Despesa, Long> {

    List<Despesa> findByStatusDespesa(StatusDespesa statusDespesa);

    List<Despesa> findByTipoDivisao(TipoDivisao tipoDivisao);
}
