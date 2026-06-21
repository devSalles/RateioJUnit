package RateioJUnit.service;


import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
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

        ParticipanteResponseDTO response = participanteService.buscarIDParticipante(idParticipante);
        assertNotNull(response);
        assertEquals(participante.getId(),response.id());
        assertEquals(participante.getNome(),response.nome());
        assertEquals(participante.getEmail(),response.email());
    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoEncontrado()
    {
        Long idParticipante = 99L;

        when(participanteRepository.findById(idParticipante)).thenReturn(Optional.empty());

        assertThrows(IdNaoEncontradoException.class,()->participanteService.buscarID(idParticipante));
    }
}
