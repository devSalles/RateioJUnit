package RateioJUnit.factory;

import RateioJUnit.entity.Participante;
import RateioJUnit.enums.TipoDivisao;

public class ParticipanteFactory {

    public static Participante criarParticipante()
    {
        Participante participante = new Participante();
        participante.setId(1L);
        participante.setNome("Bernardo");
        participante.setEmail("bernardo@gmail.com");

        return  participante;
    }
}
