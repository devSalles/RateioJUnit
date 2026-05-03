package RateioJUnit.core.exception;

public class NomeNaoEncontradoException extends RuntimeException {
    public NomeNaoEncontradoException(String message) {
        super(message);
    }
    public NomeNaoEncontradoException() {
        super("Nome do participante não encontrado");
    }
}
