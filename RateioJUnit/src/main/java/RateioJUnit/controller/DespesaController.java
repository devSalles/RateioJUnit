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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @PutMapping("/finalizar-despesa/{id}")
    public ResponseEntity<DespesaResponseDTO> finalizarDespesa(@PathVariable Long id) {
        return ResponseEntity.ok(despesaService.finalizacaoDespesa(id));
    }

    @PutMapping("/cancelar-despesa/{id}")
    public ResponseEntity<DespesaResponseDTO> cancelarDespesa(@PathVariable Long id) {
        return ResponseEntity.ok(despesaService.cancelarDespesa(id));
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

    @GetMapping("/buscar-despesa-por-data")
    public ResponseEntity<?> buscarDespesaPorData(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fim
    )
    {
        return ResponseEntity.ok(this.despesaService.buscarEntreDatas(inicio, fim));
    }

    @GetMapping("/buscar-despesa-por-tipo-divisao")
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesaPorTipoDivisao(
            @RequestParam TipoDivisao tipoDivisao) {
        return ResponseEntity.ok(despesaService.listarDespesaPorTipoDivisao(tipoDivisao));
    }
}