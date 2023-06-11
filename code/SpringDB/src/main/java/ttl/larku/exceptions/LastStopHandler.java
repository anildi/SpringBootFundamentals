package ttl.larku.exceptions;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ttl.larku.controllers.rest.RestResultGeneric;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class LastStopHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected RestResultGeneric<?> lastPortOfCall(Exception ex, WebRequest request) {
        RestResultGeneric<?> rr = RestResultGeneric.ofError("Unexpected Exception: " + ex);
        return rr;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RestResultGeneric<?> rr = RestResultGeneric.ofError(ex.getMessage());
        return ResponseEntity.badRequest().body(rr);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    /**
     * Used to deal with requests which do not pass the @Valid test.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        BindingResult br = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        for (FieldError e : br.getFieldErrors()) {
            errors.add(e.getField() + ": " + e.getDefaultMessage() + ", rejected value is " + e.getRejectedValue());
        }

        RestResultGeneric<?> rr = RestResultGeneric.ofError(errors);

        return ResponseEntity.badRequest().body(rr);

    }

    /**
     * Try sending an Accept of application/json, and change the controller
     * to only produce xml.  This will lead to a MediaTypeNotAcceptable situation,
     * and Spring will call this function if we have it set up.  This allows us to
     * customize the response we want to send back.
     * <p>
     * Note however, that we have to have a way to convert the RestResult to the
     * MediaType that the client wants.  Right now we can only convert to XML
     * or Json, so if the client wants a representation
     * that is not XML or Json, this method will fail miserably because it will not be
     * able to convert the RestResult.  It will get called twice, and then the client
     * will get an empty 406.
     */
    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add("Unsupported Media Type - " + request.getHeader(HttpHeaders.ACCEPT));
        errors.add("Supported Types are: ");
        List<MediaType> supportedTypes = ex.getSupportedMediaTypes();
        for (MediaType mt : supportedTypes) {
            errors.add(mt.toString());
        }

        RestResultGeneric<?> rr = RestResultGeneric.ofError(errors);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(rr);
    }
}
