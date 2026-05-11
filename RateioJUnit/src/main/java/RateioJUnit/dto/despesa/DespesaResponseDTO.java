package RateioJUnit.dto.despesa;

import RateioJUnit.ENUM.StatusDespesa;
import RateioJUnit.ENUM.TipoDivisao;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Divisao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DespesaResponseDTO(

        Long id,
        String descricao,
        BigDecimal valorTotal,
        TipoDivisao tipoDivisao,
        StatusDespesa statusDespesa,
        LocalDateTime dataCriacao,
        Long idPagador,
        List<DivisaoResponseDTO> divisoes
) {

    public static DespesaResponseDTO fromDespesa(Despesa despesa)
    {
        return new DespesaResponseDTO(despesa.getId(), despesa.getDescricao(), despesa.getValorTotal(),
                despesa.getTipoDivisao(),despesa.getStatusDespesa(),despesa.getDataCriacao(), despesa.getId(),
                despesa.getDivisoes().stream().map(DivisaoResponseDTO::fromDivisao).toList());
    }
}
