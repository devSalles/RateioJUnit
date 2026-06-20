package RateioJUnit.service;


import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
import RateioJUnit.dto.usuario.ParticipanteResquestDTO;
import RateioJUnit.entity.Participante;
import RateioJUnit.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private ParticipanteService participanteService;


    @Test
    void  salvarParticipante()
    {
        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","sallesbernardo89@gmail.com");

        participanteService.salvarParticipante(participanteResquestDTO);

        verify(participanteRepository).save(any(Participante.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailRepetido()
    {

        ParticipanteResquestDTO participanteResquestDTO = new ParticipanteResquestDTO("Bernardo","bernardo@gmail.com");

        when(participanteRepository.existsByEmail("bernardo@gmail.com")).thenReturn(true);

        assertThrows(EmailRepetidoCadastradoException.class,()->participanteService.salvarParticipante(participanteResquestDTO));
        verify(participanteRepository,never()).save(any(Participante.class));
    }
}
