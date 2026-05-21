package RateioJUnit.controller;

import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.dto.despesa.DespesaUpdtDto;
import RateioJUnit.service.DespesaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
                                                               @RequestBody @Valid DespesaUpdtDto despesaUpdtDTO) {
        return ResponseEntity.ok(despesaService.atualizarDespesa(id, despesaUpdtDTO));
    }

    @GetMapping("/buscar-todas-despesa")
    public ResponseEntity<List<DespesaResponseDTO>> listarTodasDespesas() {
        return ResponseEntity.ok(despesaService.listarTodasDespesas());
    }

    @GetMapping("/buscar-despesa-especifica/{id}")
    public ResponseEntity<DespesaResponseDTO> buscarDespesaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despesaService.buscarDespesaPorId(id));
    }

    @GetMapping("/buscar-despesa-por-status")
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesaPorStatus(
            @RequestParam StatusDespesa statusDespesa) {
        return ResponseEntity.ok(despesaService.listarDespesaPorStatus(statusDespesa));
    }

    @GetMapping("/buscar-despesa-por-tipo-divisao")
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesaPorTipoDivisao(
            @RequestParam TipoDivisao tipoDivisao) {
        return ResponseEntity.ok(despesaService.listarDespesaPorTipoDivisao(tipoDivisao));
    }
}