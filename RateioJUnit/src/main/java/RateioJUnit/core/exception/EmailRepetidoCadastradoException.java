package RateioJUnit.core.exception;

public class EmailRepetidoCadastradoException extends RuntimeException {
    public EmailRepetidoCadastradoException(String message) {
        super(message);
    }
    public EmailRepetidoCadastradoException() {
        super("O email já foi cadastrado");
    }
}
