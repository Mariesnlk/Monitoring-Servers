package com.serverMonitor.database.enteties.server;

import com.serverMonitor.database.enteties.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "server")
public class ServerInfo extends BaseEntity {

    @NotNull
    @NotEmpty
    @Column(name = "host")
    private String host;

    @NotNull
    @NotEmpty
    @Column(name = "user")
    private String user;

    @NotNull
    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotNull
    @NotEmpty
    @Column(name = "title")
    private String title;

    @NotNull
    @NotEmpty
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @Column(name = "monitoring")
    private Boolean isMonitoring;

    @NotNull
    @NotEmpty
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ServerStatus status;

}
