package RateioJUnit.service;

import RateioJUnit.core.exception.despesa.*;
import RateioJUnit.dto.despesa.DespesaRequestDTO;
import RateioJUnit.dto.divisao.DivisaoRequestDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Participante;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.factory.DespesaFactory;
import RateioJUnit.factory.ParticipanteFactory;
import RateioJUnit.repository.DespesaRepository;
import RateioJUnit.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DespesaServiceTest {

    @Mock
    DespesaRepository despesaRepository;

    @Mock
    ParticipanteService participanteService;

    @Mock
    ParticipanteRepository participanteRepository;

    @Mock
    SaldoService saldoService;

    @InjectMocks
    DespesaService despesaService;

    @Test
    void deveAdicionarDespesa()
    {
        Participante Bernardo = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");
        Participante Pietro = ParticipanteFactory.criarParticipantePersonalizado(2L,"Pietro");

        when(participanteService.buscarID(1L)).thenReturn(Bernardo);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Bernardo));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(Pietro));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("50.00"));
        DivisaoRequestDTO d2 = new DivisaoRequestDTO(2L, new BigDecimal("50.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Pizza",new BigDecimal("100.00"),1L, List.of(d1,d2), TipoDivisao.PERSONALIZADA);

        despesaService.adicionarDespesa(despesaRequestDTO);

        verify(despesaRepository).save(any(Despesa.class));
    }

    @Test
    void deveLancarExcecaoQuandoPagadorNaoEstiverNaLista()
    {
        Participante Matheus = ParticipanteFactory.criarParticipantePersonalizado(1L,"Matheus");
        Participante Helena = ParticipanteFactory.criarParticipantePersonalizado(2L,"Helena");

        when(participanteService.buscarID(1L)).thenReturn(Matheus);
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(Helena));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(2L, new BigDecimal("50.00"));

        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Queijo",new BigDecimal("100.00"),1L,List.of(d1),TipoDivisao.PERSONALIZADA);

        assertThrows(PagadorNaoEstaNaListaException.class, ()-> despesaService.adicionarDespesa(despesaDTO));
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteDuplicado()
    {
        Participante Lima = ParticipanteFactory.criarParticipantePersonalizado(1L,"Lima");

        when(participanteService.buscarID(1L)).thenReturn(Lima);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Lima));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("1000.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Compra telefone",new BigDecimal("2000.00"),1L,List.of(d1,d1), TipoDivisao.PERSONALIZADA);

        assertThrows(ParticipantesDuplicadosException.class, () -> despesaService.adicionarDespesa(despesaRequestDTO));
    }

    @Test
    void DeveLancarExcecaoQuandoSomaDiferenteTotal()
    {
        Participante Victor = ParticipanteFactory.criarParticipantePersonalizado(1L,"Victor");
        Participante Anderson = ParticipanteFactory.criarParticipantePersonalizado(2L,"Anderson");

        when(participanteService.buscarID(1L)).thenReturn(Victor);

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Victor));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(Anderson));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("5000.00"));
        DivisaoRequestDTO d2 = new DivisaoRequestDTO(2L,new BigDecimal("3000.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Viagem",new BigDecimal("9000.00"),1L,List.of(d1,d2), TipoDivisao.PERSONALIZADA);
        
        assertThrows(DiferenteValorTotalException.class,()-> despesaService.adicionarDespesa(despesaRequestDTO));
    }

    @Test
    void deveFinalizarDespesa()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"), StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        despesaService.finalizacaoDespesa(1L);

        assertEquals(StatusDespesa.FINALIZADA,despesa.getStatusDespesa());

        verify(saldoService).calcularSaldo(despesa);
    }

    @Test
    void naoDeveFinalizarDespesaJaFinalizada()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("60.00"), StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.finalizacaoDespesa(1L));
    }

    @Test
    void naoDeveFinalizarDespesaCancelada()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("500.00"), StatusDespesa.CANCELADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        assertThrows(DespesaCanceladaException.class,()->despesaService.finalizacaoDespesa(1L));
    }

    @Test
    void deveCancelarDespesa()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10000.00"), StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        despesaService.cancelarDespesa(1L);

        assertEquals(StatusDespesa.CANCELADA,despesa.getStatusDespesa());

        verify(saldoService).removeSaldo(despesa);
    }

    @Test
    void naoDeveCancelarDespesaFinalizada()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("1500.00"), StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.finalizacaoDespesa(1L));
    }

    @Test
    void deveLancarExcecaoQuandoDataFinalMenorQueDataInicial()
    {
        LocalDate dataInicial = LocalDate.of(2026,1,10);
        LocalDate dataFinal = LocalDate.of(2025,5,30);

        assertThrows(DataExcpetion.class,()->despesaService.buscarEntreDatas(dataInicial,dataFinal));
    }
}
