package RateioJUnit.dto.despesa;

import RateioJUnit.dto.divisao.DivisaoRequestDTO;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.entity.Despesa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DespesaRequestDTO(

        @NotBlank(message = "Descrição obrigatória")
        String descricao,

        @NotNull(message = "Valor obrigatório") @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valorTotal,

        @NotNull(message = "Pagador obrigatório")
        Long idPagador,

        @NotNull(message = "Participantes são obrigatórios")
        @Size(min = 2,message = "Devem ser no mínimo 2 participantes por despesa")
        List<@Valid DivisaoRequestDTO>  participantes,

        @NotNull(message = "Tipo de divisão e obrigatória")
        TipoDivisao tipoDivisao
) {

    public Despesa toDespesa() {

        Despesa despesa = new Despesa();

        despesa.setDescricao(descricao);
        despesa.setValorTotal(valorTotal);
        despesa.setTipoDivisao(tipoDivisao);
        despesa.setStatusDespesa(StatusDespesa.CRIADA);
        despesa.setDataCriacao(LocalDateTime.now());

        return despesa;
    }
}
