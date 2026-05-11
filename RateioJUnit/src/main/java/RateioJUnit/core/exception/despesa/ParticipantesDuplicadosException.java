package RateioJUnit.core.exception.despesa;

public class ParticipantesDuplicadosException extends RuntimeException {
    public ParticipantesDuplicadosException(String message) {
        super(message);
    }
    public ParticipantesDuplicadosException() {
        super("Participantes duplicados");
    }
}
