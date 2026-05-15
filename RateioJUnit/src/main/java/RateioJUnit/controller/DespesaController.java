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

import java.util.List;

@RestController
@RequestMapping("/Despesa")
@RequiredArgsConstructor
@Tag(name = "Despesa")
public class DespesaController {

    private final DespesaService despesaService;

    @PostMapping("/adicionar-despesa")
    public ResponseEntity<?> adicionarDespesa(@RequestBody @Valid DespesaRequestDTO despesaRequestDTO) {
        return ResponseEntity.ok(despesaService.adicionarDespesa(despesaRequestDTO));
    }

    @PutMapping("/atualizar-despesa/{id}")
    public ResponseEntity<DespesaResponseDTO> atualizarDespesa(@PathVariable Long id,
            @RequestBody @Valid DespesaRequestDTO despesaRequestDTO) {
        return ResponseEntity.ok(despesaService.atualizarDespesa(id, despesaRequestDTO));
    }

    @GetMapping("buscar-todas-despesa")
    public ResponseEntity<List<DespesaResponseDTO>> listarTodasDespesas() {
        return ResponseEntity.ok(despesaService.listarTodasDespesas());
    }

    @GetMapping("buscar-despesa-especifica/{id}")
    public ResponseEntity<DespesaResponseDTO> buscarDespesaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despesaService.buscarDespesaPorId(id));
    }
}