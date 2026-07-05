package RateioJUnit.core.infra;

import RateioJUnit.core.exception.*;
import RateioJUnit.core.exception.despesa.*;
import RateioJUnit.core.exception.participante.*;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageRestError> globalExceptionHandler()
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.INTERNAL_SERVER_ERROR,"Erro interno, tente novamente mais tarde!");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageRestError);
    }

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

    @ExceptionHandler(NenhumRegistroException.class)
    public ResponseEntity<MessageRestError> NenhumRegistroException(NenhumRegistroException ex)
    {
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

    @ExceptionHandler(ParticipanteInvalidoException.class)
    public ResponseEntity<MessageRestError> ParticipanteInvalidoException(ParticipanteInvalidoException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    //----------- EXCEÇÕES DESPESA -----------

    @ExceptionHandler(ValorTotalInvalidoException.class)
    public ResponseEntity<MessageRestError> ValorTotalInvalidoException(ValorTotalInvalidoException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    @ExceptionHandler(ValorNegativoException.class)
    public ResponseEntity<MessageRestError> ValorNegativoException(ValorNegativoException ex){
        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    @ExceptionHandler(PagadorNaoEstaNaListaException.class)
    public ResponseEntity<MessageRestError> PagadorNaoEstaNaListaException(PagadorNaoEstaNaListaException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    @ExceptionHandler(DespesaInexistenteException.class)
    public ResponseEntity<MessageRestError> DespesaInexistenteException(DespesaInexistenteException ex){
        MessageRestError messageRestError = new MessageRestError(HttpStatus.NOT_FOUND,ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageRestError);
    }

    @ExceptionHandler(DiferenteValorTotalException.class)
    public ResponseEntity<MessageRestError> DiferenteValorTotalException(DiferenteValorTotalException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.BAD_REQUEST,ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageRestError);
    }

    @ExceptionHandler(ParticipantesDuplicadosException.class)
    public ResponseEntity<MessageRestError> ParticipantesDuplicadosException(ParticipantesDuplicadosException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }

    @ExceptionHandler(DespesaEmStatusInicialException.class)
    public ResponseEntity<MessageRestError> DespesaEmStatusInicialException(DespesaEmStatusInicialException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }

    @ExceptionHandler(DespesaJaFinalizadaException.class)
    public ResponseEntity<MessageRestError> DespesaJaFinalizadaException(DespesaJaFinalizadaException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }

    @ExceptionHandler(DespesaCanceladaException.class)
    public ResponseEntity<MessageRestError> DespesaCanceladaException(DespesaCanceladaException ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }

    @ExceptionHandler(DataExcpetion.class)
    public ResponseEntity<MessageRestError> DataExcpetion(DataExcpetion ex)
    {
        MessageRestError messageRestError = new MessageRestError(HttpStatus.CONFLICT,ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageRestError);
    }
}