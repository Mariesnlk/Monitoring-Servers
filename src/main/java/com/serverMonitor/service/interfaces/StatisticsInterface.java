package com.serverMonitor.service.interfaces;

import com.serverMonitor.database.enteties.statistics.ActivitiesStatistics;
import com.serverMonitor.database.enteties.statistics.MonitoringStatistics;
import com.serverMonitor.model.dto.ActivitiesStatisticsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StatisticsInterface {

    List<MonitoringStatistics> getAllMonitoringStatisticsByServerId(Long serverId);

    Page<ActivitiesStatistics> getAllActivitiesStatistics(Pageable pageable);

    void addActivitiesStatistics(Long serverInfoId, String action) ;

    void addActivitiesStatistics(ActivitiesStatisticsDTO activitiesStatisticsDTO);
}
