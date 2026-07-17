package RateioJUnit.core.exception.despesa;

public class TipoDivisaoInexistenteException extends RuntimeException {
    public TipoDivisaoInexistenteException(String message) {
        super(message);
    }
    public TipoDivisaoInexistenteException() {
        super("Tipo de despesa inexistente");
    }
}
