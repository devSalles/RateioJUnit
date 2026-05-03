package RateioJUnit.controller;

import RateioJUnit.dto.usuario.ParticipanteResponseDTO;
import RateioJUnit.dto.usuario.ParticipanteResquestDTO;
import RateioJUnit.service.ParticipanteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participante")
@RequiredArgsConstructor
@Tag(name = "Participante")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @PostMapping("/salvar-participante")
    public ParticipanteResponseDTO salvarParticipante(@Valid @RequestBody ParticipanteResquestDTO dto) {
        return participanteService.salvarParticipante(dto);
    }

    @PutMapping("/atualizar-participante/{idParticipante}")
    public ParticipanteResponseDTO atualizarParticipante(@PathVariable Long idParticipante,@Valid @RequestBody ParticipanteResquestDTO dto) {
        return participanteService.atualizarParticipante(idParticipante, dto);
    }

    @GetMapping("/buscar-detalhes/{idParticipante}")
    public ParticipanteResponseDTO buscarIDParticipante(@PathVariable Long idParticipante) {
        return participanteService.buscarIDParticipante(idParticipante);
    }

    @GetMapping("/buscar-todos-participantes")
    public List<ParticipanteResponseDTO> buscarTodosParticipantes() {
        return participanteService.buscarTodosParticipantes();
    }

    @GetMapping("/buscar-por-email")
    public ParticipanteResponseDTO buscarPorEmail(@RequestParam String email) {
        return participanteService.buscarPorEmail(email);
    }

    @GetMapping("/buscar-por-nome")
    public List<ParticipanteResponseDTO> buscarPorNome(@RequestParam String nomeParticipante) {
        return participanteService.buscarPorNome(nomeParticipante);
    }

    @DeleteMapping("/deletar-participante/{idParticipante}")
    public ParticipanteResponseDTO deletarParticipante(@PathVariable Long idParticipante) {
        return participanteService.deletarParticipante(idParticipante);
    }
}