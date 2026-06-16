package RateioJUnit.core.exception.despesa;

public class DespesaCanceladaException extends RuntimeException {
    public DespesaCanceladaException(String message) {
        super(message);
    }
    public DespesaCanceladaException() {
        super("Despesa já cancelada");
    }
}
