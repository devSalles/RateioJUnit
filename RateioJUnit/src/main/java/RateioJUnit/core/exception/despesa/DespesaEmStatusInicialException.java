package RateioJUnit.core.exception.despesa;

public class DespesaEmStatusInicialException extends RuntimeException {
    public DespesaEmStatusInicialException(String message) {
        super(message);
    }
    public DespesaEmStatusInicialException() {
        super("Despesa com esse status não pode ser modificada");
    }
}
