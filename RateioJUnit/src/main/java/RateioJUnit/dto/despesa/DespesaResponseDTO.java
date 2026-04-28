package RateioJUnit.dto.despesa;

import RateioJUnit.ENUM.StatusDespesa;
import RateioJUnit.ENUM.TipoDivisao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DespesaResponseDTO(

        Long id,
        String descricao,
        BigDecimal valor,
        TipoDivisao tipoDivisao,
        StatusDespesa statusDespesa,
        LocalDateTime dataCriacao,
        Long idPagador,
        List<DivisaoResponseDTO> participantes
) {
}
