package RateioJUnit.core.infra;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MessageRestError {

    private LocalDateTime timeStamp;
    private String message;
    private HttpStatus status;

    private Map<String,String>errorsMessage = new HashMap<>();


    public MessageRestError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public MessageRestError(HttpStatus status, String message, Map<String,String> errorsMessage)
    {
        this.status = status;
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.errorsMessage = errorsMessage;
    }
}
