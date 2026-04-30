package RateioJUnit.dto.usuario;

import RateioJUnit.entity.Participante;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ParticipanteResquestDTO(

        @NotBlank(message = "Nome do participante obrigatório")
        String nome,

        @NotBlank(message = "Email do participante obrigatório")
        @Email(message = "formato de email de participante inválido")
        String email
) {

    public Participante toParticipante() {
        Participante participante = new Participante();
        participante.setNome(this.nome);
        participante.setEmail(this.email);
        return participante;
    }

    public void updateParticipante(Participante participante)
    {
        participante.setNome(this.nome);
        participante.setEmail(this.email);
    }
}
