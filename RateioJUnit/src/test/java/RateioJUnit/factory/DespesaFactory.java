package RateioJUnit.factory;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import java.math.BigDecimal;

public class DespesaFactory {

    public static Despesa criarDespesa(Long id, BigDecimal valorTotal, StatusDespesa statusDespesa, TipoDivisao tipoDivisao)
    {

        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(1L,"Pedro");

        Despesa despesa = new Despesa();
        despesa.setId(id);
        despesa.setValorTotal(valorTotal);
        despesa.setStatusDespesa(statusDespesa);
        despesa.setTipoDivisao(tipoDivisao);
        despesa.setPagador(participante);

        return despesa;
    }
}
