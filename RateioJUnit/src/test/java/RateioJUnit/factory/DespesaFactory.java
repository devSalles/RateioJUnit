package RateioJUnit.factory;

import RateioJUnit.entity.Despesa;
import RateioJUnit.enums.StatusDespesa;

import java.math.BigDecimal;

public class DespesaFactory {

    public static Despesa criarDespesa()
    {
        Despesa despesa = new Despesa();
        despesa.setId(1L);
        despesa.setValorTotal(new BigDecimal(300));
        despesa.setStatusDespesa(StatusDespesa.CRIADA);

        
        return despesa;
    }
}
