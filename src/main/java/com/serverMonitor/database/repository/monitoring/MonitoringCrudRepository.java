package com.serverMonitor.database.repository.monitoring;

import com.serverMonitor.database.enteties.monitoring.Monitoring;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MonitoringCrudRepository extends CrudRepository<Monitoring, Long> {

    Optional<Monitoring> findById(Long serverId);

    Monitoring getMonitoringByServerId(Long serverId);

    Boolean existsByServerId(Long serverId);

    List<Monitoring> findAll();

}
