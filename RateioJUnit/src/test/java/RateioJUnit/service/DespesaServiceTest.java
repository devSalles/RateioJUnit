package RateioJUnit.service;

import RateioJUnit.repository.DespesaRepository;
import RateioJUnit.repository.ParticipanteRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
}
