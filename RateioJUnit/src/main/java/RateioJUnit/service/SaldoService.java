package RateioJUnit.service;

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

    private final SaldoRepository saldoRepository;

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

            saldos.add(saldo);
        }

        saldoRepository.saveAll(saldos);
    }

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
