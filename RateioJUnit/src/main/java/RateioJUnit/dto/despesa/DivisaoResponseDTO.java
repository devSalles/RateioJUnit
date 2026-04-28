package RateioJUnit.dto.despesa;

import java.math.BigDecimal;

public record DivisaoResponseDTO(

        Long id,
        String nomeParticipante,
        BigDecimal valor

) {
}
