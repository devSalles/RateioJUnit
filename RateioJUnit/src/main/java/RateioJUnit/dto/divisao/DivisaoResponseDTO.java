package RateioJUnit.dto.divisao;

import RateioJUnit.entity.Divisao;

import java.math.BigDecimal;

public record DivisaoResponseDTO(

        Long id,
        String nomeParticipante,
        BigDecimal valor

) {
    public static DivisaoResponseDTO fromDivisao(Divisao divisao)
    {
        return new DivisaoResponseDTO(divisao.getId(), divisao.getParticipante().getNome(), divisao.getValor());
    }
}
