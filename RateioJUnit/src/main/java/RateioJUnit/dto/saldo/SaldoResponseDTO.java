package RateioJUnit.dto.saldo;

import java.math.BigDecimal;

public record SaldoResponseDTO(

        Long id,
        BigDecimal valor,
        String nomeCredor,
        Long idDevedor,
        String nomeDevedor,
        Long idCredor

) {
}
