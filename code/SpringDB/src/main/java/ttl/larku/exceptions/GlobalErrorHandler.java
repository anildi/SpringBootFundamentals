package ttl.larku.exceptions;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import ttl.larku.controllers.rest.RestResultWrapper;
import ttl.larku.controllers.rest.UriCreator;

import static java.util.stream.Collectors.toList;

/**
 * Two ways to handle global exceptions.  One is this class, the other
 * one is the LastStopHandler.
 * One key difference is that in the LastStopHandler you extend the ResponseEntityExceptionHandler
 * and override methods you are interested in.  Look at the code in ResponseEntityExceptionHandler
 * to see what you can override.
 * This approach seems like the easier one, and is preferred by Spring.  That is, if you have an
 * exception handler declared here, it will be called in preference
 * to anything declared in the LastStopHandler.
 * @author whynot
 */
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalErrorHandler {

    @Autowired
    private UriCreator uriCreator;

    /**
     * Handle BadRequest (400) errors.
     * <p>
     * From ResponseEntityExceptionHandler::handleExceptions, these are the
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
    public RestResultWrapper<?> handleBadRequestException(Exception ex, WebRequest request) {
        RestResultWrapper<?> rr = RestResultWrapper.ofError("Unexpected Exception: " + ex);

        return rr;
    }

//    @ExceptionHandler(value = {AccessDeniedException.class})
//    protected ResponseEntity<RestResultWrapper<?>> accessDenied(AccessDeniedException ex, WebRequest request) {
//        RestResultWrapper<String> rr = RestResultWrapper.ofError("LSH AccessDenied: " + ex);
//        return ResponseEntity.status(403).body(rr);
//    }
//
//    @ExceptionHandler(value = {AuthenticationException.class})
//    protected ResponseEntity<RestResultWrapper<?>> authException(AuthenticationException ex, WebRequest request) {
//        RestResultWrapper<String> rr = RestResultWrapper.ofError("LSH AuthenticationException: " + ex);
//        return ResponseEntity.status(401).body(rr);
//    }

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
    public RestResultWrapper<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                             WebRequest request) {
        var errors = ex.getFieldErrors();
        List<String> errMsgs = errors.stream()
                .map(error -> "@Valid error:" + error.getField() + ": " + error.getDefaultMessage()
                        + ", supplied Value: " + error.getRejectedValue())
                .collect(toList());

        RestResultWrapper<?> rr = RestResultWrapper.ofError(errMsgs);

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
    protected ResponseEntity<RestResultWrapper<?>> handleMethodArgument(MethodArgumentTypeMismatchException ex, WebRequest request) {
        var errMessage = "MethodArgumentTypeMismatch: name: " + ex.getName() + ", value: " + ex.getValue() + ", message: " +
                ex.getMessage() + ", parameter: " + ex.getParameter();

        RestResultWrapper<?> rr = RestResultWrapper.ofError(errMessage);

        return ResponseEntity.badRequest().body(rr);
    }

    /**
     * When the Accept header is "Not Acceptable".  This one is interesting
     * because if they have sent you an Accept header you can't respond to,
     * then in what representation should you send the result back.
     * For now we are converting it to a String and hoping for the best.
     * Maybe "text/plain" can squeak through because of some default somewhere.
     *
     * @param ex The exception that got thrown
     * @param request The web request
     * @return A ResponseEntity with the status NOT_ACCEPTABLE and
     *         a RestResultGeneric with the errors and supported types
     */
    @ExceptionHandler(value = {HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    protected String handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, WebRequest request) {
        List<String> errMessage = new ArrayList<>();
                errMessage.add("HttpMediaTypeNotAcceptable: " + ex.getMessage());
               errMessage.add("Supported Types: ");
               errMessage.addAll(ex.getSupportedMediaTypes().stream().map(MimeType::toString).toList());



        RestResultWrapper<?> rr = RestResultWrapper.ofError(errMessage);

//        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(rr.toString());
        return rr.toString();
    }

    /**
     * When the Content-Type header is missing or invalid.
     *
     * @param ex The exception that got thrown
     * @param request The web request
     * @return A ResponseEntity with the status UNSUPPORTED_MEDIA_TYPE and
     *         a RestResultGeneric with the errors and supported types
     */
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    protected ResponseEntity<RestResultWrapper<?>> handleMediaNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        List<String> errMessage = new ArrayList<>();
        errMessage.add("HttpMediaTypeNotSupported: " + ex.getMessage());
        errMessage.add("Supported Types: ");
        errMessage.addAll(ex.getSupportedMediaTypes().stream().map(MimeType::toString).toList());



        RestResultWrapper<?> rr = RestResultWrapper.ofError(errMessage);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(rr);
    }


    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected RestResultWrapper<?> lastPortOfCall(Exception ex, WebRequest request) {

//        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
//              "Unexpected Exception: " + ex);
//        RestResultWrapper<?> rr = RestResultWrapper.ofError(pd);

        RestResultWrapper<?> rr = RestResultWrapper.ofError("Last Port ;Unexpected Exception: " + ex);

        return rr;
    }
}