package com.serverMonitor.database.enteties.monitoring;


import com.serverMonitor.database.enteties.BaseEntity;
import com.serverMonitor.model.request.MonitoringInfoRunCommandRequest;
import com.serverMonitor.model.request.MonitoringInfoRunRequest;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "monitoring")
public class Monitoring extends BaseEntity {

    @Column(name = "server_id")
    private Long serverId;

    @Column(name = "port")
    private String port;

    @Column(name = "ping_method")
    @Enumerated(EnumType.STRING)
    private PingMethod pingMethod;

    @Column(name = "request")
    @Type(type="text")
    private String request;

    @Column(name = "expected_response")
    private String expectedResponse;

    @Column(name = "server_url")
    private String serverUrl;

    @Column(name = "reboot")
    private Boolean isReboot;

    @Column(name = "type_monitoring")
    @Enumerated(EnumType.STRING)
    private TypeMonitoring typeMonitoring;

    @NotNull
    @Column(name = "time")
    private Long time;

    @Column(name = "new_time")
    private Long pingTime;

}
