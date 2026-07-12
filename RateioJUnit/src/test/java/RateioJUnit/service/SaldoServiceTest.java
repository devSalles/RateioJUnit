package RateioJUnit.service;

import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.dto.saldo.ResumoSaldoTotalResponseDTO;
import RateioJUnit.dto.saldo.SaldoResponseDTO;
import RateioJUnit.dto.saldo.SaldoTotalEntreParticipantesResponseDTO;
import RateioJUnit.entity.Despesa;
import RateioJUnit.entity.Divisao;
import RateioJUnit.entity.Participante;
import RateioJUnit.entity.Saldo;
import RateioJUnit.enums.StatusDespesa;
import RateioJUnit.enums.TipoDivisao;
import RateioJUnit.factory.DespesaFactory;
import RateioJUnit.factory.ParticipanteFactory;
import RateioJUnit.repository.SaldoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaldoServiceTest {

    @Mock
    ParticipanteService participanteService;

    @Mock
    SaldoRepository saldoRepository;

    @InjectMocks
    SaldoService saldoService;

    @Captor
    private ArgumentCaptor<List<Saldo>> saldoCaptor;

    // --- LISTAR TODOS OS SALDOS ---

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

    // --- LISTAR SALDO POR PARTICIPANTE ---

    @Test
    void deveRetornarSaldoIndividualPorParticipante()
    {
        Long idParticipante = 1L;

        Participante devedor = ParticipanteFactory.criarParticipante();
        Participante credor = ParticipanteFactory.criarParticipante();
        Participante participante = ParticipanteFactory.criarParticipante();
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

        when(this.participanteService.buscarID(idParticipante)).thenReturn(participante);
        when(this.saldoRepository.findByCredorIdOrDevedorId(idParticipante,idParticipante)).thenReturn(List.of(saldoUm,saldoDois));

        List<SaldoResponseDTO> response = this.saldoService.listarPorParticipante(idParticipante);
        assertNotNull(response);
        assertEquals(2,response.size());
        assertEquals(new BigDecimal("100.00"),response.getFirst().valor());

        verify(participanteService).buscarID(idParticipante);
        verify(this.saldoRepository).findByCredorIdOrDevedorId(idParticipante,idParticipante);
    }

    @Test
    void deveLancarExcecaoCasoNaotenhaNenhumSaldoSaldo()
    {
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteService.buscarID(idParticipante)).thenReturn(participante);
        when(this.saldoRepository.findByCredorIdOrDevedorId(idParticipante,idParticipante)).thenReturn(List.of());

        assertThrows(NenhumRegistroException.class,()->this.saldoService.listarPorParticipante(idParticipante));

        verify(participanteService).buscarID(idParticipante);
        verify(saldoRepository).findByCredorIdOrDevedorId(idParticipante,idParticipante);
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistirAoListarSaldoPorParticipante()
    {
        Long idParticipante = 1L;

        when(participanteService.buscarID(idParticipante)).thenThrow(new IdNaoEncontradoException("Participante não encontrado"));

        IdNaoEncontradoException exception = assertThrows(IdNaoEncontradoException.class,()->this.saldoService.listarPorParticipante(idParticipante));
        assertEquals("Participante não encontrado",exception.getMessage());

        verify(participanteService).buscarID(idParticipante);
        verify(saldoRepository,never()).findByCredorIdAndDevedorId(anyLong(),anyLong());
    }

    // --- LISTAR SALDO TOTAL POR USUARIO ---

    @Test
    void deveRetornarOSaldoTotaLDoUsuario()
    {
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();
        when(participanteService.buscarID(idParticipante)).thenReturn(participante);

        Saldo saldoUm = new Saldo();
        saldoUm.setValor(new BigDecimal("100.00"));

        Saldo saldoDois = new Saldo();
        saldoDois.setValor(new BigDecimal("50.00"));

        Saldo saldoTres = new Saldo();
        saldoTres.setValor(new BigDecimal("150.00"));

        Saldo saldoQuatro = new Saldo();
        saldoQuatro.setValor(new BigDecimal("20.00"));

        when(saldoRepository.findByCredorId(idParticipante)).thenReturn(List.of(saldoUm,saldoDois));
        when(saldoRepository.findByDevedorId(idParticipante)).thenReturn(List.of(saldoTres,saldoQuatro));

        ResumoSaldoTotalResponseDTO response = saldoService.saldoTotalUsuario(idParticipante);
        assertNotNull(response);

        verify(participanteService).buscarID(idParticipante);
        verify(saldoRepository).findByCredorId(idParticipante);
        verify(saldoRepository).findByDevedorId(idParticipante);
    }

    @Test
    void deveRetornarZeroQuandoNenhumSaldo()
    {
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteService.buscarID(idParticipante)).thenReturn(participante);
        when(saldoRepository.findByCredorId(idParticipante)).thenReturn(List.of());
        when(saldoRepository.findByDevedorId(idParticipante)).thenReturn(List.of());

        ResumoSaldoTotalResponseDTO response = saldoService.saldoTotalUsuario(idParticipante);
        assertNotNull(response);

        verify(participanteService).buscarID(idParticipante);

        assertEquals(BigDecimal.ZERO,response.totalDever());
        assertEquals(BigDecimal.ZERO,response.totalReceber());
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistir()
    {
        Long idParticipante = 1L;

        when(participanteService.buscarID(idParticipante)).thenThrow(new IdNaoEncontradoException("Participante não encontrado"));

        IdNaoEncontradoException exception = assertThrows(IdNaoEncontradoException.class,()->saldoService.saldoTotalUsuario(idParticipante));
        assertEquals("Participante não encontrado",exception.getMessage());

        verify(participanteService).buscarID(idParticipante);
        verify(saldoRepository,never()).findByCredorId(anyLong());
        verify(saldoRepository,never()).findByDevedorId(anyLong());
    }

    // --- EXIBIR SALDO TOTAL ENTRE PARTICIPANTES ---

    @Test
    void deveExibirSaldoTotalEntreParticipantes()
    {
        Long idDevedor = 1L;
        Long idCredor = 2L;

        Participante credor = ParticipanteFactory.criarParticipante();
        credor.setId(idCredor);
        credor.setNome("Juanito");

        Participante devedor = ParticipanteFactory.criarParticipante();
        devedor.setId(idDevedor);
        devedor.setNome("Pietro");

        Saldo saldoUm = new Saldo();
        saldoUm.setValor(new BigDecimal("100.00"));

        Saldo saldoDois = new Saldo();
        saldoDois.setValor(new BigDecimal("50.00"));

        when(saldoRepository.findByCredorIdAndDevedorId(idCredor,idDevedor)).thenReturn(List.of(saldoUm,saldoDois));
        when(participanteService.buscarID(idCredor)).thenReturn(credor);
        when(participanteService.buscarID(idDevedor)).thenReturn(devedor);

        SaldoTotalEntreParticipantesResponseDTO response = saldoService.saldoTotalEntreParticipante(idDevedor,idCredor);
        assertNotNull(response);
        assertEquals(idCredor,response.idCredor());
        assertEquals("Juanito",response.nomeCredor());
        assertEquals(idDevedor,response.idDevedor());
        assertEquals("Pietro",response.nomeDevedor());
        assertEquals(new BigDecimal("150.00"),response.valorTotal());

        verify(participanteService).buscarID(idDevedor);
        verify(participanteService).buscarID(idCredor);
        verify(saldoRepository).findByCredorIdAndDevedorId(idCredor,idDevedor);
    }

    @Test
    void deveRetornarZeroQuandoNaoExistiremSaldos()
    {
        Long idCredor = 1L;
        Long idDevedor = 2L;

        Participante credor = ParticipanteFactory.criarParticipante();
        credor.setId(idCredor);
        credor.setNome("Juanito");

        Participante devedor = ParticipanteFactory.criarParticipante();
        devedor.setId(idDevedor);
        devedor.setNome("Pietro");

        when(participanteService.buscarID(idCredor)).thenReturn(credor);
        when(participanteService.buscarID(idDevedor)).thenReturn(devedor);
        when(saldoRepository.findByCredorIdAndDevedorId(idCredor,idDevedor)).thenReturn(List.of());

        SaldoTotalEntreParticipantesResponseDTO response = this.saldoService.saldoTotalEntreParticipante(idDevedor,idCredor);

        assertEquals(BigDecimal.ZERO,response.valorTotal());
    }

    @Test
    void deveLancarExcecaoQuandoCredorNaoExistir()
    {
        Long idCredor = 1L;
        Long idDevedor = 2L;

        when(participanteService.buscarID(idCredor)).thenThrow(new IdNaoEncontradoException("Participante não encontrado"));

        IdNaoEncontradoException exception = assertThrows(IdNaoEncontradoException.class,()->saldoService.saldoTotalEntreParticipante(idDevedor,idCredor));
        assertEquals("Participante não encontrado",exception.getMessage());

        verify(participanteService).buscarID(idCredor);
        verify(saldoRepository,never()).findByCredorIdAndDevedorId(anyLong(),anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoDevedorNaoExistir()
    {
        Long idCredor = 1L;
        Long idDevedor = 2L;

        Participante credor =  ParticipanteFactory.criarParticipante();
        credor.setId(idCredor);
        credor.setNome("Juanito");

        when(participanteService.buscarID(idCredor)).thenReturn(credor);
        when(participanteService.buscarID(idDevedor)).thenThrow(new IdNaoEncontradoException("Participante não encontrado"));

        IdNaoEncontradoException exception = assertThrows(IdNaoEncontradoException.class,()->saldoService.saldoTotalEntreParticipante(idDevedor,idCredor));
        assertEquals("Participante não encontrado",exception.getMessage());

        verify(participanteService).buscarID(idCredor);
        verify(participanteService).buscarID(idDevedor);
        verify(saldoRepository,never()).findByCredorIdAndDevedorId(anyLong(),anyLong());
    }

    // --- CALCULAR SALDO ---

    @Test
    void deveCalcularSaldoCorretamente()
    {
        //Arrange
        Participante pagador = new Participante();
        pagador.setId(1L);
        pagador.setNome("procopio");

        Participante cleiton = new Participante();
        cleiton.setId(2L);
        cleiton.setNome("cleiton");

        Participante carol = new Participante();
        carol.setId(3L);
        carol.setNome("carol");

        Divisao divUm =  new Divisao();
        divUm.setId(1L);
        divUm.setParticipante(pagador);
        divUm.setValor(new BigDecimal("40"));

        Divisao divDois = new Divisao();
        divDois.setId(2L);
        divDois.setParticipante(cleiton);
        divDois.setValor(new BigDecimal("30"));

        Divisao divTres = new Divisao();
        divTres.setId(3L);
        divTres.setParticipante(carol);
        divTres.setValor(new BigDecimal("30"));

        Despesa despesa = new Despesa();
        despesa.setPagador(pagador);
        despesa.setDivisoes(List.of(divUm,divDois,divTres));

        //Act
        saldoService.calcularSaldo(despesa);

        //Assert
        verify(saldoRepository).saveAll(saldoCaptor.capture());

        List<Saldo>saldos = saldoCaptor.getValue();

        assertEquals(2,saldos.size());

        assertEquals(cleiton,saldos.getFirst().getDevedor());
        assertEquals(pagador,saldos.getFirst().getCredor());
        assertEquals(new BigDecimal("30"),saldos.getFirst().getValor());

        assertEquals(carol,saldos.get(1).getDevedor());
        assertEquals(pagador,saldos.get(1).getCredor());
        assertEquals(new BigDecimal("30"),saldos.get(1).getValor());
    }

    @Test
    void deveNaoCriarSaldoQuandoHaSomentePagador()
    {
        Participante pagador = new Participante();
        pagador.setId(1L);

        Divisao divUm =  new Divisao();
        divUm.setParticipante(pagador);
        divUm.setValor(new BigDecimal("100.00"));

        Despesa despesa = new Despesa();
        despesa.setPagador(pagador);
        despesa.setDivisoes(List.of(divUm));

        saldoService.calcularSaldo(despesa);

        verify(saldoRepository).saveAll(saldoCaptor.capture());

        List<Saldo>saldos = saldoCaptor.getValue();

        assertTrue(saldos.isEmpty());
    }
}