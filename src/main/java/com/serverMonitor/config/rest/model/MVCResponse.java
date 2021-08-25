package com.serverMonitor.config.rest.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverMonitor.config.rest.resources.Statuses;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class MVCResponse {
    @Getter
    @Setter
    @JsonProperty("status")
    protected int status;

    public MVCResponse(int status){
        this.status = status;
    }

    public MVCResponse(Statuses status){
        this.status = status.getStatus();
    }
}
