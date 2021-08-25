package com.serverMonitor.model.dto;

import com.serverMonitor.database.enteties.monitoring.PingMethod;
import com.serverMonitor.database.enteties.monitoring.TypeMonitoring;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonitoringDTO {

    @NotNull
    @NotEmpty
    private TypeMonitoring typeMonitoring;

    @NotNull
    @NotEmpty
    private Long time;

    @NotNull
    @NotEmpty
    private Long serverId;

    private String port;
    private String serverUrl;
    private PingMethod pingMethod;
    private String expectedResponse;
    private String request;
    private Boolean isReboot;

}
