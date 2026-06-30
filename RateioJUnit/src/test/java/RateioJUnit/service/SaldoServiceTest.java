package RateioJUnit.service;

import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.dto.saldo.ResumoSaldoTotalResponseDTO;
import RateioJUnit.dto.saldo.SaldoResponseDTO;
import RateioJUnit.dto.saldo.SaldoTotalEntreParticipantesResponseDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaldoServiceTest {

    @Mock
    ParticipanteService participanteService;

    @Mock
    SaldoRepository saldoRepository;

    @InjectMocks
    SaldoService saldoService;


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

    // --- LISTAR SALDO POR USUÁRIO ---

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
}