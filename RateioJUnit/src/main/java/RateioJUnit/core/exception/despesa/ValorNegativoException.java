package RateioJUnit.core.exception.despesa;

public class ValorNegativoException extends RuntimeException {
    public ValorNegativoException(String message) {
        super(message);
    }
    public ValorNegativoException() {
        super("Valor não pode ser negativo ou zero");
    }
}
