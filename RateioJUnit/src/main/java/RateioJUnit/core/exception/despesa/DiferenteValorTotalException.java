package RateioJUnit.core.exception.despesa;

public class DiferenteValorTotalException extends RuntimeException {
    public DiferenteValorTotalException(String message) {
        super(message);
    }
    public DiferenteValorTotalException() {
        super("A soma e diferente do valor total");
    }
}
