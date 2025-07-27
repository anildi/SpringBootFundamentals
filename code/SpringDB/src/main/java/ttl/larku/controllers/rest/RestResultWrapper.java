package ttl.larku.controllers.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

public class RestResultWrapper<T> {

    public enum Status {
        Ok,
        Error
    }

    private Map<String, Object> extraProps = new HashMap<>();

    private Status status = Status.Error;

    private List<String> errors = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private T entity;
    
    public static <E> RestResultWrapper<E> ofValue(E value) {
    	return new RestResultWrapper<E>(Status.Ok).entity(value);
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

    public RestResultWrapper<T> addProp(String name, Object value) {
        extraProps.put(name, value);
        return this;
    }

    public Object getProp(String name) {
        return extraProps.get(name);
    }

    public Map<String, Object> getExtraProps() {
        return extraProps;
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
