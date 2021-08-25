package com.serverMonitor.config.rest.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverMonitor.config.rest.resources.Statuses;
import lombok.Getter;
import lombok.Setter;

public class MVCResponseError extends MVCResponse {
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("error")
    protected String error;

    MVCResponseError(int status) {
        super(status);
    }

    public MVCResponseError(int status, String error) {
        super(status);
        this.error=error;
    }

    public MVCResponseError(Statuses status, String error) {
        super(status.ordinal());
        this.error=error;
    }


    public static MVCResponseError getMvcErrorResponse(int status, String errorMsg) {
        MVCResponseError mvc = new MVCResponseError(status);
        mvc.setError(errorMsg);
        return mvc;
    }

    public static MVCResponseError getMvcErrorResponse(Statuses status, String errorMsg) {
        MVCResponseError mvc = new MVCResponseError(status.ordinal());
        mvc.setError(errorMsg);
        return mvc;
    }

    @Override
    public String toString() {
        return "{\"status\":" + status + ", \"error\":" + error + "}";
    }
}

