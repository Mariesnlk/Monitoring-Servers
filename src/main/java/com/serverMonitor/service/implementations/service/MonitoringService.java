package com.serverMonitor.service.implementations.service;


import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.monitoring.PingMethod;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.repository.monitoring.MonitoringCrudRepository;
import com.serverMonitor.model.request.MonitoringInfoRunCommandRequest;
import com.serverMonitor.model.request.MonitoringInfoRunRequest;
import com.serverMonitor.exceptions.BadRequestException;
import com.serverMonitor.model.Server;
import com.serverMonitor.service.implementations.telegramModule.ChatBotService;
import com.serverMonitor.service.interfaces.MonitoringInterface;
import com.serverMonitor.utils.converting.Conversion;
import com.serverMonitor.utils.requests.RequestHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringService implements MonitoringInterface {

    private final RunScenarioService runScenarioService;
    private final MonitoringCrudRepository monitoringCrudRepository;
    private final ChatBotService chatBotService;
    private final ServerService serverService;
    private final StatisticsService statisticsService;


    @Override
    public void addMonitoringRunRequest(MonitoringInfoRunRequest monitoringInfoRunRequest) throws Exception {
        if (monitoringCrudRepository.existsByServerId(monitoringInfoRunRequest.getServerId()))
            throw new BadRequestException("The server is already pinging");

        Long serverId = monitoringInfoRunRequest.getServerId();

        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        String host = serverInfo.getHost();
        String port = monitoringInfoRunRequest.getPort();


        //monitoring.setPingTime(monitoring.getCreated() + Conversion.convertMinutesToMilliseconds(monitoring.getTime()));
        serverService.activeMonitoring(serverId);
        monitoringCrudRepository.save(Monitoring.builder()
                .serverId(serverId)
                .typeMonitoring(monitoringInfoRunRequest.getTypeMonitoring())
                .pingMethod(monitoringInfoRunRequest.getPingMethod())
                .expectedResponse(monitoringInfoRunRequest.getExpectedResponse())
                .port(port)
                .serverUrl("http://" + host + ":" + port + monitoringInfoRunRequest.getServerUrl())
                .pingTime(new Date().getTime() + monitoringInfoRunRequest.getTime())
                .isReboot(false)
                .build());

        statisticsService.addActivitiesStatistics(serverId, "add monitoring");
    }

    @Override
    public void addMonitoringRunCommand(MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) throws Exception {
        if (monitoringCrudRepository.existsByServerId(monitoringInfoRunCommandRequest.getServerId()))
            throw new BadRequestException("The server is already pinging");

        Long serverId = monitoringInfoRunCommandRequest.getServerId();

//        monitoring.setPingTime(monitoring.getCreated() + Conversion.convertMinutesToMilliseconds(monitoring.getTime()));
        serverService.activeMonitoring(serverId);
        monitoringCrudRepository.save(Monitoring.builder()
                .serverId(serverId)
                .typeMonitoring(monitoringInfoRunCommandRequest.getTypeMonitoring())
                .pingTime(new Date().getTime() + monitoringInfoRunCommandRequest.getTime())
                .expectedResponse(monitoringInfoRunCommandRequest.getExpectedResponse())
                .request(monitoringInfoRunCommandRequest.getRequest())
                .isReboot(false)
                .build());

        statisticsService.addActivitiesStatistics(serverId, "add monitoring");
    }

    @Override
    public void deleteMonitoring(Long serverId) {
        Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
        serverService.disableMonitoring(serverId);
        monitoringCrudRepository.delete(monitoring);

        statisticsService.addActivitiesStatistics(serverId, "delete monitoring");
    }


    @Override
    public Monitoring updateMonitoringInfoByRunCommand(MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) {

        Monitoring info = monitoringCrudRepository.getMonitoringByServerId(monitoringInfoRunCommandRequest.getServerId());

        info.setCreated(new Date().getTime());
        info.setTypeMonitoring(monitoringInfoRunCommandRequest.getTypeMonitoring());
        info.setTime(monitoringInfoRunCommandRequest.getTime());
        info.setRequest(monitoringInfoRunCommandRequest.getRequest());
        info.setExpectedResponse(monitoringInfoRunCommandRequest.getExpectedResponse());
        info.setPingTime(new Date().getTime() + Conversion.convertMinutesToMilliseconds(monitoringInfoRunCommandRequest.getTime()));

        return monitoringCrudRepository.save(info);
    }

    @Override
    public Monitoring updateMonitoringInfoByRunRequest(MonitoringInfoRunRequest monitoringInfoRunRequest) {
        Long serverId = monitoringInfoRunRequest.getServerId();

        Monitoring info = monitoringCrudRepository.getMonitoringByServerId(serverId);

        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        String host = serverInfo.getHost();
        String port = monitoringInfoRunRequest.getPort();

        info.setCreated(new Date().getTime());
        info.setTypeMonitoring(monitoringInfoRunRequest.getTypeMonitoring());
        info.setTime(monitoringInfoRunRequest.getTime());
        info.setServerUrl("http://" + host + ":" + port + monitoringInfoRunRequest.getServerUrl());
        info.setPort(monitoringInfoRunRequest.getPort());
        info.setPingMethod(monitoringInfoRunRequest.getPingMethod());
        info.setExpectedResponse(monitoringInfoRunRequest.getExpectedResponse());
        info.setPingTime(new Date().getTime() + Conversion.convertMinutesToMilliseconds(monitoringInfoRunRequest.getTime()));

        return monitoringCrudRepository.save(info);
    }

    @Override
    public Monitoring getMonitoringInfo(Long serverId) {
        return monitoringCrudRepository.getMonitoringByServerId(serverId);
    }

    @SneakyThrows
    public void pingServer(Monitoring monitoring) {

        HttpResponse<String> httpResponse;
        String responseBody;
        String command = "";

        if (monitoring.getPingMethod().equals(PingMethod.GET)) {
            httpResponse = RequestHelper.GET_Request(monitoring.getServerUrl(), "Content-Type", "application/json");
            if (httpResponse != null) {
                responseBody = httpResponse.body();
                command = "Get request";
            } else {
                responseBody = "";
            }
        } else if (monitoring.getPingMethod().equals(PingMethod.POST)) {
            httpResponse = Objects.requireNonNull(RequestHelper.POST_Request(monitoring.getServerUrl(), "Content-Type", "application/json"));
            if (httpResponse != null) {
                responseBody = httpResponse.body();
                command = "Post request";
            } else {
                responseBody = "";
            }

        } else throw new RuntimeException("Wrong ping method");

        ServerInfo serverInfo = serverService.getServerInfoById(monitoring.getServerId());
        String title = serverInfo.getTitle();

        if (!responseBody.contains(monitoring.getExpectedResponse())) {
            chatBotService.broadcast("\uD83D\uDD34The monitoring " + title + " with the url "
                    + monitoring.getServerUrl() + " does not response with body " + monitoring.getExpectedResponse());

            statisticsService.saveMonitoringStatistics("Expected response",
                    monitoring.getRequest(), "Error",
                    monitoring.getServerId());

            runScenarioService.saveMonitoringRebooting(monitoring, true);
            runScenarioService.runScenario(serverInfo.getId(), TypeCommand.REBOOT, ServerStatus.REBOOTING);
            statisticsService.addActivitiesStatistics(serverInfo.getId(), "reboot");

            log.error("Server {} command {} url {} response body {}", title, command, monitoring.getServerUrl(), responseBody);
        } else {
            if (monitoring.getIsReboot()) {
                chatBotService.broadcast("\uD83D\uDFE2The server " + title + " is success rebooting");
                serverService.activeMonitoring(serverInfo.getId());
                runScenarioService.saveMonitoringRebooting(monitoring, false);
            }
        }

    }

    @SneakyThrows
    public void checkExpectedResponse(Monitoring monitoring) {
        ServerInfo serverInfo = serverService.getServerInfoById(monitoring.getServerId());
        String title = serverInfo.getTitle();
        Server server = new Server(serverInfo.getHost(), serverInfo.getPassword());
        String command = server.executeCommand(monitoring.getRequest());

        if (command.contains(monitoring.getExpectedResponse())) {
            if (monitoring.getIsReboot()) {
                chatBotService.broadcast("\uD83D\uDFE2The server " + title + " is success rebooting");
                serverService.activeMonitoring(serverInfo.getId());
                runScenarioService.saveMonitoringRebooting(monitoring, false);
            }
        } else {
            statisticsService.saveMonitoringStatistics("Expected response",
                    monitoring.getRequest(), "Error",
                    monitoring.getServerId());

            chatBotService.broadcast("\uD83D\uDD34" + title + "\n\uD83D\uDD34Invalid response\n\n" + command);
            runScenarioService.saveMonitoringRebooting(monitoring, true);
            runScenarioService.runScenario(serverInfo.getId(), TypeCommand.REBOOT, ServerStatus.REBOOTING);
            statisticsService.addActivitiesStatistics(serverInfo.getId(), "reboot");

            log.error("Server {} request {} after execute command {}", title, monitoring.getRequest(), command);
        }

    }

}