package RateioJUnit.service;

import RateioJUnit.core.exception.*;
import RateioJUnit.core.exception.participante.EmailNaoEncontradoException;
import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
import RateioJUnit.core.exception.participante.NomeNaoEncontradoException;
import RateioJUnit.core.exception.participante.ParticipantePossuiDespesasException;
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
        Participante participante = buscarID(idParticipante);

        boolean existeEmail = this.participanteRepository.existsByEmailAndIdNot(participanteDTO.email(),idParticipante);
        if(existeEmail)
        {
            throw new EmailRepetidoCadastradoException();
        }

        participanteDTO.updateParticipante(participante);
        this.participanteRepository.save(participante);

        return ParticipanteResponseDTO.fromParticipante(participante);
    }

    public ParticipanteResponseDTO buscarIDParticipante(Long idParticipante)
    {
         Participante participanteID = buscarID(idParticipante);
         return ParticipanteResponseDTO.fromParticipante(participanteID);
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

    public ParticipanteResponseDTO buscarPorEmail(String email)
    {
        Participante participanteEmail = this.participanteRepository.findByEmail(email).orElseThrow(EmailNaoEncontradoException::new);
        return ParticipanteResponseDTO.fromParticipante(participanteEmail);
    }

    public List<ParticipanteResponseDTO> buscarPorNome(String nomeParticipante)
    {
        List<Participante> participanteNome = this.participanteRepository.findByNome(nomeParticipante);

        if(participanteNome.isEmpty())
        {
            throw new NomeNaoEncontradoException();
        }

        return participanteNome.stream().map(ParticipanteResponseDTO::fromParticipante).toList();
    }

    public ParticipanteResponseDTO deletarParticipante(Long idParticipante)
    {
        Participante participante = buscarID(idParticipante);

        validacaoExclusao(idParticipante);

        this.participanteRepository.delete(participante);
        return ParticipanteResponseDTO.fromParticipante(participante);
    }

    //-------------- METODOS AUXILIARES --------------

    public Participante buscarID(Long idParticipante){
        return this.participanteRepository.findById(idParticipante)
                .orElseThrow(()->new IdNaoEncontradoException("Participante não encontrado"));
    }

    public void validacaoExclusao(Long idParticipante)
    {
        //Verifica se participante possui despesa pagas
        boolean possuiDespesasPagas = this.participanteRepository.existsByIdAndDespesasPagasIsNotEmpty(idParticipante);

        //Verifica se participante possui despesas no histórico
        boolean possuiDivisoes = this.participanteRepository.existsByIdAndDivisaoIsNotEmpty(idParticipante);

        boolean possuiSaldoDevedor = this.participanteRepository.existsByIdAndSaldoDevedorIsNotEmpty(idParticipante);

        boolean possuiSaldoCredor = this.participanteRepository.existsByIdAndSaldoCredorIsNotEmpty(idParticipante);

        if(possuiDespesasPagas||possuiDivisoes||possuiSaldoDevedor||possuiSaldoCredor)
        {
            throw new ParticipantePossuiDespesasException();
        }
    }
}
