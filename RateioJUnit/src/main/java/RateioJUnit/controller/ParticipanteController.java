package RateioJUnit.controller;

import RateioJUnit.dto.usuario.ParticipanteResponseDTO;
import RateioJUnit.dto.usuario.ParticipanteResquestDTO;
import RateioJUnit.service.ParticipanteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participante")
@RequiredArgsConstructor
@Tag(name = "Participante")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @PostMapping("/adicionar-participante")
    public ResponseEntity<ParticipanteResponseDTO> salvarParticipante(@Valid @RequestBody ParticipanteResquestDTO dto) {

        return ResponseEntity.ok(participanteService.salvarParticipante(dto));
    }

    @PutMapping("/atualizar-participante/{idParticipante}")
    public ResponseEntity<ParticipanteResponseDTO> atualizarParticipante(@PathVariable Long idParticipante, @Valid @RequestBody ParticipanteResquestDTO dto) {

        return ResponseEntity.ok(participanteService.atualizarParticipante(idParticipante, dto));
    }

    @GetMapping("/buscar-detalhes/{idParticipante}")
    public ResponseEntity<ParticipanteResponseDTO> buscarIDParticipante(@PathVariable Long idParticipante) {

        return ResponseEntity.ok(participanteService.buscarIDParticipante(idParticipante));
    }

    @GetMapping("/buscar-todos-participantes")
    public ResponseEntity<List<ParticipanteResponseDTO>> buscarTodosParticipantes() {

        return ResponseEntity.ok(participanteService.buscarTodosParticipantes());
    }

    @GetMapping("/buscar-por-email")
    public ResponseEntity<ParticipanteResponseDTO> buscarPorEmail(@RequestParam String email) {

        return ResponseEntity.ok(participanteService.buscarPorEmail(email));
    }

    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<ParticipanteResponseDTO>> buscarPorNome(@RequestParam String nomeParticipante) {

        return ResponseEntity.ok(participanteService.buscarPorNome(nomeParticipante));
    }

    @DeleteMapping("/deletar-participante/{idParticipante}")
    public ResponseEntity<ParticipanteResponseDTO> deletarParticipante(@PathVariable Long idParticipante) {

        return ResponseEntity.ok(participanteService.deletarParticipante(idParticipante));
    }
}