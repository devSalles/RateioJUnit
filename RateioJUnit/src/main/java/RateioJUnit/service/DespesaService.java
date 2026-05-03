package RateioJUnit.service;

import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DespesaService {


    private final DespesaRepository despesaRepository;
    private final ParticipanteService participanteService;


    public DespesaResponseDTO adicionarDespesa(DespesaRequestDTO despesaRequestDTO)
    {
        Participante pagador = participanteService.buscarID(despesaRequestDTO.idPagador());

        Despesa despesa = despesaRequestDTO.toDespesa(pagador);

    }
}
