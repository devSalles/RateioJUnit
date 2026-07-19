package RateioJUnit.factory;

import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;

import java.math.BigDecimal;

public class DivisaoFactory {

    public static Divisao criarDivisaoComParticipante(Long idDivisao,Participante participante)
    {
        Divisao divisao = new Divisao();
        divisao.setId(idDivisao);
        divisao.setParticipante(participante);
        return divisao;
    }

    public static Divisao criarDivisaoComValor(Long idDivisao,Participante participante, BigDecimal valor)
    {
        Divisao divisao = new Divisao();
        divisao.setId(idDivisao);
        divisao.setParticipante(participante);
        divisao.setValor(valor);
        return divisao;
    }
}
