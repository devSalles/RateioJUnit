package RateioJUnit.factory;

import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;

public class DivisaoFactory {

    public static Divisao criarDivisaoComParticipante(Long id,Participante participante)
    {
        Divisao divisao = new Divisao();
        divisao.setId(id);
        divisao.setParticipante(participante);
        return divisao;
    }
}
