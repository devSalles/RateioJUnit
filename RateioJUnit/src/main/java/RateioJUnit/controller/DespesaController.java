package RateioJUnit.controller;

import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.service.DespesaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Despesa")
@RequiredArgsConstructor
@Tag(name = "Despesa")
public class DespesaController {

    private final DespesaService despesaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> adicionarDespesa(@RequestBody @Valid DespesaRequestDTO despesaRequestDTO)
    {
        return ResponseEntity.ok(despesaService.adicionarDespesa(despesaRequestDTO));
    }
}
