package RateioJUnit.service;

import RateioJUnit.core.exception.EmailRepetidoCadastradoException;
import RateioJUnit.core.exception.IdNaoEncontradoException;
import RateioJUnit.core.exception.NenhumRegistroException;
import RateioJUnit.dto.usuario.ParticipanteResponseDTO;
import RateioJUnit.dto.usuario.ParticipanteResquestDTO;
import RateioJUnit.entity.Participante;
import RateioJUnit.repository.ParticipanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;


    public ParticipanteResponseDTO salvarParticipante(ParticipanteResquestDTO participanteResquestDTO)
    {
        if(this.participanteRepository.existsByEmail(participanteResquestDTO.email()))
        {
            throw new EmailRepetidoCadastradoException();
        }

        Participante participante = participanteResquestDTO.toParticipante();
        participanteRepository.save(participante);

        return ParticipanteResponseDTO.fromParticipante(participante);
    }

    public ParticipanteResponseDTO atualizarParticipante(Long idParticipante,ParticipanteResquestDTO participanteDTO)
    {
        if(this.participanteRepository.existsByEmail(participanteDTO.email()))
        {
            throw new EmailRepetidoCadastradoException();
        }

        Participante participante = buscarID(idParticipante);

        participanteDTO.updateParticipante(participante);
        this.participanteRepository.save(participante);

        return ParticipanteResponseDTO.fromParticipante(participante);
    }

    public ParticipanteResponseDTO buscarIDParticipante(Long idParticipante)
    {
         Participante paeticipanteID = buscarID(idParticipante);
         return ParticipanteResponseDTO.fromParticipante(paeticipanteID);
    }

    public List<ParticipanteResponseDTO> buscarTodosParticipantes()
    {
        List<Participante> participantes = participanteRepository.findAll();

        if(participantes.isEmpty())
        {
            throw new NenhumRegistroException("Nenhum registro foi encontrado");
        }

        return participantes.stream().map(ParticipanteResponseDTO::fromParticipante).toList();
    }
    

    //------------- Metodos Auxiliares -------------

    public Participante buscarID(Long idParticipante){
        return this.participanteRepository.findById(idParticipante)
                .orElseThrow(()->new IdNaoEncontradoException("Id de participante não encontrado"));
    }
}
