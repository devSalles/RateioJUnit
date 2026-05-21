package RateioJUnit.dto.divisao;

import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DivisaoRequestDTO(

        @NotNull(message = "ID de participante obrigatório")
        Long idParticipante,

        BigDecimal valor

) {

    public Divisao toDivisao(Participante participante)
    {
        Divisao divisao = new Divisao();

        divisao.setParticipante(participante);
        divisao.setValor(valor);

        return divisao;
    }
}
