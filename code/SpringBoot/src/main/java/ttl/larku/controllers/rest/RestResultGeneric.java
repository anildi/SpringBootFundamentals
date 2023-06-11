package ttl.larku.controllers.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;
import java.util.List;

public class RestResultGeneric<T> {

    public enum Status {
        Ok,
        Error
    }

    private Status status = Status.Error;

    private List<String> errors = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private T entity;
    
    public static <E> RestResultGeneric<E> ofValue(E value) {
    	return new RestResultGeneric<E>(Status.Ok).entity(value);
    }


    public static <E> RestResultGeneric<E> ofError(List<String> errors) {
    	return new RestResultGeneric<E>(Status.Error).errors(errors);
    }

    public static <E> RestResultGeneric<E> ofError(String ... errors) {
    	return new RestResultGeneric<E>(Status.Error).errors(errors);
    }
    
    //For Jackson etc.
    public RestResultGeneric() {}

    private RestResultGeneric(Status status) {
        this.status = status;
    }



    /**
     * "Builder type Api
     *
     * @param entity
     * @return
     */
    public RestResultGeneric<T> entity(T entity) {
        this.entity = entity;
        return this;
    }

    public RestResultGeneric<T> status(Status status) {
        this.status = status;
        return this;
    }

    public RestResultGeneric<T> errors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public RestResultGeneric<T> errors(String ... messages) {
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
