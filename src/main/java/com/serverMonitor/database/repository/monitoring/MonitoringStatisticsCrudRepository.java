package com.serverMonitor.database.repository.monitoring;

import com.serverMonitor.database.enteties.statistics.MonitoringStatistics;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonitoringStatisticsCrudRepository extends CrudRepository<MonitoringStatistics, Long> {

    List<MonitoringStatistics> findAllByStatisticsServer(Long serverId);

    List<MonitoringStatistics> deleteMonitoringStatisticsByStatisticsServer(Long serverId);

    Boolean existsByStatisticsServer(Long serverId);
}
