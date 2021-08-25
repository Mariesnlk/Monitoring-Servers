package com.serverMonitor.model.dto;

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
public class ActivitiesStatisticsDTO {

    @NotNull
    @NotEmpty
    private String serverTitle;

    @NotNull
    @NotEmpty
    private String serverHost;

    @NotNull
    @NotEmpty
    private String action;
}
