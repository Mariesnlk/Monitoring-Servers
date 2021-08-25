package com.serverMonitor.database.enteties.telegram;

import com.serverMonitor.database.enteties.BaseEntity;
import com.serverMonitor.database.enteties.server.ServerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "telegram_info")
public class TelegramInfo extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "chat_id")
    private Long chartId;

    @JsonIgnore
    @Column(name = "state_id")
    private Integer stateId;

    @JsonIgnore
    @Column(name = "admin")
    private Boolean admin;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @JsonIgnore
    @Column(name = "server_title")
    private String serverTitle;

    @JsonIgnore
    @Column(name = "request")
    private String request;


    public TelegramInfo(Long chartId, Integer stateId) {
        this.chartId = chartId;
        this.stateId = stateId;
    }
}
