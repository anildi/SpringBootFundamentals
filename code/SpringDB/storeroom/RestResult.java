package ttl.larku.domain;

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

    public RestResult() {
    }

    public RestResult(String message) {
        errors.add(message);
        //We assume that creating with a message means errors
        this.status = Status.Error;
    }

    public RestResult(Status status, String message) {
        this.status = status;
        errors.add(message);
    }


    public RestResult(Status status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public RestResult(Object entity) {
        this.entity = entity;
    }

    public RestResult(Status status, Object entity) {
        this.status = status;
        this.entity = entity;
    }


    /**
     * "Builder type Api
     *
     * @param entity
     * @return
     */
    public RestResult entity(Object entity) {
        this.entity = entity;
        return this;
    }

    public RestResult status(Status status) {
        this.status = status;
        return this;
    }

    @JsonIgnore
    public String getErrorMessage() {
        return errors.get(0);
    }

    @JsonIgnore
    public void setErrorMessage(String errorMessage) {
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

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "RestResult [status=" + status + ", errors=" + errors + ", entity=" + entity + "]";
    }
}
