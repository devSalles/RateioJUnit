package RateioJUnit.factory;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;

public class SaldoFactory {

    public static void adicionarSaldo(Participante participante, Despesa despesa)
    {
        Saldo saldoCredor = new Saldo();
        saldoCredor.setDespesa(despesa);

        Saldo saldoDevedor = new Saldo();
        saldoDevedor.setDespesa(despesa);

        participante.getSaldoCredor().add(saldoCredor);
        participante.getSaldoDevedor().add(saldoDevedor);
    }
}
