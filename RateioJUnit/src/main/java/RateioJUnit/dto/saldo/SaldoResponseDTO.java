package RateioJUnit.dto.saldo;

import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;

import java.math.BigDecimal;

public record SaldoResponseDTO(

        Long id,
        BigDecimal valor,
        Long idCredor,
        String nomeCredor,
        Long idDevedor,
        String nomeDevedor
) {

    public static SaldoResponseDTO fromSaldo(Saldo saldo)
    {
        return new SaldoResponseDTO(saldo.getId(), saldo.getValor(),
                saldo.getCredor().getId(), saldo.getCredor().getNome(),
                saldo.getDevedor().getId(), saldo.getDevedor().getNome());
    }
}
