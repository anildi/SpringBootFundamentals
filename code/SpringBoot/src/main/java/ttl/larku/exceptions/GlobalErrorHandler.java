package ttl.larku.exceptions;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import ttl.larku.controllers.rest.RestResultGeneric;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Two ways to handle global exceptions.  One is this class, the other
 * one is the LastStopHandler.
 * One key difference is that in the LastStopHandler you extend the ResponseEntityExceptionHandler
 * and override methods you are interested in.  Look at the code in the super class to see
 * what you can override.
 * This approach seems like the easier one, and is preferred by Spring.  That is, if you have an
 * exception handler declared here, it will be called in preference
 * to anything declared in the LastStopHandler.
 * @author whynot
 */
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalErrorHandler {


    /**
     * Handle BadRequest (400) errors.
     * <p>
     * From ResponseEntityExceptionHandler::handleExceptions, there are the
     * exceptions that result from 400 Bad Request status codes.  We are
     * trapping them a bunch of them in one place, and have specific
     * functions for others.
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class,
            ServletRequestBindingException.class, TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            BindException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResultGeneric<?> handleBadRequestException(Exception ex, WebRequest request) {
        RestResultGeneric<?> rr = RestResultGeneric.ofError("Unexpected Exception: " + ex);

        return rr;
    }

    /**
     * Handle validation errors for automatic validation, i.e with the @Valid annotation.
     * For this to be invoked, you have to have a controller argument of object type
     * to which you have attached the @Valid annotation.
     * Look at the end of StudentRestController for an example, which may be commented out
     * by default.
     * @param ex the exception thrown
     * @param request the incoming request
     * @return a bad request + restresult that contains the errors
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public RestResultGeneric<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        var errors = ex.getFieldErrors();
        List<String> errMsgs = errors.stream()
                .map(error -> "error:" + error.getField() + ": " + error.getDefaultMessage()
                        + ", supplied Value: " + error.getRejectedValue())
                .collect(toList());

        RestResultGeneric<?> rr = RestResultGeneric.ofError(errMsgs);

        return rr;
    }

    /**
     * Handle the case where input cannot be converted to the types specified
     * in controller arguments.
     * @param ex the exception thrown
     * @param request the incoming request
     * @return a bad request + restresult that contains the errors
     */
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    protected RestResultGeneric<?> handleMethodArgument(MethodArgumentTypeMismatchException ex, WebRequest request) {
        var errMessage = "MethodArgumentTypeMismatch: name: " + ex.getName() + ", value: " + ex.getValue() + ", message: " +
                ex.getMessage() + ", parameter: " + ex.getParameter();

        RestResultGeneric<?> rr = RestResultGeneric.ofError(errMessage);

        return rr;
    }
}