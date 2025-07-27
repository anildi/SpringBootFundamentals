package ttl.larku.controllers.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;
import java.util.List;

public class RestResult {

    public enum Status {
        Ok,
        Error
    }

    private Status status = Status.Ok;

    private List<String> errors = new ArrayList<>();

    //We set this so Jackson allows null entities
    @JsonInclude(Include.NON_NULL)
    private Object entity;

    //Need this for Jackson
    public RestResult() {}

    public static RestResult ofValue(Object value) {
        return new RestResult(RestResult.Status.Ok).entity(value);
    }

    public static RestResult ofNoContent() {
        return new RestResult(RestResult.Status.Ok);
    }


    public static RestResult ofError(List<String> errors) {
        return new RestResult(RestResult.Status.Error).errors(errors);
    }

    public static RestResult ofError(String ... errors) {
        return new RestResult(RestResult.Status.Error).errors(List.of(errors));
    }


    private RestResult(RestResult.Status status) {
        this.status = status;
    }

    /**
     * "Builder type Api
     *
     * @param entity
     * @return
     */
    private RestResult entity(Object entity) {
        this.entity = entity;
        return this;
    }

    private RestResult status(Status status) {
        this.status = status;
        return this;
    }

    private RestResult errors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    @JsonIgnore
    public String getErrorMessage() {
        return errors.get(0);
    }

    @JsonIgnore
    public void addErrorMessage(String errorMessage) {
        errors.add(0, errorMessage);
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

    public Object getEntity() {
        return entity;
    }

    public <T> T getEntity(Class<T> expectedClass) {
        if(status == Status.Error) {
            throw new UnsupportedOperationException("Result is an Error, No entity available");
        }
        return expectedClass.cast(entity);
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "RestResult [status=" + status + ", errors=" + errors + ", entity=" + entity + "]";
    }
}
