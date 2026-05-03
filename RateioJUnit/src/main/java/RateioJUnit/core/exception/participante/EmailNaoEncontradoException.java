package RateioJUnit.core.exception.participante;

public class EmailNaoEncontradoException extends RuntimeException {
    public EmailNaoEncontradoException(String message) {
        super(message);
    }
    public EmailNaoEncontradoException() {
        super("Email de participante não encontrado");
    }
}
