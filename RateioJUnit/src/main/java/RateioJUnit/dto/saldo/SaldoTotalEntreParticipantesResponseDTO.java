package RateioJUnit.dto.saldo;

import java.math.BigDecimal;

public record SaldoTotalEntreParticipantesResponseDTO(
        Long idCredor,
        String nomeCredor,
        Long idDevedor,
        String nomeDevedor,
        BigDecimal valorTotal
) {
}
