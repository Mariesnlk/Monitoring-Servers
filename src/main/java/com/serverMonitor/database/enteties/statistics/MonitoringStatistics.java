package com.serverMonitor.database.enteties.statistics;

import com.serverMonitor.database.enteties.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "monitoring_statistics")
public class MonitoringStatistics extends BaseEntity {

    @Column(name = "statistics_server")
    private Long statisticsServer;

    @Column(name = "statistics_command")
    @Type(type="text")
    private String statisticsCommand;

    @Column(name = "statistics_request")
    @Type(type="text")
    private String statisticsRequest;

    @Column(name = "statistics_status")
    private String statisticsStatus;

}
