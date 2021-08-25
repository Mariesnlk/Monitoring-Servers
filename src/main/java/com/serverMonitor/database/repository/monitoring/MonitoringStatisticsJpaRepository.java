package com.serverMonitor.database.repository.monitoring;

import com.serverMonitor.database.enteties.statistics.MonitoringStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringStatisticsJpaRepository extends JpaRepository<MonitoringStatistics, Long> {
}
