package RateioJUnit.core.exception.despesa;

public class DespesaJaFinalizadaException extends RuntimeException {
    public DespesaJaFinalizadaException(String message) {
        super(message);
    }
    public DespesaJaFinalizadaException() {
        super("Despesa finalizada não pode ser cancelada");
    }
}
