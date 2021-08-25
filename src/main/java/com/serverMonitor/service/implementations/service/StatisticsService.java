package com.serverMonitor.service.implementations.service;

import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.statistics.ActivitiesStatistics;
import com.serverMonitor.database.enteties.statistics.MonitoringStatistics;
import com.serverMonitor.database.repository.ActivitiesStatisticsJpaRepository;
import com.serverMonitor.database.repository.monitoring.MonitoringStatisticsCrudRepository;
import com.serverMonitor.database.repository.monitoring.MonitoringStatisticsJpaRepository;
import com.serverMonitor.database.repository.server.ServerCrudRepository;
import com.serverMonitor.model.dto.ActivitiesStatisticsDTO;
import com.serverMonitor.exceptions.BadRequestException;
import com.serverMonitor.service.interfaces.StatisticsInterface;
import com.serverMonitor.utils.page.InnerPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StatisticsService implements StatisticsInterface {

    private final MonitoringStatisticsJpaRepository monitoringStatisticsJpaRepository;
    private final MonitoringStatisticsCrudRepository monitoringStatisticsCrudRepository;
    private final ActivitiesStatisticsJpaRepository activitiesStatisticsJpaRepository;
    private final ServerCrudRepository serverCrudRepository;

    @Override
    public List<MonitoringStatistics> getAllMonitoringStatisticsByServerId(Long serverId) {
        return monitoringStatisticsCrudRepository.findAllByStatisticsServer(serverId);
    }

    @Override
    public Page<ActivitiesStatistics> getAllActivitiesStatistics(Pageable pageable) {
        return InnerPage.getPageFromList(activitiesStatisticsJpaRepository.findAllByOrderByCreatedDesc(), pageable);
    }

    @Override
    public void addActivitiesStatistics(Long serverInfoId, String action) {
        ServerInfo serverInfo = serverCrudRepository.findServerById(serverInfoId).orElseThrow(
                () -> new BadRequestException("The server does not exists")
        );
        activitiesStatisticsJpaRepository.save(ActivitiesStatistics.builder()
                .serverTitle(serverInfo.getTitle())
                .serverHost(serverInfo.getHost())
                .action(action)
                .build());
    }

    @Override
    public void addActivitiesStatistics(ActivitiesStatisticsDTO activitiesStatisticsDTO) {
        activitiesStatisticsJpaRepository.save(ActivitiesStatistics.builder()
                .serverTitle(activitiesStatisticsDTO.getServerTitle())
                .serverHost(activitiesStatisticsDTO.getServerHost())
                .action(activitiesStatisticsDTO.getAction())
                .build());
    }

    public void saveMonitoringStatistics(String command, String request, String status, Long serverId){

        monitoringStatisticsJpaRepository.save(MonitoringStatistics.builder()
                .statisticsCommand(command)
                .statisticsRequest(request)
                .statisticsStatus(status)
                .statisticsServer(serverId)
                .build());
    }

}