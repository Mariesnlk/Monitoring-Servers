package com.serverMonitor.model.request;

import com.serverMonitor.database.enteties.monitoring.PingMethod;
import com.serverMonitor.database.enteties.monitoring.TypeMonitoring;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class MonitoringInfoRunRequest {

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
    private String port;

    @NotNull
    @NotEmpty
    private String serverUrl;

    @NotNull
    @NotEmpty
    private PingMethod pingMethod;

    @NotNull
    @NotEmpty
    private String expectedResponse;

    private boolean isReboot;

}
