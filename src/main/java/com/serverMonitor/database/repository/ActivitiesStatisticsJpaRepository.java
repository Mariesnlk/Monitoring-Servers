package com.serverMonitor.database.repository;

import com.serverMonitor.database.enteties.statistics.ActivitiesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivitiesStatisticsJpaRepository extends JpaRepository<ActivitiesStatistics, Long> {

    List<ActivitiesStatistics> findAllByOrderByCreatedDesc();

}
