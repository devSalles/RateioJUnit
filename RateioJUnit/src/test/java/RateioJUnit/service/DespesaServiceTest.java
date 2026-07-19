package RateioJUnit.service;

import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.core.exception.despesa.*;
import RateioJUnit.core.exception.participante.ParticipanteInvalidoException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    void deveAdicionarDespesaComDivisaoPersonalizada()
    {
        //Arrange
        Participante Bernardo = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");
        Participante Pietro = ParticipanteFactory.criarParticipantePersonalizado(2L,"Pietro");

        when(participanteService.buscarID(1L)).thenReturn(Bernardo);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Bernardo));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(Pietro));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("50.00"));
        DivisaoRequestDTO d2 = new DivisaoRequestDTO(2L, new BigDecimal("50.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Pizza",new BigDecimal("100.00"),1L, List.of(d1,d2), TipoDivisao.PERSONALIZADA);

        //Act
        despesaService.adicionarDespesa(despesaRequestDTO);

        //Assert
        verify(despesaRepository).save(any(Despesa.class));
    }

    @Test
    void deveAdicionarDespesaComDivisaoIgual()
    {
        //Arrange
        Participante pagador = ParticipanteFactory.criarParticipantePersonalizado(1L,"Pagador");
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(2L,"Participante");

        when(participanteService.buscarID(1L)).thenReturn(pagador);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(pagador));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(participante));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("50.00"));
        DivisaoRequestDTO d2 = new DivisaoRequestDTO(2L,new BigDecimal("50.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Compra chocolate",new BigDecimal("100.00"),1L, List.of(d1,d2), TipoDivisao.IGUAL);

        //Act
        despesaService.adicionarDespesa(despesaRequestDTO);

        //Assert
        verify(despesaRepository).save(any(Despesa.class));
    }

    @Test
    void deveLancarExcecaoQuandoPagadorNaoEstiverNaLista()
    {
        //Arrange
        Participante Matheus = ParticipanteFactory.criarParticipantePersonalizado(1L,"Matheus");

        when(participanteService.buscarID(1L)).thenReturn(Matheus);
        when(participanteRepository.findById(2L)).thenReturn(Optional.empty());

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(2L, new BigDecimal("50.00"));

        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Queijo",new BigDecimal("100.00"),1L,List.of(d1),TipoDivisao.PERSONALIZADA);

        //Act
        IdNaoEncontradoException exception = assertThrows(IdNaoEncontradoException.class, ()-> despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertEquals("ID de pagador não encontrado",exception.getMessage());
        verify(participanteService).buscarID(1L);
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteDuplicado()
    {
        //Arrange
        Participante Lima = ParticipanteFactory.criarParticipantePersonalizado(1L,"Lima");

        when(participanteService.buscarID(1L)).thenReturn(Lima);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Lima));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("1000.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Compra telefone",new BigDecimal("2000.00"),1L,List.of(d1,d1), TipoDivisao.PERSONALIZADA);

        //Act & Assert
        assertThrows(ParticipantesDuplicadosException.class, () -> despesaService.adicionarDespesa(despesaRequestDTO));
    }

    @Test
    void deveLancarExcecaoQuandoSomaDiferenteTotal()
    {
        //Arrange
        Participante Victor = ParticipanteFactory.criarParticipantePersonalizado(1L,"Victor");
        Participante Anderson = ParticipanteFactory.criarParticipantePersonalizado(2L,"Anderson");

        when(participanteService.buscarID(1L)).thenReturn(Victor);

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(Victor));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(Anderson));

        DivisaoRequestDTO d1 = new DivisaoRequestDTO(1L,new BigDecimal("5000.00"));
        DivisaoRequestDTO d2 = new DivisaoRequestDTO(2L,new BigDecimal("3000.00"));

        DespesaRequestDTO despesaRequestDTO =
                new DespesaRequestDTO("Viagem",new BigDecimal("9000.00"),1L,List.of(d1,d2), TipoDivisao.PERSONALIZADA);

        //Act
        assertThrows(DiferenteValorTotalException.class,()-> despesaService.adicionarDespesa(despesaRequestDTO));

        //Assert
        verify(participanteRepository).findById(1L);
        verify(participanteRepository).findById(2L);
    }

    @Test
    void deveLancarExcecaoQuandoValorTotalDespesaForMaiorOuIgualAZero()
    {
        //Arrange
        DespesaRequestDTO despesaReqDto = new DespesaRequestDTO("compra pizza",BigDecimal.ZERO,1L,List.of(),TipoDivisao.IGUAL);

        //Act
        assertThrows(ValorTotalInvalidoException.class,()->despesaService.adicionarDespesa(despesaReqDto));

        //Assert
        verifyNoInteractions(participanteService);
        verifyNoInteractions(participanteRepository);
        verifyNoInteractions(despesaRepository);
    }

    @Test
    void deveLancarExcecaoQuandoValorDeDivisaoForNulo()
    {
        //Arrange
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteService.buscarID(1L)).thenReturn(participante);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));

        DivisaoRequestDTO divDto = new DivisaoRequestDTO(1L,null);

        DespesaRequestDTO despesaDTO =
                new DespesaRequestDTO("Aluguel", new BigDecimal("100.00"), 1L, List.of(divDto), TipoDivisao.PERSONALIZADA);

        //Act
        ValorNegativoException exception = assertThrows(ValorNegativoException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertNotNull(exception);
        verify(participanteRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoValorDeDivisaoForNegativo()
    {
        //Arrange
        Participante  participante = ParticipanteFactory.criarParticipante();

        when(participanteService.buscarID(1L)).thenReturn(participante);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(participante));

        DivisaoRequestDTO divDto = new DivisaoRequestDTO(1L,new BigDecimal("-1000.00"));

        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Aluguel", new BigDecimal("6400.00"), 1L, List.of(divDto), TipoDivisao.PERSONALIZADA);

        //Act
        ValorNegativoException exception = assertThrows(ValorNegativoException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertNotNull(exception);
        verify(participanteRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoListaDeParticipanteForNulo()
    {
        //Arrange
        Participante pagador = ParticipanteFactory.criarParticipante();
        when(participanteService.buscarID(1L)).thenReturn(pagador);

        List<DivisaoRequestDTO> participantes = new ArrayList<>();
        participantes.add(null);

        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Compra Pizza", new BigDecimal("60.00"), 1L, participantes, TipoDivisao.PERSONALIZADA);

        //Act
        assertThrows(ParticipanteInvalidoException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        verifyNoInteractions(despesaRepository);
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaDivisaoPersonalizadaNaoForEncontrado()
    {
        //Arrange
        Participante p1 = ParticipanteFactory.criarParticipantePersonalizado(1L,"Erick");
        Participante p2 = ParticipanteFactory.criarParticipantePersonalizado(2L,"Eugenio");

        Despesa despesa = new Despesa();
        despesa.setValorTotal(new BigDecimal("1000.00"));

        List<DivisaoRequestDTO> participantesDTO = List.of(new DivisaoRequestDTO(1L, new BigDecimal("500.00")));

        //Act & Assert
        assertThrows(IdNaoEncontradoException.class,()->despesaService.AplicarDivisaoPersonalizada(despesa,List.of(p1,p2),participantesDTO));
    }

    @Test
    void deveDistribuirValorIgualmenteComRestoDistribuidoEntreParticipantes()
    {
        //Arrange
        Participante andre = ParticipanteFactory.criarParticipantePersonalizado(1L,"Andre");
        Participante felipe =  ParticipanteFactory.criarParticipantePersonalizado(2L,"Felipe");
        Participante julio = ParticipanteFactory.criarParticipantePersonalizado(3L,"Julio");

        when(participanteService.buscarID(1L)).thenReturn(andre);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(andre));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(felipe));
        when(participanteRepository.findById(3L)).thenReturn(Optional.of(julio));

        DivisaoRequestDTO divUm = new DivisaoRequestDTO(1L,null);
        DivisaoRequestDTO divDois = new DivisaoRequestDTO(2L,null);
        DivisaoRequestDTO divTres = new DivisaoRequestDTO(3L,null);

        DespesaRequestDTO despesa =
                new DespesaRequestDTO("Compra fone",new BigDecimal("100.00"),1L,List.of(divUm,divDois,divTres),TipoDivisao.IGUAL);

        //Act
        despesaService.adicionarDespesa(despesa);

        //Assert
        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());

        List<Divisao>divisoes = captor.getValue().getDivisoes();

        assertEquals(new BigDecimal("33.34"),divisoes.getFirst().getValor());
        assertEquals(new BigDecimal("33.33"),divisoes.get(1).getValor());
        assertEquals(new BigDecimal("33.33"),divisoes.get(2).getValor());

        BigDecimal soma = divisoes.stream().map(Divisao::getValor).reduce(BigDecimal.ZERO,BigDecimal::add);
        assertEquals(new BigDecimal("100.00"),soma);
    }

    @Test
    void deveLancarExcecaoQuandoValorTotalForNulo()
    {
        //Arrange
        DespesaRequestDTO despesaDTO =
                new DespesaRequestDTO("Compra veiculo",null,1L,List.of(),TipoDivisao.IGUAL);

        //Act
        ValorTotalInvalidoException exception = assertThrows(ValorTotalInvalidoException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertEquals("Valor total inválido",exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoPagadorNaoEstiverNalista()
    {
        //Arrange
        Participante pagador = ParticipanteFactory.criarParticipantePersonalizado(1L,"Helena");
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(2L,"Carol");

        when(participanteService.buscarID(1L)).thenReturn(pagador);
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(participante));

        DivisaoRequestDTO divisaoDTO = new DivisaoRequestDTO(2L,new BigDecimal("100.00"));

        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Compra fone",new BigDecimal("200.00"),1L,List.of(divisaoDTO),TipoDivisao.IGUAL);

        //Act
        PagadorNaoEstaNaListaException exception = assertThrows(PagadorNaoEstaNaListaException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertEquals("Pagador precisa estar na lista",exception.getMessage());
        verify(participanteRepository).findById(2L);
    }

    @Test
    void deveLancarExcecaoQuandoStatusDeTipoDivisaoForNulo()
    {
        //Arrange
        Participante pagador = ParticipanteFactory.criarParticipantePersonalizado(1L,"Joao");

        when(participanteService.buscarID(1L)).thenReturn(pagador);
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(pagador));

        DivisaoRequestDTO divisaoDTO = new DivisaoRequestDTO(1L,new BigDecimal("100.00"));
        DespesaRequestDTO despesaDTO =
                new DespesaRequestDTO("Compra pizza",new BigDecimal("100.00"),1L,List.of(divisaoDTO),null);

        //Act
        TipoDivisaoInexistenteException exception = assertThrows(TipoDivisaoInexistenteException.class,()->despesaService.adicionarDespesa(despesaDTO));

        //Assert
        assertEquals("Tipo de despesa inexistente",exception.getMessage());

        verify(participanteService).buscarID(1L);
        verify(participanteRepository).findById(1L);
    }

    // --- ATUALIZAR DESPESA ---

    @Test
    void deveAtualizarDespesa()
    {
        //Arrange
        Long idDespesa = 1L;
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        Divisao divisao = new Divisao();
        divisao.setParticipante(participante);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaUpdt = new DespesaUpdtDto("Compra pizza",1L);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));
        when(participanteService.buscarID(idDespesa)).thenReturn(participante);

        //Act
        DespesaResponseDTO response = despesaService.atualizarDespesa(idDespesa,despesaUpdt);

        //Assert
        assertEquals("Compra pizza",response.descricao());
        assertEquals(participante.getId(),response.idPagador());

        verify(despesaRepository).save(despesa);
    }

    @Test
    void deveLancarExcecaoQuandoPagadorNaoEstiverNaListaNoMetodoDeAtualizarDespesa()
    {
        //Arrange
        Long idDespesa = 1L;
        Participante participanteLista = ParticipanteFactory.criarParticipantePersonalizado(1L,"Bernardo");
        Participante novoPagador = ParticipanteFactory.criarParticipantePersonalizado(2L,"Anderson");

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        Divisao divisao = new Divisao();
        divisao.setParticipante(participanteLista);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaUpdt = new DespesaUpdtDto("Compra pizza",2L);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));
        when(participanteService.buscarID(2L)).thenReturn(novoPagador);

        //Act
        assertThrows(PagadorNaoEstaNaListaException.class,()->despesaService.atualizarDespesa(idDespesa,despesaUpdt));

        //Assert
        verify(despesaRepository).findById(idDespesa);
        verify(participanteService).buscarID(2L);
        verify(despesaRepository,never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoStatusEstiverIncorretoParaAtualizarDespesa()
    {
        //Arrange
        Long idDespesa = 1L;
        Participante participante = ParticipanteFactory.criarParticipantePersonalizado(1L,"Kaique");

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("500.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        Divisao divisao = new Divisao();
        divisao.setParticipante(participante);

        despesa.setDivisoes(List.of(divisao));

        DespesaUpdtDto despesaReqDTO = new DespesaUpdtDto("Compra teclado",1L);

        when(participanteService.buscarID(1L)).thenReturn(participante);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaEmStatusInicialException.class,()->despesaService.atualizarDespesa(1L,despesaReqDTO));

        //Assert
        verify(despesaRepository).findById(idDespesa);
        verify(participanteService).buscarID(1L);
        verify(despesaRepository,never()).save(any(Despesa.class));
    }

    @Test
    void deveLancarExcecaoQuandoDespesaNaoEncontrada()
    {
        //Arrange
        Long idDespesa = 2L;

        DespesaUpdtDto despesaRequestDTO =
                new DespesaUpdtDto("Compra Telefone",1L);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());

        //Act
        IdNaoEncontradoException exception =
                assertThrows(IdNaoEncontradoException.class,()->despesaService.atualizarDespesa(idDespesa,despesaRequestDTO));

        //Assert
        assertEquals("Despesa não encontrada",exception.getMessage());

        verify(despesaRepository).findById(idDespesa);
        verify(participanteService,never()).buscarID(anyLong());
        verify(despesaRepository,never()).save(any(Despesa.class));
    }

    // ---LISTAR TODAS AS DESPESAS ---

    @Test
    void deveListarTodasDespesas()
    {
        //Arrange
        Despesa despesaUm = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        Despesa despesaDois = DespesaFactory.criarDespesa(2L,new BigDecimal("200.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findAll()).thenReturn(List.of(despesaUm,despesaDois));

        //Act
        List<DespesaResponseDTO> response = despesaService.listarTodasDespesas();

        //Assert
        assertNotNull(response);
        assertEquals(2,response.size());

        verify(despesaRepository).findAll();
    }

    @Test
    void deveLancarExcecaoQuandoNaoTiverDespesas()
    {
        //Arrange
        when(despesaRepository.findAll()).thenReturn(List.of());

        //Act
        assertThrows(NenhumRegistroException.class,()->despesaService.listarTodasDespesas());

        //Assert
        verify(despesaRepository).findAll();
    }

    // ---LISTAR DEPSESA POR ID ---

    @Test
    void deveBuscarDespesaPorId()
    {
        //Arrange
        Long idDespesa = 1L;
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        //Act
        DespesaResponseDTO response = despesaService.buscarDespesaPorId(idDespesa);

        //Assert
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
        //Arrange
        Long idDespesa = 1L;
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());

        //Act
        assertThrows(IdNaoEncontradoException.class,()->despesaService.buscarDespesaPorId(idDespesa));

        //Assert
        verify(despesaRepository).findById(idDespesa);
    }

    // ---LISTAR DESPESA POR STATUS ---

    @Test
    void deveBuscarDespesaPorStatus()
    {
        //Arrange
        StatusDespesa statusDespesa = StatusDespesa.FINALIZADA;
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByStatusDespesa(statusDespesa)).thenReturn(List.of(despesa));

        //Act
        List<DespesaResponseDTO> response = despesaService.listarDespesaPorStatus(statusDespesa);

        //Assert
        assertNotNull(response);
        assertEquals(despesa.getStatusDespesa(),response.getFirst().statusDespesa());
        assertEquals(despesa.getValorTotal(),response.getFirst().valorTotal());
        assertEquals(despesa.getDescricao(),response.getFirst().descricao());

        verify(despesaRepository).findByStatusDespesa(statusDespesa);
    }

    @Test
    void deveLancarExcecaoCasoDespesaNaoEncontrada()
    {
        //Arrange
        StatusDespesa statusDespesa = StatusDespesa.FINALIZADA;
        when(despesaRepository.findByStatusDespesa(statusDespesa)).thenReturn(List.of());

        //Act
        assertThrows(TipoDivisaoInexistenteException.class,()->despesaService.listarDespesaPorStatus(statusDespesa));

        //Assert
        verify(despesaRepository).findByStatusDespesa(statusDespesa);
    }

    // --- BUSCAR DESPESA POR TIPO DE DIVISÃO ---

    @Test
    void deveBuscarDespesaPorTipoDeDivisao()
    {
        //Arrange
        TipoDivisao tipoDivisao = TipoDivisao.IGUAL;
        Despesa despesaUm = DespesaFactory.criarDespesa(1L,new BigDecimal("20.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        Despesa despesaDois = DespesaFactory.criarDespesa(2L,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByTipoDivisao(tipoDivisao)).thenReturn(List.of(despesaUm,despesaDois));

        //Act
        List<DespesaResponseDTO> despesaResponse = despesaService.listarDespesaPorTipoDivisao(tipoDivisao);

        //Assert
        assertEquals(2,despesaResponse.size());
        assertEquals(despesaUm.getTipoDivisao(),despesaResponse.getFirst().tipoDivisao());
        assertEquals(despesaDois.getTipoDivisao(),despesaResponse.getFirst().tipoDivisao());

        verify(despesaRepository).findByTipoDivisao(tipoDivisao);
    }


    @Test
    void deveLancarExcecaoQuandoTipoDeDivisaoDeDespesaNaoEncontrada()
    {
        //Arrange
        TipoDivisao tipoDivisao = TipoDivisao.PERSONALIZADA;
        when(despesaRepository.findByTipoDivisao(tipoDivisao)).thenReturn(List.of());

        //Act
        assertThrows(TipoDivisaoInexistenteException.class,()->despesaService.listarDespesaPorTipoDivisao(tipoDivisao));

        //Assert
        verify(despesaRepository).findByTipoDivisao(tipoDivisao);
    }

    // --- FINALIZAR DESPESA ---

    @Test
    void deveFinalizarDespesa()
    {
        //Arrange
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("100.00"), StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        //Act
        despesaService.finalizacaoDespesa(1L);

        //Assert
        assertEquals(StatusDespesa.FINALIZADA,despesa.getStatusDespesa());
        verify(saldoService).calcularSaldo(despesa);
    }

    @Test
    void naoDeveFinalizarDespesaJaFinalizada()
    {
        //Arrange
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("60.00"), StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.finalizacaoDespesa(1L));

        //Assert
        verify(despesaRepository).findById(1L);
    }

    @Test
    void naoDeveFinalizarDespesaCancelada()
    {
        //Arrange
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("500.00"), StatusDespesa.CANCELADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaCanceladaException.class,()->despesaService.finalizacaoDespesa(1L));

        //Assert
        verify(despesaRepository).findById(1L);
    }

    // --- CANCELAR DESPESA ---

    @Test
    void deveCancelarDespesa()
    {
        //Arrange
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10000.00"), StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        //Act
        despesaService.cancelarDespesa(1L);

        //Assert
        assertEquals(StatusDespesa.CANCELADA,despesa.getStatusDespesa());

        verify(saldoService).removeSaldo(despesa);
    }

    @Test
    void deveLancarExcecaoQuandoIdDeDespesaNaoEncontrado()
    {
        //Arrange
        Long idDespesa = 2L;

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10.00"),StatusDespesa.CANCELADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.empty());

        //Act
        assertThrows(IdNaoEncontradoException.class,()->despesaService.cancelarDespesa(idDespesa));

        //Assert
        verify(despesaRepository).findById(idDespesa);
        verify(despesaRepository,never()).save(despesa);
    }

    @Test
    void naoDeveCancelarDespesaFinalizada()
    {
        //Arrange
        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("1500.00"), StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.cancelarDespesa(1L));

        //Assert
        verify(despesaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCancelarDespesaJaCancelada()
    {
        //Arrange
        Long idDespesa = 1L;
        Despesa despesa = DespesaFactory.criarDespesa(idDespesa,new BigDecimal("45.00"),StatusDespesa.CANCELADA,TipoDivisao.IGUAL);

        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaCanceladaException.class,()->despesaService.cancelarDespesa(idDespesa));

        //Assert
        verify(despesaRepository).findById(idDespesa);
    }

    @Test
    void deveLancarExcecaoQuandoCancelarDespesaJaFinalizada()
    {
        //Arrange
        Long idDespesa = 1L;
        Despesa despesa = DespesaFactory.criarDespesa(idDespesa,new BigDecimal("100.00"),StatusDespesa.FINALIZADA,TipoDivisao.IGUAL);
        when(despesaRepository.findById(idDespesa)).thenReturn(Optional.of(despesa));

        //Act
        assertThrows(DespesaJaFinalizadaException.class,()->despesaService.cancelarDespesa(1L));

        //Assert
        verify(despesaRepository).findById(idDespesa);
    }

    // --- BUSCA ENTRE DATAS ---

    @Test
    void deveBuscarDespesaEntreDatas()
    {
        //Arrange
        LocalDate dataInicial = LocalDate.of(2026,1,1);
        LocalDate dataFinal = LocalDate.of(2026,2,2);

        LocalDateTime dataInicialFormatada = dataInicial.atStartOfDay();
        LocalDateTime dataFinalFormatada  = dataFinal.atTime(LocalTime.MAX);

        Despesa despesa = DespesaFactory.criarDespesa(1L,new BigDecimal("10.00"),StatusDespesa.CRIADA,TipoDivisao.IGUAL);

        when(despesaRepository.findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada)).thenReturn(List.of(despesa));

        //Act
        List<DespesaResponseDTO> response = despesaService.buscarEntreDatas(dataInicial,dataFinal);

        //Assert
        assertEquals(1,response.size());
        assertEquals(despesa.getDescricao(),response.getFirst().descricao());
        assertEquals(despesa.getValorTotal(),response.getFirst().valorTotal());

        verify(despesaRepository).findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada);

    }

    @Test
    void deveLancarExcecaoQuandoRetornaListaVazia()
    {
        //Arrange
        LocalDate dataInicial = LocalDate.of(2026,1,1);
        LocalDate dataFinal = LocalDate.of(2026,2,2);

        LocalDateTime dataInicialFormatada = dataInicial.atStartOfDay();
        LocalDateTime dataFinalFormatada  = dataFinal.atTime(LocalTime.MAX);

        when(despesaRepository.findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada)).thenReturn(List.of());

        //Act
        assertThrows(NenhumRegistroException.class,()->despesaService.buscarEntreDatas(dataInicial,dataFinal));

        //Assert
        verify(despesaRepository).findByDataCriacaoBetween(dataInicialFormatada,dataFinalFormatada);
    }

    @Test
    void deveLancarExcecaoQuandoDataFinalMenorQueDataInicial()
    {
        //Arrange
        LocalDate dataInicial = LocalDate.of(2026,1,10);
        LocalDate dataFinal = LocalDate.of(2025,5,30);

        //Act
        assertThrows(DataExcpetion.class,()->despesaService.buscarEntreDatas(dataInicial,dataFinal));

        //Assert
        verify(despesaRepository,never()).findByDataCriacaoBetween(any(LocalDateTime.class),any(LocalDateTime.class));
    }
}