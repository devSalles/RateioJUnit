package RateioJUnit.dto.saldo;

import RateioJUnit.entity.Saldo;

import java.math.BigDecimal;

public record ResumoSaldoTotalResponseDTO(
        BigDecimal totalReceber,
        BigDecimal totalDever
) {
}
