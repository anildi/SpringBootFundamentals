package ttl.larku.controllers.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ProblemDetail;

public class RestResultWrapper<T> {

    public enum Status {
        Ok,
        Error
    }

    private Status status = Status.Error;

    private List<String> errors = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private T entity;
    
    public static <E> RestResultWrapper<E> ofValue(E value) {
    	return new RestResultWrapper<E>(Status.Ok).entity(value);
    }

    public static <E> RestResultWrapper<E> ofError(ProblemDetail problemDetail) {
        var rr = new RestResultWrapper<E>(Status.Error);
        rr.getErrors().add("pdType: " + problemDetail.getType());
        rr.getErrors().add("pdTitle: " + problemDetail.getTitle());
        rr.getErrors().add("pdStatus: " + String.valueOf(problemDetail.getStatus()));
        rr.getErrors().add("pdDetail: " + problemDetail.getDetail());
        rr.getErrors().add("pdInstance: " + problemDetail.getInstance());
        return rr;
    }

    public static <E> RestResultWrapper<E> ofError(List<String> errors) {
    	return new RestResultWrapper<E>(Status.Error).errors(errors);
    }

    public static <E> RestResultWrapper<E> ofError(String ... errors) {
    	return new RestResultWrapper<E>(Status.Error).errors(errors);
    }
    
    //For Jackson etc.
    public RestResultWrapper() {}

    private RestResultWrapper(Status status) {
        this.status = status;
    }



    /**
     * "Builder type Api
     *
     * @param entity
     * @return
     */
    public RestResultWrapper<T> entity(T entity) {
        this.entity = entity;
        return this;
    }

    public RestResultWrapper<T> status(Status status) {
        this.status = status;
        return this;
    }

    public RestResultWrapper<T> errors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public RestResultWrapper<T> errors(String ... messages) {
    	for(String message : messages) {
    		this.errors.add(message);
    	}
        return this;
    }


    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getEntity() {
        return entity;
    }


    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "RestResult [status=" + status + ", errors=" + errors + ", entity=" + entity + "]";
    }
}
