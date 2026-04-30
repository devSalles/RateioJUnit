package RateioJUnit.dto.usuario;

import RateioJUnit.entity.Participante;

public record ParticipanteResponseDTO(

        Long id,
        String nome,
        String email
) {

    public static ParticipanteResponseDTO fromParticipante(Participante participante)
    {
        return new ParticipanteResponseDTO(participante.getId(), participante.getNome(), participante.getEmail());
    }
}
