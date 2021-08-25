package com.serverMonitor.database.enteties.command;

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
@Table(name = "command")
public class Command extends BaseEntity {

    @Column(name = "server_id")
    private Long serverId;

    @NotNull @NotEmpty
    @Column(name = "command_title")
    private String title;

    @Lob @NotNull @NotEmpty
    @Column(name = "server_command")
    private String command;

    @Column(name = "type_command")
    @Enumerated(EnumType.STRING)
    private TypeCommand typeCommand;

    @NotNull @NotEmpty
    @Column(name = "execute_interval")
    private Long interval;

}
