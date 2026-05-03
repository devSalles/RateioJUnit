package RateioJUnit.core.exception.participante;

public class ParticipantePossuiDespesasException extends RuntimeException {
    public ParticipantePossuiDespesasException(String message) {
        super(message);
    }
    public ParticipantePossuiDespesasException() {
        super("Participante que possui despesas não pode ser excluído");
    }
}
