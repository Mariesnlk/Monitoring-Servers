package com.serverMonitor.model.request;

import com.serverMonitor.database.enteties.monitoring.TypeMonitoring;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class MonitoringInfoRunCommandRequest {

    @NotNull
    @NotEmpty
    private TypeMonitoring typeMonitoring;

    @NotNull
    @NotEmpty
    private Long time;

    @NotNull
    @NotEmpty
    private Long serverId;

    @NotNull
    @NotEmpty
    private String expectedResponse;

    @NotNull
    @NotEmpty
    private String request;

    private boolean isReboot;

}
