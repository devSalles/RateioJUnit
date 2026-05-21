package RateioJUnit.dto.despesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DespesaUpdtDto(

        @NotBlank(message = "Descrição obrigatória")
        String descricao,

        @NotNull(message = "Pagador obrigatório")
        Long idPagador

) {
}
