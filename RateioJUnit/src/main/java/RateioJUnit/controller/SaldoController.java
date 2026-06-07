package RateioJUnit.controller;

import RateioJUnit.dto.saldo.ResumoSaldoTotalResponseDTO;
import RateioJUnit.dto.saldo.SaldoResponseDTO;
import RateioJUnit.dto.saldo.SaldoTotalEntreParticipantesResponseDTO;
import RateioJUnit.service.SaldoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/saldo")
@RequiredArgsConstructor
@Tag(name = "Saldo")
public class SaldoController {

    private final SaldoService saldoService;

    @GetMapping
    public ResponseEntity<List<SaldoResponseDTO>> listarTodosOsSaldos() {
        return ResponseEntity.ok(saldoService.listarTodosOsSaldos());
    }

    @GetMapping("/participante/{idParticipante}")
    public ResponseEntity<List<SaldoResponseDTO>> listarPorParticipante(@PathVariable Long idParticipante) {

        return ResponseEntity.ok(saldoService.listarPorParticipante(idParticipante));
    }

    @GetMapping("/participante/{idParticipante}/total")
    @Operation(summary = "Consultar saldo total do participante")
    public ResponseEntity<ResumoSaldoTotalResponseDTO> saldoTotalUsuario(@PathVariable Long idParticipante) {

        return ResponseEntity.ok(saldoService.saldoTotalUsuario(idParticipante));
    }

    @GetMapping("/entre-dois-participantes")
    @Operation(summary = "Consultar saldo entre dois participantes")
    public ResponseEntity<SaldoTotalEntreParticipantesResponseDTO> saldoTotalEntreParticipante(@RequestParam Long idCredor, @RequestParam Long idDevedor) {

        return ResponseEntity.ok(saldoService.saldoTotalEntreParticipante(idDevedor, idCredor));
    }
}
