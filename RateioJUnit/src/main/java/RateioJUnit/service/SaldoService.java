package RateioJUnit.service;

import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.dto.saldo.ResumoSaldoTotalResponseDTO;
import RateioJUnit.dto.saldo.SaldoResponseDTO;
import RateioJUnit.dto.saldo.SaldoTotalEntreParticipantesResponseDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;
import RateioJUnit.repository.SaldoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaldoService {

    private final ParticipanteService participanteService;
    private final SaldoRepository saldoRepository;

    public List<SaldoResponseDTO> listarTodosOsSaldos()
    {
        List<Saldo> saldos = saldoRepository.findAll();

        if(saldos.isEmpty())
        {
            throw new NenhumRegistroException("Nenhum registro foi encontrado");
        }

        return saldos.stream().map(SaldoResponseDTO::fromSaldo).toList();
    }

    public List<SaldoResponseDTO> listarPorParticipante(Long idParticipante)
    {
        participanteService.buscarID(idParticipante);

        List<Saldo> saldos = this.saldoRepository.findByCredorIdOrDevedorId(idParticipante, idParticipante);
        if(saldos.isEmpty())
        {
            throw new NenhumRegistroException("Nenhum registro foi encontrado");
        }

        return saldos.stream().map(SaldoResponseDTO::fromSaldo).toList();
    }

    public ResumoSaldoTotalResponseDTO saldoTotalUsuario(Long idUsuario)
    {
        participanteService.buscarID(idUsuario);

        BigDecimal totalReceber = this.saldoRepository.findByCredorId(idUsuario)
                .stream().map(Saldo::getValor).reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalDever = this.saldoRepository.findByDevedorId(idUsuario)
                .stream().map(Saldo::getValor).reduce(BigDecimal.ZERO,BigDecimal::add);

        return new ResumoSaldoTotalResponseDTO(totalReceber,totalDever);
    }


    public SaldoTotalEntreParticipantesResponseDTO saldoTotalEntreParticipante(Long idDevedor, Long idCredor)
    {
        Participante credor = participanteService.buscarID(idCredor);
        Participante devedor = participanteService.buscarID(idDevedor);

        BigDecimal saldoTotal = this.saldoRepository.findByCredorIdAndDevedorId(idCredor,idDevedor)
                .stream().map(Saldo::getValor).reduce(BigDecimal.ZERO,BigDecimal::add);

        return new SaldoTotalEntreParticipantesResponseDTO(credor.getId(), credor.getNome(), devedor.getId(), devedor.getNome(), saldoTotal);
    }

    //-------------- METODOS AUXILIARES --------------

    //Metodo utilizando no service de despesa para finalizar uma despesa
    public void calcularSaldo(Despesa despesa)
    {
        Participante pagador = despesa.getPagador();

        List<Saldo> saldos = new ArrayList<>();

        for(Divisao divisao : despesa.getDivisoes())
        {
            Participante participante = divisao.getParticipante();
            BigDecimal valorDivisao = divisao.getValor();

            //Pagador não cobrar de si mesmo
            if(participante.getId().equals(pagador.getId()))
            {
                continue;
            }

            Saldo saldo = new Saldo();

            saldo.setValor(valorDivisao);
            saldo.setDevedor(participante);
            saldo.setCredor(pagador);
            saldo.setDespesa(despesa);

            saldos.add(saldo);
        }

        saldoRepository.saveAll(saldos);
    }

    //Metodo utilizando no service de despesa para remoção de despesa
    public void removeSaldo(Despesa despesa)
    {
        for(Divisao divisao: despesa.getDivisoes())
        {
            Participante participante = divisao.getParticipante();

            participante.getSaldoCredor().removeIf(saldo->saldo.getDespesa().getId().equals(despesa.getId()));
            participante.getSaldoDevedor().removeIf(saldo -> saldo.getDespesa().getId().equals(despesa.getId()));
        }
    }

}
