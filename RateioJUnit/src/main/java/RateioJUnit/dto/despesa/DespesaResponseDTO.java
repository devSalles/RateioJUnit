package RateioJUnit.dto.despesa;

import RateioJUnit.dto.divisao.DivisaoResponseDTO;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.entity.Despesa;

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
                despesa.getTipoDivisao(),despesa.getStatusDespesa(),despesa.getDataCriacao(),
                despesa.getPagador().getId(),
                despesa.getDivisoes().stream().map(DivisaoResponseDTO::fromDivisao).toList());
    }
}
