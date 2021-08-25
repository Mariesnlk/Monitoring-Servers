package com.serverMonitor.model.dto;

import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.enteties.server.ServerType;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerDTO {

    @NotNull
    @NotEmpty
    private String host;

    @NotNull
    @NotEmpty
    private String user;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private ServerType serverType;

    @ApiParam(access="hide")
    private boolean isMonitoring;

    @ApiParam(access="hide")
    private ServerStatus status;

}

