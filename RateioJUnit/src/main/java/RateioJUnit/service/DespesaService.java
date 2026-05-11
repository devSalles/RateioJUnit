package RateioJUnit.service;

import RateioJUnit.ENUM.TipoDivisao;
import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.despesa.*;
import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.dto.despesa.DivisaoRequestDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;
import RateioJUnit.repository.DespesaRepository;
import RateioJUnit.repository.ParticipanteRepository;
import jakarta.transaction.Transactional;
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
    private final ParticipanteRepository participanteRepository;


    @Transactional
    public DespesaResponseDTO adicionarDespesa(DespesaRequestDTO despesaRequestDTO)
    {
        Participante pagador = participanteService.buscarID(despesaRequestDTO.idPagador());

        List<Participante> pagadores = despesaRequestDTO.participantes().stream()
                .map(divDto->this.participanteRepository.
                        findById(despesaRequestDTO.idPagador()).orElseThrow(()->new IdNaoEncontradoException("ID de pagador não encontrado")))
                .toList();

        boolean pagadorNaLista = pagadores.stream().anyMatch(p->p.getId().equals(pagador.getId()));
        if(!pagadorNaLista)
        {
            throw new PagadorNaoEstaNaListaException();
        }

        long participanteDistintos = pagadores.stream().map(Participante::getId).distinct().count();
        if(participanteDistintos != pagadores.size())
        {
            throw new ParticipantesDuplicadosException();
        }

        Despesa despesa = despesaRequestDTO.toDespesa();
        despesa.setPagador(pagador);

        if(despesaRequestDTO.tipoDivisao() == TipoDivisao.IGUAL)
        {
            AplicarDivisaoIgual(despesa,pagadores);
        }
        else if(despesaRequestDTO.tipoDivisao() == TipoDivisao.PERSONALIZADA)
        {
            AplicarDivisaoPersonalizada(despesa,pagadores,despesaRequestDTO.participantes());
        }
        else
        {
            throw new DespesaInexistenteException();
        }

        BigDecimal somaValorDiv = despesa.getDivisoes().stream().map(Divisao::getValor)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        if(somaValorDiv.compareTo(despesa.getValorTotal()) != 0)
        {
            throw new DiferenteValorTotalException();
        }

        this.despesaRepository.save(despesa);

        return DespesaResponseDTO.fromDespesa(despesa);
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
                                            List<DivisaoRequestDTO> participantesDTO)
    {
        List<Divisao> divisoes = new ArrayList<>();

        for(Participante participante:participantes)
        {
            DivisaoRequestDTO dto = participantesDTO.stream()
                    .filter(d->d.idParticipante().equals(participante.getId()))
                    .findFirst().orElseThrow(()->new IdNaoEncontradoException("ID de participante não encontrado"));

            BigDecimal valor = dto.valor();

            if(valor==null||valor.compareTo(BigDecimal.ZERO)<=0)
            {
                throw new ValorNegativoException();
            }

            Divisao divisao = new Divisao();
            divisao.setValor(valor);
            divisao.setParticipante(participante);
            divisao.setDespesa(despesa);

            divisoes.add(divisao);
        }

        despesa.setDivisoes(divisoes);
    }
}
