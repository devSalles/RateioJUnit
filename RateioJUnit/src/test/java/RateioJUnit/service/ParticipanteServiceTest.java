package RateioJUnit.service;

import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.core.exception.participante.EmailNaoEncontradoException;
import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
import RateioJUnit.core.exception.participante.NomeNaoEncontradoException;
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

    // ---METODOS DE SALVAR PARTICIPANTE ---

    @Test
    void deveSalvarParticipante()
    {
        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","sallesbernardo89@gmail.com");

        participanteService.salvarParticipante(participanteResquestDTO);

        verify(participanteRepository).save(any(Participante.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir()
    {

        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","bernardo@gmail.com");

        when(participanteRepository.existsByEmail("bernardo@gmail.com")).thenReturn(true);

        assertThrows(EmailRepetidoCadastradoException.class,()->participanteService.salvarParticipante(participanteResquestDTO));
        verify(participanteRepository,never()).save(any(Participante.class));
    }

    // ---METODOS DE ATUALIZAR PARTICIPANTE ---

    @Test
    void deveAtualizarParticipante()
    {
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();
        ParticipanteResquestDTO resquestDTO = new ParticipanteResquestDTO("bernardo","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByEmailAndIdNot("bernardo@gmail.com",idParticipante)).thenReturn(false);

        ParticipanteResponseDTO response = participanteService.atualizarParticipante(idParticipante,resquestDTO);
        assertNotNull(response);

        assertEquals("bernardo",response.nome());
        assertEquals("bernardo@gmail.com",response.email());

        verify(participanteRepository).save(participante);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado()
    {
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();
        ParticipanteResquestDTO requestDTO = new ParticipanteResquestDTO("pedrin","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));
        when(participanteRepository.existsByEmailAndIdNot("bernardo@gmail.com",idParticipante)).thenReturn(true);

        assertThrows(EmailRepetidoCadastradoException.class,()->participanteService.atualizarParticipante(idParticipante,requestDTO));

        verify(participanteRepository,never()).save(any(Participante.class));
    }
    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistir()
    {
        Long idParticipante = 99L;

        ParticipanteResquestDTO requestDTO = new ParticipanteResquestDTO("bernardo","bernardo@gmail.com");

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        assertThrows(IdNaoEncontradoException.class,()->participanteService.atualizarParticipante(idParticipante,requestDTO));

        verify(participanteRepository,never()).save(any());
    }

    // --- METODOS DE BUSCAR ID ---

    @Test
    void deveBuscarOParticipantePorID()
    {
        Long idParticipante = 1L;

        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));

        ParticipanteResponseDTO participanteResponse = participanteService.buscarIDParticipante(idParticipante);
        validarParticipante(participante,participanteResponse);
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoEncontrado()
    {
        Long idParticipante = 99L;

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        assertThrows(IdNaoEncontradoException.class,()->participanteService.buscarID(idParticipante));
    }

    // --- METODO DE BUSCAR TODOS OS PARTICIPANTES ---

    @Test
    void buscarTodosOsParticipantes()
    {
        Participante participanteUm = ParticipanteFactory.criarParticipantePersonalizado(1L,"Mariana");
        Participante participanteDois = ParticipanteFactory.criarParticipantePersonalizado(2L,"Carla");

        when(this.participanteRepository.findAll()).thenReturn(List.of(participanteUm,participanteDois));

        List<ParticipanteResponseDTO> participanteResponse = this.participanteService.buscarTodosParticipantes();
        assertNotNull(participanteResponse);

        assertEquals(2,participanteResponse.size());

        assertEquals("Mariana", participanteResponse.get(0).nome());
        assertEquals("Carla",participanteResponse.get(1).nome());

        verify(participanteRepository).findAll();
    }

    @Test
    void deveLancarExcecaoCasoListaDeParticipantesVazias()
    {
        when(participanteRepository.findAll()).thenReturn(List.of());

        assertThrows(NenhumRegistroException.class,()->participanteService.buscarTodosParticipantes());

        verify(participanteRepository).findAll();
    }

    // --- METODO BUSCAR POR EMAIL ---

    @Test
    void deveBuscarParticipantePorEmail()
    {
        String emailParticipante = "bernardo@gmail.com";

        Participante participante = ParticipanteFactory.criarParticipante();
        when(participanteRepository.findByEmail(emailParticipante)).thenReturn(Optional.of(participante));

        ParticipanteResponseDTO participanteResponse = this.participanteService.buscarPorEmail(emailParticipante);
        validarParticipante(participante,participanteResponse);
    }

    @Test
    void deveLancarExcecaoQuandoEmailDeParticipanteNaoEncontrado()
    {
        String emailParticipante = "email@gmail.com";

        when(this.participanteRepository.findByEmail(emailParticipante)).thenReturn(Optional.empty());

        assertThrows(EmailNaoEncontradoException.class,()->participanteService.buscarPorEmail(emailParticipante));
    }

    // --- METODO BUSCAR PARTICIPANTE POR NOME

    @Test
    void deveBuscarParticpantePorNome()
    {
        String nomeParticipante = "Bernardo";
        Participante participante = ParticipanteFactory.criarParticipante();

        when(participanteRepository.findByNome(nomeParticipante)).thenReturn(List.of(participante));

        List<ParticipanteResponseDTO>participanteResponse = this.participanteService.buscarPorNome(nomeParticipante);
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
        String nomeParticipante = "Bernardo";

        when(this.participanteRepository.findByNome(nomeParticipante)).thenReturn(List.of());

        assertThrows(NomeNaoEncontradoException.class,()->participanteService.buscarPorNome(nomeParticipante));
    }

    // --- METODO DE DELEÇÃO DE PARTICIPANTE ---

    @Test
    void deveDeletarParticipante()
    {
        Long idParticipante = 1L;
        Participante participante = ParticipanteFactory.criarParticipante();
        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.of(participante));

        when(participanteRepository.existsByIdAndDespesasPagasIsNotEmpty(idParticipante)).thenReturn(false);
        when(participanteRepository.existsByIdAndSaldoDevedorIsNotEmpty(idParticipante)).thenReturn(false);
        when(participanteRepository.existsByIdAndSaldoCredorIsNotEmpty(idParticipante)).thenReturn(false);

        ParticipanteResponseDTO resposeDTO = this.participanteService.deletarParticipante(idParticipante);
        assertNotNull(resposeDTO);

        verify(participanteRepository).delete(participante);
    }

    @Test
    void deveLancarExcecaoQuandoIdParticipanteNaoEncontrado()
    {
        Long idParticipante = 1L;

        when(this.participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        assertThrows(IdNaoEncontradoException.class,()->participanteService.deletarParticipante(idParticipante));
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
