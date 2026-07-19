package RateioJUnit.service;

import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.core.exception.participante.EmailNaoEncontradoException;
import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
import RateioJUnit.core.exception.participante.NomeNaoEncontradoException;
import RateioJUnit.core.exception.participante.ParticipantePossuiDespesasException;
import RateioJUnit.dto.usuario.ParticipanteResponseDTO;
import RateioJUnit.dto.usuario.ParticipanteResquestDTO;
import RateioJUnit.entity.Participante;
import RateioJUnit.factory.ParticipanteFactory;
import RateioJUnit.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private ParticipanteService participanteService;

    // --- SALVAR PARTICIPANTE ---

    @Test
    void deveSalvarParticipante()
    {
        //Arrange
        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","sallesbernardo89@gmail.com");

        //Act
        participanteService.salvarParticipante(participanteResquestDTO);

        //Assert
        verify(participanteRepository).save(any(Participante.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir()
    {
        //Arrange
        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","bernardo@gmail.com");

        when(participanteRepository.existsByEmail("bernardo@gmail.com")).thenReturn(true);

        //Act
        assertThrows(EmailRepetidoCadastradoException.class,()->participanteService.salvarParticipante(participanteResquestDTO));

        //Assert
        verify(participanteRepository,never()).save(any(Participante.class));
    }

    // --- ATUALIZAR PARTICIPANTE ---

    @Test
    void deveAtualizarParticipante()
    {
        //Arrange
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();
        ParticipanteResquestDTO resquestDTO = new ParticipanteResquestDTO("bernardo","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByEmailAndIdNot("bernardo@gmail.com",idParticipante)).thenReturn(false);

        //Act
        ParticipanteResponseDTO response = participanteService.atualizarParticipante(idParticipante,resquestDTO);

        //Assert
        assertNotNull(response);
        assertEquals("bernardo",response.nome());
        assertEquals("bernardo@gmail.com",response.email());

        verify(participanteRepository).save(participante);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado()
    {
        //Arrange
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();
        ParticipanteResquestDTO requestDTO = new ParticipanteResquestDTO("pedrin","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByEmailAndIdNot("bernardo@gmail.com",idParticipante)).thenReturn(true);

        //Act
        assertThrows(EmailRepetidoCadastradoException.class,()->participanteService.atualizarParticipante(idParticipante,requestDTO));

        //Assert
        verify(participanteRepository,never()).save(any(Participante.class));
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistir()
    {
        //Arrange
        Long idParticipante = 99L;

        ParticipanteResquestDTO requestDTO = new ParticipanteResquestDTO("bernardo","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        //Act
        assertThrows(IdNaoEncontradoException.class,()->participanteService.atualizarParticipante(idParticipante,requestDTO));

        //Assert
        verify(participanteRepository,never()).save(any());
    }

    // --- METODOS DE BUSCAR ID ---

    @Test
    void deveBuscarOParticipantePorID()
    {
        //Arrange
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));

        //Act
        ParticipanteResponseDTO participanteResponse = participanteService.buscarIDParticipante(idParticipante);

        //Assert
        validarParticipante(participante,participanteResponse);
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoEncontrado()
    {
        //Arrange
        Long idParticipante = 99L;
        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        //Act
        assertThrows(IdNaoEncontradoException.class,()->participanteService.buscarID(idParticipante));

        //Assert
        verify(participanteRepository).findById(idParticipante);
    }

    // --- BUSCAR TODOS OS PARTICIPANTES ---

    @Test
    void buscarTodosOsParticipantes()
    {
        //Arrange
        Participante participanteUm = ParticipanteFactory.criarParticipantePersonalizado(1L,"Mariana");
        Participante participanteDois = ParticipanteFactory.criarParticipantePersonalizado(2L,"Carla");

        when(this.participanteRepository.findAll()).thenReturn(List.of(participanteUm,participanteDois));

        //Act
        List<ParticipanteResponseDTO> participanteResponse = this.participanteService.buscarTodosParticipantes();

        //Assert
        assertNotNull(participanteResponse);

        assertEquals(2,participanteResponse.size());

        assertEquals("Mariana", participanteResponse.get(0).nome());
        assertEquals("Carla",participanteResponse.get(1).nome());

        verify(participanteRepository).findAll();
    }

    @Test
    void deveLancarExcecaoCasoListaDeParticipantesVazias()
    {
        //Arrange
        when(participanteRepository.findAll()).thenReturn(List.of());

        //Act
        assertThrows(NenhumRegistroException.class,()->participanteService.buscarTodosParticipantes());

        //Assert
        verify(participanteRepository).findAll();
    }

    // --- BUSCAR POR EMAIL ---

    @Test
    void deveBuscarParticipantePorEmail()
    {
        //Arrange
        String emailParticipante = "bernardo@gmail.com";

        Participante participante = ParticipanteFactory.criarParticipante();
        when(participanteRepository.findByEmail(emailParticipante)).thenReturn(Optional.of(participante));

        //Act
        ParticipanteResponseDTO participanteResponse = this.participanteService.buscarPorEmail(emailParticipante);

        //Assert
        validarParticipante(participante,participanteResponse);
    }

    @Test
    void deveLancarExcecaoQuandoEmailDeParticipanteNaoEncontrado()
    {
        //Arrange
        String emailParticipante = "email@gmail.com";
        when(this.participanteRepository.findByEmail(emailParticipante)).thenReturn(Optional.empty());

        //Act
        assertThrows(EmailNaoEncontradoException.class,()->participanteService.buscarPorEmail(emailParticipante));

        //Assert
        verify(participanteRepository).findByEmail(emailParticipante);
    }

    // --- BUSCAR PARTICIPANTE POR NOME ---

    @Test
    void deveBuscarParticpantePorNome()
    {
        //Arrange
        String nomeParticipante = "Bernardo";
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findByNome(nomeParticipante)).thenReturn(List.of(participante));

        //Act
        List<ParticipanteResponseDTO>participanteResponse = this.participanteService.buscarPorNome(nomeParticipante);

        //Assert
        assertNotNull(participanteResponse);
        assertEquals(1,participanteResponse.size());

        ParticipanteResponseDTO responseDTO = participanteResponse.getFirst();
        assertAll(()->assertNotNull(responseDTO),
                ()->assertEquals(participante.getId(),responseDTO.id()),
                ()->assertEquals(participante.getNome(),responseDTO.nome()),
                ()->assertEquals(participante.getEmail(),responseDTO.email())
        );

        verify(participanteRepository).findByNome(nomeParticipante);
    }

    @Test
    void deveLancarExcecaoQuandoNomeNaoEncontrado()
    {
        //Arrange
        String nomeParticipante = "Bernardo";
        when(this.participanteRepository.findByNome(nomeParticipante)).thenReturn(List.of());

        //Act
        assertThrows(NomeNaoEncontradoException.class,()->participanteService.buscarPorNome(nomeParticipante));

        //Assert
        verify(participanteRepository).findByNome(nomeParticipante);
    }

    // --- DELEÇÃO DE PARTICIPANTE ---

    @Test
    void deveDeletarParticipante()
    {
        //Arrange
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();
        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));

        when(participanteRepository.existsByIdAndDespesasPagasIsNotEmpty(idParticipante)).thenReturn(false);
        when(participanteRepository.existsByIdAndDivisaoIsNotEmpty(idParticipante)).thenReturn(false);
        when(participanteRepository.existsByIdAndSaldoDevedorIsNotEmpty(idParticipante)).thenReturn(false);
        when(participanteRepository.existsByIdAndSaldoCredorIsNotEmpty(idParticipante)).thenReturn(false);

        //Act
        ParticipanteResponseDTO resposeDTO = this.participanteService.deletarParticipante(idParticipante);

        //Assert
        assertNotNull(resposeDTO);

        verify(participanteRepository).delete(participante);
    }

    @Test
    void deveLancarExcecaoQuandoIdParticipanteNaoEncontrado()
    {
        //Arrange
        Long idParticipante = 1L;
        when(this.participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        //Act
        assertThrows(IdNaoEncontradoException.class,()->participanteService.deletarParticipante(idParticipante));

        //Assert
        verify(participanteRepository).findById(idParticipante);
    }

    // Metodo para testar lançamento de exceção caso participante tenha despesa vinculada
    @Test
    void deveLancarExcecaoQuandoParticipantePossuirDespesa()
    {
        //Arrange
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByIdAndDespesasPagasIsNotEmpty(idParticipante)).thenReturn(true);

        //Act
        assertThrows(ParticipantePossuiDespesasException.class,()->participanteService.deletarParticipante(idParticipante));

        //Assert
        verify(participanteRepository,never()).delete(participante);
    }

    @Test
    void deveLancarExcecaoQuandoParticipantePossuirDivisoes()
    {
        //Arrange
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByIdAndDivisaoIsNotEmpty(idParticipante)).thenReturn(true);

        //Act
        assertThrows(ParticipantePossuiDespesasException.class,()->participanteService.deletarParticipante(idParticipante));

        //Assert
        verify(participanteRepository,never()).delete(participante);
    }

    @Test
    void deveLancarExcecaoQuandoParticipantePossuirSaldoDevedor()
    {
        //Arrange
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByIdAndSaldoDevedorIsNotEmpty(idParticipante)).thenReturn(true);

        //Act
        assertThrows(ParticipantePossuiDespesasException.class,()->participanteService.deletarParticipante(idParticipante));

        //Assert
        verify(participanteRepository,never()).delete(participante);
    }

    @Test
    void deveLancarExcecaoQuandoParticipantePossuirSaldoCredor()
    {
        //Arrange
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByIdAndSaldoCredorIsNotEmpty(idParticipante)).thenReturn(true);

        //Act
        assertThrows(ParticipantePossuiDespesasException.class,()->participanteService.deletarParticipante(idParticipante));

        //Assert
        verify(participanteRepository,never()).delete(participante);
    }

    // --- METODO AUXILIAR ---

    void validarParticipante(Participante participante, ParticipanteResponseDTO responseDTO)
    {
        assertAll(
                ()->assertNotNull(responseDTO),
                ()-> assertEquals(participante.getId(),responseDTO.id()),
                ()-> assertEquals(participante.getNome(),responseDTO.nome()),
                ()->assertEquals(participante.getEmail(),responseDTO.email())
        );
    }
}