package RateioJUnit.core.infra;

import RateioJUnit.core.exception.*;
import RateioJUnit.core.exception.participante.EmailNaoEncontradoException;
import RateioJUnit.core.exception.participante.EmailRepetidoCadastradoException;
import RateioJUnit.core.exception.participante.NomeNaoEncontradoException;
import RateioJUnit.core.exception.participante.ParticipantePossuiDespesasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class HandlerException {

    //----------- EXCEÇÕES GLOBAIS -----------

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<MessageRestError> globalExceptionHandler()
//    {
//        MessageRestError messageRestError = new MessageRestError(HttpStatus.INTERNAL_SERVER_ERROR,"Erro interno, tente novamente mais tarde!");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageRestError);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageRestError> inputErrorHandler(MethodArgumentNotValidException ex)
    {
        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error->errors.put(error.getField(),error.getDefaultMessage()));

        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,"Erro, verifique os dados",errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    @ExceptionHandler(IdNaoEncontradoException.class)
    public ResponseEntity<MessageRestError> IdNaoEncontradoException(IdNaoEncontradoException ex){
        MessageRestError messageRestError = new MessageRestError(HttpStatus.NOT_FOUND,ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageRestError);
    }

    //----------- EXCEÇÕES PARTICIPANTE -----------

    @ExceptionHandler({
            EmailNaoEncontradoException.class,
            NomeNaoEncontradoException.class
    })
    public ResponseEntity<MessageRestError> EmailENomeNaoEncontradoException(Exception ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.NOT_FOUND,ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageRestError);
    }

    @ExceptionHandler(EmailRepetidoCadastradoException.class)
    public ResponseEntity<MessageRestError> EmailRepetidoCadastradoException(EmailRepetidoCadastradoException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }

    @ExceptionHandler(ParticipantePossuiDespesasException.class)
    public ResponseEntity<MessageRestError> ParticipantePossuiDespesasException(ParticipantePossuiDespesasException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }
}
