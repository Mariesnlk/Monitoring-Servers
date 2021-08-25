package com.serverMonitor.model.dto;

import com.serverMonitor.database.enteties.command.TypeCommand;
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
public class CommandDTO {

    @NotNull
    @NotEmpty
    private Long serverId;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String command;

    @NotNull
    @NotEmpty
    private TypeCommand typeCommand;

    private Long interval;
}
