package RateioJUnit.service;

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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    // --- ADICIONAR DESPESA ---

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
    void deveLancarExcecaoQuandoSomaDiferenteTotal()
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

        verify(participanteRepository).findById(1L);
        verify(participanteRepository).findById(2L);
    }

    // --- ATUALIZAR DESPESA ---

    @Test
    void deveAtualizarDespesa()
    {
        Long idDespesa = 1L;
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");
        Despesa despesa =
                DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        Divisao divisao = new Divisao();
        divisao.setParticipante(participante);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaUpdt = new DespesaUpdtDto("Compra pizza",1L);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));
        when(participanteService.buscarID(idDespesa)).thenReturn(participante);

        DespesaResponseDTO response = despesaService.atualizarDespesa(idDespesa,despesaUpdt);
        assertEquals("Compra pizza",response.descricao());
        assertEquals(participante.getId(),response.idPagador());

        verify(despesaRepository).save(despesa);
    }

    @Test
    void deveLancarExcecaoQuandoPagadorNaoEstiverNaListaNoMetodoDeAtualizarDespesa()
    {
        Long idDespesa = 1L;
        Participante participanteLista = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");
        Participante novoPagador = ParticipanteFactory.criarParticipantePersonalizado(2L,"Anderson");
        Despesa despesa =
                DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);
        Divisao divisao = new Divisao();
        divisao.setParticipante(participanteLista);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaUpdt = new DespesaUpdtDto("Compra pizza",2L);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));
        when(participanteService.buscarID(2L)).thenReturn(novoPagador);

        assertThrows(PagadorNaoEstaNaListaException.class,()->despesaService.atualizarDespesa(idDespesa,despesaUpdt));

        verify(despesaRepository).findById(idDespesa);
        verify(participanteService).buscarID(2L);
        verify(despesaRepository,never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoStatusEstiverIncorretoParaAtualizarDespesa()
    {
        Long idDespesa = 1L;
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(1L,"Kaique");
        Despesa despesa =
                DespesaFactory.criarDespesa(1L,new BigDecimal("500.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        Divisao divisao = new Divisao();
        divisao.setParticipante(participante);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaReqDTO = new DespesaUpdtDto("Compra teclado",1L);

        when(participanteService.buscarID(1L)).thenReturn(participante);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        assertThrows(DespesaEmStatusInicialException.class,()->despesaService.atualizarDespesa(1L,despesaReqDTO));

        verify(despesaRepository).findById(idDespesa);
        verify(participanteService).buscarID(1L);
        verify(despesaRepository,never()).save(any(Despesa.class));
    }

    @Test
    void deveLancarExcecaoQuandoDespesaNaoEncontrada()
    {
        Long idDespesa = 2L;

        DespesaUpdtDto despesaRequestDTO =
                new DespesaUpdtDto("Compra Telefone",1L);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());

        IdNaoEncontradoException exception =
                assertThrows(IdNaoEncontradoException.class,()->despesaService.atualizarDespesa(idDespesa,despesaRequestDTO));
        assertEquals("Despesa não encontrada",exception.getMessage());

        verify(despesaRepository).findById(idDespesa);
        verify(participanteService,never()).buscarID(anyLong());
        verify(despesaRepository,never()).save(any(Despesa.class));
    }

    // ---LISTAR TODAS AS DESPESAS ---

    @Test
    void deveListarTodasDespesas()
    {
        Despesa despesaUm = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        Despesa despesaDois = DespesaFactory.criarDespesa(2L,new BigDecimal("200.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findAll()).thenReturn(List.of(despesaUm,despesaDois));

        List<DespesaResponseDTO> response = despesaService.listarTodasDespesas();
        assertNotNull(response);
        assertEquals(2,response.size());

        verify(despesaRepository).findAll();
    }

    @Test
    void deveLancarExcecaoQuandoNaoTiverDespesas()
    {
        when(despesaRepository.findAll()).thenReturn(List.of());
        assertThrows(NenhumRegistroException.class,()->despesaService.listarTodasDespesas());
        verify(despesaRepository).findAll();
    }

    // ---LISTAR DEPSESA POR ID ---

    @Test
    void deveBuscarDespesaPorId()
    {
        Long idDespesa = 1L;
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        DespesaResponseDTO response = despesaService.buscarDespesaPorId(idDespesa);
        assertEquals(despesa.getId(),response.id());
        assertEquals(despesa.getDescricao(),response.descricao());
        assertEquals(despesa.getValorTotal(),response.valorTotal());
        assertEquals(despesa.getStatusDespesa(),response.statusDespesa());
        assertEquals(despesa.getTipoDivisao(),response.tipoDivisao());

        verify(despesaRepository).findById(idDespesa);
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoEncontrado()
    {
        Long idDespesa = 1L;
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());
        assertThrows(IdNaoEncontradoException.class,()->despesaService.buscarDespesaPorId(idDespesa));
        verify(despesaRepository).findById(idDespesa);
    }

    // ---LISTAR DESPESA POR STATUS ---

    @Test
    void deveBuscarDespesaPorStatus()
    {
        StatusDespesa statusDespesa = StatusDespesa.FINALIZADA;
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByStatusDespesa(statusDespesa)).thenReturn(List.of(despesa));

        List<DespesaResponseDTO> response = despesaService.listarDespesaPorStatus(statusDespesa);
        assertNotNull(response);
        assertEquals(despesa.getStatusDespesa(),response.getFirst().statusDespesa());
        assertEquals(despesa.getValorTotal(),response.getFirst().valorTotal());
        assertEquals(despesa.getDescricao(),response.getFirst().descricao());

        verify(despesaRepository).findByStatusDespesa(statusDespesa);
    }

    @Test
    void deveLancarExcecaoCasoDespesaNaoEncontrada()
    {
        StatusDespesa statusDespesa = StatusDespesa.FINALIZADA;
        when(despesaRepository.findByStatusDespesa(statusDespesa)).thenReturn(List.of());
        assertThrows(DespesaInexistenteException.class,()->despesaService.listarDespesaPorStatus(statusDespesa));
        verify(despesaRepository).findByStatusDespesa(statusDespesa);
    }

    // --- BUSCAR DESPESA POR TIPO DE DIVISÃO ---

    @Test
    void deveBuscarDespesaPorTipoDeDivisao()
    {
        TipoDivisao tipoDivisao = TipoDivisao.IGUAL;
        Despesa despesaUm = DespesaFactory.criarDespesa(1L,new BigDecimal("20.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        Despesa despesaDois = DespesaFactory.criarDespesa(2L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByTipoDivisao(tipoDivisao)).thenReturn(List.of(despesaUm,despesaDois));

        List<DespesaResponseDTO> despesaResponse = despesaService.listarDespesaPorTipoDivisao(tipoDivisao);
        assertEquals(2,despesaResponse.size());
        assertEquals(despesaUm.getTipoDivisao(),despesaResponse.getFirst().tipoDivisao());
        assertEquals(despesaDois.getTipoDivisao(),despesaResponse.getFirst().tipoDivisao());

        verify(despesaRepository).findByTipoDivisao(tipoDivisao);
    }


    @Test
    void deveLancarExcecaoQuandoTipoDeDivisaoDeDespesaNaoEncontrada()
    {
        TipoDivisao tipoDivisao = TipoDivisao.PERSONALIZADA;
        when(despesaRepository.findByTipoDivisao(tipoDivisao)).thenReturn(List.of());
        assertThrows(DespesaInexistenteException.class,()->despesaService.listarDespesaPorTipoDivisao(tipoDivisao));
        verify(despesaRepository).findByTipoDivisao(tipoDivisao);
    }

    // --- FINALIZAR DESPESA ---

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
        verify(despesaRepository).findById(1L);
    }

    @Test
    void naoDeveFinalizarDespesaCancelada()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("500.00"), StatusDespesa.CANCELADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        assertThrows(DespesaCanceladaException.class,()->despesaService.finalizacaoDespesa(1L));
        verify(despesaRepository).findById(1L);
    }

    // --- CANCELAR DESPESA ---

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
    void deveLancarExcecaoQuandoIdDeDespesaNaoEncontrado()
    {
        Long idDespesa = 2L;

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());

        assertThrows(IdNaoEncontradoException.class,()->despesaService.cancelarDespesa(idDespesa));

        verify(despesaRepository).findById(idDespesa);
        verify(despesaRepository,never()).save(despesa);
    }

    @Test
    void naoDeveCancelarDespesaFinalizada()
    {
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("1500.00"), StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.finalizacaoDespesa(1L));
        verify(despesaRepository).findById(1L);
    }

    // --- BUSCA ENTRE DATAS ---

    @Test
    void deveBuscarDespesaEntreDatas()
    {
        LocalDate dataInicial = LocalDate.of(2026,1,1);
        LocalDate dataFinal = LocalDate.of(2026,2,2);

        LocalDateTime dataInicialFormatada = dataInicial.atStartOfDay();
        LocalDateTime dataFinalFormatada  = dataFinal.atTime(LocalTime.MAX);

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada)).thenReturn(List.of(despesa));

        List<DespesaResponseDTO> response = despesaService.buscarEntreDatas(dataInicial,dataFinal);
        assertEquals(1,response.size());
        assertEquals(despesa.getDescricao(),response.getFirst().descricao());
        assertEquals(despesa.getValorTotal(),response.getFirst().valorTotal());

        verify(despesaRepository).findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada);

    }

    @Test
    void deveLancarExcecaoQuandoRetornaListaVazia()
    {
        LocalDate dataInicial = LocalDate.of(2026,1,1);
        LocalDate dataFinal = LocalDate.of(2026,2,2);

        LocalDateTime dataInicialFormatada = dataInicial.atStartOfDay();
        LocalDateTime dataFinalFormatada  = dataFinal.atTime(LocalTime.MAX);

        when(despesaRepository.findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada)).thenReturn(List.of());

        assertThrows(NenhumRegistroException.class,()->despesaService.buscarEntreDatas(dataInicial,dataFinal));

        verify(despesaRepository).findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada);
    }

    @Test
    void deveLancarExcecaoQuandoDataFinalMenorQueDataInicial()
    {
        LocalDate dataInicial = LocalDate.of(2026,1,10);
        LocalDate dataFinal = LocalDate.of(2025,5,30);

        assertThrows(DataExcpetion.class,()->despesaService.buscarEntreDatas(dataInicial,dataFinal));

        verify(despesaRepository,never()).findByDataCriacaoBetween(any(LocalDateTime.class),any(LocalDateTime.class));
    }
}
