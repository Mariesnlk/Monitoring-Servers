package com.serverMonitor.service.implementations.service;

import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.enteties.server.ServerType;
import com.serverMonitor.database.repository.monitoring.MonitoringCrudRepository;
import com.serverMonitor.database.repository.server.ServerCrudRepository;
import com.serverMonitor.model.dto.ActivitiesStatisticsDTO;
import com.serverMonitor.model.dto.ServerDTO;
import com.serverMonitor.exceptions.BadRequestException;
import com.serverMonitor.security.encryption.AES;
import com.serverMonitor.service.interfaces.ServerInterface;
import com.serverMonitor.utils.converting.Conversion;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServerService implements ServerInterface {

    private final ServerCrudRepository serverCrudRepository;
    private final MonitoringCrudRepository monitoringCrudRepository;
    private final StatisticsService statisticsService;
    private final static String SECRET_KEY = "fifi!fifi!!";


    @Override
    public void addServer(ServerDTO serverDTO) {
        String host = serverDTO.getHost();
        String title = serverDTO.getTitle();

        if (serverCrudRepository.existsByHostAndTitle(host, title))
            throw new BadRequestException("The server " + title + " is already exist!");

        serverCrudRepository.save(ServerInfo.builder()
                .host(host)
                .password(AES.encrypt(serverDTO.getPassword(), SECRET_KEY))
                .title(title)
                .user(serverDTO.getUser())
                .serverType(serverDTO.getServerType())
                .status(ServerStatus.NOT_ACTIVE_MONITORING)
                .isMonitoring(false)
                .build());

        statisticsService.addActivitiesStatistics(ActivitiesStatisticsDTO.builder()
                .serverTitle(title)
                .serverHost(host)
                .action("add server")
                .build());
    }

    @Override
    public ServerInfo getServerInfoById(Long serverId) {
        return serverCrudRepository.findServerById(serverId).orElseThrow(
                () -> new BadRequestException("The server does not exists")
        );
    }

    @Override
    public List<ServerInfo> getAllServers() {
        return serverCrudRepository.findAll();
    }


    @Override
    public List<ServerInfo> getAllServersByType(ServerType type) {

        return serverCrudRepository.findServersByServerType(type);
    }


    @Override
    public void deleteServer(Long serverId) {
        ServerInfo serverInfo = getServerInfoById(serverId);
        if (monitoringCrudRepository.existsByServerId(serverId)) {
            Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
            monitoringCrudRepository.delete(monitoring);
        }
        serverCrudRepository.delete(serverInfo);

        statisticsService.addActivitiesStatistics(ActivitiesStatisticsDTO.builder()
                .serverTitle(serverInfo.getTitle())
                .serverHost(serverInfo.getHost())
                .action("delete server")
                .build());
    }

    @Override
    public ServerInfo changeServerInfo(Long serverId, ServerDTO serverDTO) {

        ServerInfo serverInfo = getServerInfoById(serverId);
        String title = serverDTO.getTitle();
        serverInfo.setHost(serverDTO.getHost());
        serverInfo.setUser(serverDTO.getUser());
        serverInfo.setPassword(AES.encrypt(serverDTO.getPassword(), SECRET_KEY));
        serverInfo.setTitle(title);
        serverInfo.setServerType(serverDTO.getServerType());

        serverCrudRepository.save(serverInfo);

        return serverInfo;
    }

    @SneakyThrows
    public void pauseServerMonitoring(Long serverId) {

        ServerInfo serverInfo = getServerInfoById(serverId);
        serverInfo.setStatus(ServerStatus.PAUSE_MONITORING);
        serverInfo.setIsMonitoring(false);

        serverCrudRepository.save(serverInfo);
    }

    @SneakyThrows
    public void continueMonitoring(Long serverId) {

        ServerInfo serverInfo = getServerInfoById(serverId);
        serverInfo.setStatus(ServerStatus.ACTIVE_MONITORING);
        serverInfo.setIsMonitoring(true);

        Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
        monitoring.setCreated(new Date().getTime());
        monitoring.setPingTime(monitoring.getCreated() + Conversion.convertMinutesToMilliseconds(monitoring.getTime()));

        serverCrudRepository.save(serverInfo);
        monitoringCrudRepository.save(monitoring);
    }

    public void activeMonitoring(Long serverInfoId) {
        ServerInfo serverInfo = serverCrudRepository.findServerById(serverInfoId).orElseThrow(
                () -> new BadRequestException("The server does not exists")
        );
        serverInfo.setIsMonitoring(true);
        serverInfo.setStatus(ServerStatus.ACTIVE_MONITORING);

        serverCrudRepository.save(serverInfo);
    }

    public void disableMonitoring(Long serverInfoId) {
        ServerInfo serverInfo = serverCrudRepository.findServerById(serverInfoId).orElseThrow(
                () -> new BadRequestException("The server does not exists")
        );
        serverInfo.setIsMonitoring(false);
        serverInfo.setStatus(ServerStatus.NOT_ACTIVE_MONITORING);
        serverCrudRepository.save(serverInfo);
    }

    public void changeStatus(ServerInfo serverInfo, ServerStatus serverStatus) {
        serverInfo.setStatus(serverStatus);
        serverCrudRepository.save(serverInfo);
    }
}