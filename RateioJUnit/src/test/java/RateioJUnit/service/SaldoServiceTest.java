package RateioJUnit.service;

import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.dto.saldo.SaldoResponseDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.factory.DespesaFactory;
import RateioJUnit.factory.ParticipanteFactory;
import RateioJUnit.repository.SaldoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaldoServiceTest {

    @Mock
    ParticipanteService participanteService;

    @Mock
    SaldoRepository saldoRepository;

    @InjectMocks
    SaldoService saldoService;


    // --- METODO PARA LISTAR TODOS OS SALDOS ---

    @Test
    void deveListarTodosOsSaldos()
    {
        Participante devedor = ParticipanteFactory.criarParticipante();
        Participante credor = ParticipanteFactory.criarParticipante();
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"), StatusDespesa.CRIADA, TipoDivisao.IGUAL);

        Saldo saldoUm = new Saldo();
        saldoUm.setId(1L);
        saldoUm.setValor(new BigDecimal("100.00"));
        saldoUm.setDevedor(devedor);
        saldoUm.setCredor(credor);
        saldoUm.setDespesa(despesa);

        Saldo saldoDois = new Saldo();
        saldoDois.setId(2L);
        saldoDois.setValor(new BigDecimal("100.00"));
        saldoDois.setDevedor(devedor);
        saldoDois.setCredor(credor);
        saldoDois.setDespesa(despesa);

        when(this.saldoRepository.findAll()).thenReturn(List.of(saldoUm,saldoDois));

        List<SaldoResponseDTO>responseDTO = this.saldoService.listarTodosOsSaldos();
        assertNotNull(responseDTO);

        assertEquals(2,responseDTO.size());

        verify(this.saldoRepository).findAll();
    }

    @Test
    void deveLancarExcecaoCasoAlistaEstejaVazia()
    {
        when(this.saldoRepository.findAll()).thenReturn(List.of());

        assertThrows(NenhumRegistroException.class,()->this.saldoService.listarTodosOsSaldos());

        verify(saldoRepository).findAll();
    }
}
