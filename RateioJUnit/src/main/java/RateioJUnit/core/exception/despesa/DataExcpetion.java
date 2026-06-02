package RateioJUnit.core.exception.despesa;

public class DataExcpetion extends RuntimeException {
    public DataExcpetion(String message) {
        super(message);
    }
    public DataExcpetion() {
        super("Datas incorretas");
    }
}
