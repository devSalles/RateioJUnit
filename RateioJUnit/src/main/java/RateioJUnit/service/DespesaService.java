package RateioJUnit.service;

import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;
import RateioJUnit.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespesaService {


    private final DespesaRepository despesaRepository;
    private final ParticipanteService participanteService;


    public DespesaResponseDTO adicionarDespesa(DespesaRequestDTO despesaRequestDTO)
    {
        Participante pagador = participanteService.buscarID(despesaRequestDTO.idPagador());

        Despesa despesa = despesaRequestDTO.toDespesa();
        despesa.setPagador(pagador);
    }


    // --------------- METODOS AUXILIARES ---------------

    private void AplicarDivisaoIgual(Despesa despesa, List<Participante> participantes)
    {
        BigDecimal total = despesa.getValorTotal();
        int quantidade = participantes.size();

        BigDecimal valorBase = total.divide(BigDecimal.valueOf(quantidade),2, RoundingMode.DOWN);
        BigDecimal totalDistribuido = valorBase.multiply(BigDecimal.valueOf(quantidade));

        BigDecimal resto = total.subtract(totalDistribuido);

        List<Divisao> divisoes = new ArrayList<>();

        for(int i=0;i<participantes.size();i++){
            BigDecimal valorFinal = valorBase;

            if(valorFinal.compareTo(BigDecimal.ZERO)>0)
            {
                valorFinal = valorFinal.add(new BigDecimal("0.01"));
                valorBase = resto.subtract(new BigDecimal("0.01"));
            }

            Divisao divisao = new Divisao();
            divisao.setValor(valorFinal);
            divisao.setParticipante(participantes.get(i));
            divisao.setDespesa(despesa);
            divisoes.add(divisao);
        }

        despesa.setDivisoes(divisoes);
    }

    public void AplicarDivisaoPersonalizada(Despesa despesa,  List<Participante> participantes ,
                                            List<DespesaRequestDTO> despesaRequestDTO)
    {
        
    }
}
