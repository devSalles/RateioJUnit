package RateioJUnit.service;

import RateioJUnit.core.exception.participante.ParticipanteInvalidoException;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.core.exception.despesa.*;
import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.despesa.DespesaResponseDTO;
import RateioJUnit.dto.despesa.DespesaUpdtDto;
import RateioJUnit.dto.divisao.DivisaoRequestDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final ParticipanteService participanteService;
    private final ParticipanteRepository participanteRepository;
    private final SaldoService saldoService;

    @Transactional
    public DespesaResponseDTO adicionarDespesa(DespesaRequestDTO despesaRequestDTO)
    {
        if(despesaRequestDTO.valorTotal() == null || despesaRequestDTO.valorTotal().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorTotalInvalidoException();
        }

        Participante pagador = participanteService.buscarID(despesaRequestDTO.idPagador());

        List<Participante> pagadores = despesaRequestDTO.participantes()
                .stream()
                .map(divDto ->
                {
                    if (divDto == null) {
                        throw new ParticipanteInvalidoException();
                    }

                    return this.participanteRepository.findById(divDto.idParticipante())
                            .orElseThrow(() -> new IdNaoEncontradoException("ID de pagador não encontrado"));
                })
                .toList();

        boolean pagadorNaLista = pagadores.stream().anyMatch(p -> p.getId().equals(pagador.getId()));
        if (!pagadorNaLista)
        {
            throw new PagadorNaoEstaNaListaException();
        }

        long participanteDistintos = pagadores.stream().map(Participante::getId).distinct().count();
        if (participanteDistintos != pagadores.size())
        {
            throw new ParticipantesDuplicadosException();
        }

        Despesa despesa = despesaRequestDTO.toDespesa();
        despesa.setPagador(pagador);

        if (despesaRequestDTO.tipoDivisao() == TipoDivisao.IGUAL)
        {
            AplicarDivisaoIgual(despesa, pagadores);
        }
        else if (despesaRequestDTO.tipoDivisao() == TipoDivisao.PERSONALIZADA)
        {
            AplicarDivisaoPersonalizada(despesa, pagadores, despesaRequestDTO.participantes());
        }
        else
        {
            throw new DespesaInexistenteException();
        }

        BigDecimal somaValorDiv = despesa.getDivisoes().stream().map(Divisao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (somaValorDiv.compareTo(despesa.getValorTotal()) != 0)
        {
            throw new DiferenteValorTotalException();
        }

        this.despesaRepository.save(despesa);

        return DespesaResponseDTO.fromDespesa(despesa);
    }

    @Transactional
    public DespesaResponseDTO atualizarDespesa(Long idDespesa, DespesaUpdtDto despesaUpdtDto)
    {
        Despesa despesaID = buscarID(idDespesa);

        Participante pagador = this.participanteService.buscarID(despesaUpdtDto.idPagador());

        if (despesaID.getStatusDespesa() != StatusDespesa.CRIADA)
        {
            throw new DespesaEmStatusInicialException();
        }

        boolean pagadorNaLista = despesaID.getDivisoes()
                .stream().anyMatch(d -> d.getParticipante().getId().equals(pagador.getId()));

        if(!pagadorNaLista)
        {
            throw new PagadorNaoEstaNaListaException();
        }

        despesaID.setDescricao(despesaUpdtDto.descricao());
        despesaID.setPagador(pagador);

        this.despesaRepository.save(despesaID);
        return DespesaResponseDTO.fromDespesa(despesaID);
    }

    @Transactional
    public DespesaResponseDTO finalizacaoDespesa(Long idDespesa)
    {
        Despesa despesaID = buscarID(idDespesa);

        validarFinalizacaoDespesa(despesaID);
        saldoService.calcularSaldo(despesaID);
        despesaID.setStatusDespesa(StatusDespesa.FINALIZADA);

        this.despesaRepository.save(despesaID);
        return DespesaResponseDTO.fromDespesa(despesaID);
    }

    public List<DespesaResponseDTO> listarTodasDespesas()
    {
        List<Despesa> despesas = this.despesaRepository.findAll();

        if (despesas.isEmpty())
        {
            throw new NenhumRegistroException("Nenhum registro foi encontrado");
        }

        return despesas.stream().map(DespesaResponseDTO::fromDespesa).toList();
    }

    public List<DespesaResponseDTO> listarDespesaPorStatus(StatusDespesa statusDespesa)
    {
        List<Despesa> despesaStatus = this.despesaRepository.findByStatusDespesa(statusDespesa);

        if (despesaStatus.isEmpty())
        {
            throw new DespesaInexistenteException();
        }

        return despesaStatus.stream().map(DespesaResponseDTO::fromDespesa).toList();
    }

    public List<DespesaResponseDTO> listarDespesaPorTipoDivisao(TipoDivisao tipoDivisao)
    {
        List<Despesa> despesaTipoDivisao = this.despesaRepository.findByTipoDivisao(tipoDivisao);

        if (despesaTipoDivisao.isEmpty())
        {
            throw new DespesaInexistenteException();
        }

        return despesaTipoDivisao.stream().map(DespesaResponseDTO::fromDespesa).toList();
    }

    public List<DespesaResponseDTO> buscarEntreDatas(LocalDate dataInicial, LocalDate dataFinal)
    {
        if(dataFinal.isBefore(dataInicial))
        {
            throw new DataExcpetion();
        }

        LocalDateTime dataInicialFormatada = dataInicial.atStartOfDay();
        LocalDateTime dataFinalFormatada = dataFinal.atTime(LocalTime.MAX);

        List<Despesa> despesasDatas = this.despesaRepository.findByDataCriacaoBetween(dataInicialFormatada, dataFinalFormatada);

        if(despesasDatas.isEmpty())
        {
            throw new NenhumRegistroException("Nenhum registro foi encontrado com essas datas");
        }

        return despesasDatas.stream().map(DespesaResponseDTO::fromDespesa).toList();
    }

    public DespesaResponseDTO buscarDespesaPorId(Long idDespesa)
    {
        Despesa despesa = buscarID(idDespesa);
        return DespesaResponseDTO.fromDespesa(despesa);
    }

    @Transactional
    public DespesaResponseDTO cancelarDespesa(Long idDespesa)
    {
        Despesa despesa = buscarID(idDespesa);

        validarCancelamentoDespesa(despesa);
        saldoService.removeSaldo(despesa);

        despesa.setStatusDespesa(StatusDespesa.CANCELADA);

        this.despesaRepository.save(despesa);
        return DespesaResponseDTO.fromDespesa(despesa);
    }

    //-------------- METODOS AUXILIARES --------------

    public Despesa buscarID(Long idDespesa)
    {
        return this.despesaRepository.findById(idDespesa)
                .orElseThrow(() -> new IdNaoEncontradoException("Despesa não encontrada"));
    }

        private void validarFinalizacaoDespesa(Despesa despesa)
    {
        if(despesa.getStatusDespesa() == StatusDespesa.FINALIZADA)
        {
            throw new DespesaJaFinalizadaException("Despesa já finalizada");
        }

        if(despesa.getStatusDespesa() == StatusDespesa.CANCELADA)
        {
            throw new DespesaCanceladaException("Despesa cancelada não pode ser finalizada");
        }
    }

    private void validarCancelamentoDespesa(Despesa despesa)
    {
        if(despesa.getStatusDespesa() == StatusDespesa.CANCELADA)
        {
            throw new DespesaCanceladaException("A despesa já foi cancelada");
        }

        if(despesa.getStatusDespesa() == StatusDespesa.FINALIZADA)
        {
            throw new DespesaJaFinalizadaException("A despesa finalizada não pode ser cancelada");
        }
    }

    private void AplicarDivisaoIgual(Despesa despesa, List<Participante> participantes)
    {
        BigDecimal total = despesa.getValorTotal();
        int quantidade = participantes.size();

        BigDecimal valorBase = total.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.DOWN);
        BigDecimal totalDistribuido = valorBase.multiply(BigDecimal.valueOf(quantidade));

        BigDecimal resto = total.subtract(totalDistribuido);

        List<Divisao> divisoes = new ArrayList<>();

        for(int i = 0; i < participantes.size(); i++)
        {
            BigDecimal valorFinal = valorBase;

            if (resto.compareTo(BigDecimal.ZERO) > 0)
            {
                valorFinal = valorFinal.add(new BigDecimal("0.01"));
                resto = resto.subtract(new BigDecimal("0.01"));
            }

            Divisao divisao = new Divisao();
            divisao.setValor(valorFinal);
            divisao.setParticipante(participantes.get(i));
            divisao.setDespesa(despesa);
            divisoes.add(divisao);
        }

        despesa.setDivisoes(divisoes);
    }

    public void AplicarDivisaoPersonalizada(Despesa despesa, List<Participante> participantes,
                                            List<DivisaoRequestDTO> participantesDTO)
    {
        List<Divisao> divisoes = new ArrayList<>();

        for (Participante participante : participantes)
        {
            DivisaoRequestDTO dto = participantesDTO.stream()
                    .filter(d -> d.idParticipante().equals(participante.getId()))
                    .findFirst().orElseThrow(() -> new IdNaoEncontradoException("ID de participante não encontrado"));

            BigDecimal valor = dto.valor();

            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
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