package com.serverMonitor.config.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverMonitor.config.rest.resources.Statuses;
import lombok.Getter;
import lombok.Setter;

public class MVCResponseObject extends MVCResponse{
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("response")
    private Object response;

    public MVCResponseObject(int status, Object response){
        super(status);
        this.response = response;
    }

    public MVCResponseObject(Statuses status, Object response){
        super(status.ordinal());
        this.response = response;
    }

    @Override
    public String toString() {
        return "{" +
                "\"response\":" + response +
                ", \"status\":" + status +
                '}';
    }
}
