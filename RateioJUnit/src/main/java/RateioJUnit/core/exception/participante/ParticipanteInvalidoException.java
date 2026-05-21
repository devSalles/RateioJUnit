package RateioJUnit.core.exception.participante;

public class ParticipanteInvalidoException extends RuntimeException {
    public ParticipanteInvalidoException(String message) {
        super(message);
    }
    public ParticipanteInvalidoException() {
        super("Participante inválido");
    }
}
