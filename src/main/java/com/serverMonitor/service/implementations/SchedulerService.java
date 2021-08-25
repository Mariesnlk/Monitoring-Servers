package com.serverMonitor.service.implementations;

import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.monitoring.TypeMonitoring;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.repository.command.CommandCrudRepository;
import com.serverMonitor.database.repository.monitoring.MonitoringCrudRepository;
import com.serverMonitor.service.implementations.service.MonitoringService;
import com.serverMonitor.service.implementations.service.RunScenarioService;
import com.serverMonitor.service.implementations.service.ServerService;
import com.serverMonitor.service.implementations.service.StatisticsService;
import com.serverMonitor.service.implementations.telegramModule.ChatBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@EnableScheduling
@Component
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final MonitoringService monitoringService;
    private final MonitoringCrudRepository monitoringCrudRepository;
    private final ServerService serverService;
    private final StatisticsService statisticsService;
    private final ChatBotService chatBotService;
    private final RunScenarioService runScenarioService;
    public static List<Monitoring> monitoringList = new ArrayList<>();
    public static List<ServerInfo> serverInfoList = new ArrayList<>();


    @Scheduled(initialDelay = 120_000, fixedDelay = 60_000)
    public void checkServersForPinging() {
        long currentTime = new Date().getTime();
        monitoringList = monitoringCrudRepository.findAll();
        ExecutorService executorService = Executors.newFixedThreadPool(monitoringList.size());
        monitoringList.forEach(
                monitoringServer -> {
                    ServerInfo serverInfo = serverService.getServerInfoById(monitoringServer.getServerId());
                    if (!(serverInfo.getStatus() == ServerStatus.PAUSE_MONITORING)) {
                        executorService.submit(() -> {

                            if (currentTime >= monitoringServer.getPingTime()) {

                                if (monitoringServer.getTypeMonitoring().equals(TypeMonitoring.RUN_COMMAND)) {
                                    monitoringService.checkExpectedResponse(monitoringServer);
                                } else if (monitoringServer.getTypeMonitoring().equals(TypeMonitoring.RUN_REQUEST)) {
                                    monitoringService.pingServer(monitoringServer);
                                }

                            }

                        });
                    }
                });
    }

    @Scheduled(cron = "0 0 5 * * ?")
    public void rebuildServersOnceADay() {
        serverInfoList = serverService.getAllServers();
        ExecutorService executorService = Executors.newFixedThreadPool(serverInfoList.size());
        serverInfoList.forEach(
                server ->
                        executorService.submit(() -> {
                            runScenarioService.runRebuildOnceADay(server.getId(), TypeCommand.REBUILD, ServerStatus.REBUILDING);
                            statisticsService.addActivitiesStatistics(server.getId(), "rebuild once a day");
                        }));

    }

    @Scheduled(cron = "0 3 5 * * ?")
    public void setStatusRebuildServersOnceADay() {
        serverInfoList = serverService.getAllServers();
        ExecutorService executorService = Executors.newFixedThreadPool(serverInfoList.size());
        serverInfoList.forEach(
                server ->
                        executorService.submit(() -> {
                            if (server.getStatus() == ServerStatus.REBUILDING) {
                                if (monitoringCrudRepository.existsByServerId(server.getId())) {
                                    serverService.activeMonitoring(server.getId());
                                } else {
                                    serverService.disableMonitoring(server.getId());
                                }
                                chatBotService.broadcast("\uD83D\uDFE2The server " + server.getTitle() + " is success rebuilding");
                            }
                        }));
    }

}