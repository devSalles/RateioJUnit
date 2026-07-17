package RateioJUnit.factory;

import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;
import RateioJUnit.enums.TipoDivisao;

import java.util.ArrayList;

public class ParticipanteFactory {

    public static Participante criarParticipante()
    {
        Participante participante = new Participante();
        participante.setId(1L);
        participante.setNome("Bernardo");
        participante.setEmail("bernardo@gmail.com");

        return  participante;
    }

    public static Participante criarParticipantePersonalizado(Long id, String nome)
    {
        Participante participante = new Participante();

        participante.setId(id);
        participante.setNome(nome);
        participante.setEmail(nome.toLowerCase().replace(" ","" + "@email.com"));

        return participante;
    }

    public  static Participante criarParticipanteComSaldo(Long id,String nome, Despesa despesa)
    {
        Participante participante = new Participante();
        participante.setId(id);
        participante.setNome(nome);
        participante.setSaldoCredor(new ArrayList<>());
        participante.setSaldoDevedor(new ArrayList<>());

        Saldo saldoCredor = new Saldo();
        saldoCredor.setDespesa(despesa);

        Saldo saldoDevedor = new Saldo();
        saldoDevedor.setDespesa(despesa);

        participante.getSaldoCredor().add(saldoCredor);
        participante.getSaldoDevedor().add(saldoDevedor);

        return participante;
    }
}
