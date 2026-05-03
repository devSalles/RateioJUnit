package RateioJUnit.core.exception;

public class EmailNaoEncontradoException extends RuntimeException {
    public EmailNaoEncontradoException(String message) {
        super(message);
    }
    public EmailNaoEncontradoException() {
        super("Email de participante não encontrado");
    }
}
