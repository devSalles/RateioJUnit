package RateioJUnit.factory;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;

import java.math.BigDecimal;

public class SaldoFactory {

    public static Saldo criarSaldo(String nome)
    {
        Participante credor = ParticipanteFactory.criarParticipantePersonalizado(1L,"Thais");
        Participante devedor = ParticipanteFactory.criarParticipantePersonalizado(2L,"Pedro");

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("300.00"), StatusDespesa.CRIADA, TipoDivisao.IGUAL);

        Saldo saldo = new Saldo();
        saldo.setId(1L);
        saldo.setValor(new BigDecimal("300.00"));
        saldo.setDespesa(despesa);
        saldo.setDevedor(devedor);
        saldo.setCredor(credor);

        return saldo;
    }
}
