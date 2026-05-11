package RateioJUnit.core.exception.despesa;

public class PagadorNaoEstaNaListaException extends RuntimeException {
    public PagadorNaoEstaNaListaException(String message) {
        super(message);
    }
    public PagadorNaoEstaNaListaException() {
        super("Pagador precisa estar na lista");
    }
}
