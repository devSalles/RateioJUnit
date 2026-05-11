package RateioJUnit.core.exception.despesa;

public class DespesaInexistenteException extends RuntimeException {
    public DespesaInexistenteException(String message) {
        super(message);
    }
    public DespesaInexistenteException() {
        super("Tipo de despesa inexistente");
    }
}
