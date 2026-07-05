package RateioJUnit.core.exception.despesa;

public class ValorTotalInvalidoException extends RuntimeException {
    public ValorTotalInvalidoException(String message) {
        super(message);
    }
    public ValorTotalInvalidoException() {
        super("Valor total inválido");
    }
}
