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
@Table(name = "activities_statistics")
public class ActivitiesStatistics extends BaseEntity {

    @Column(name = "server_title")
    private String serverTitle;

    @Column(name = "server_host")
    private String serverHost;

    @Column(name = "action")
    private String action;

}
