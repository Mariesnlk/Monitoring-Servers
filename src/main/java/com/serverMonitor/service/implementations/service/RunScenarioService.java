package com.serverMonitor.service.implementations.service;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.repository.command.CommandCrudRepository;
import com.serverMonitor.database.repository.monitoring.MonitoringCrudRepository;
import com.serverMonitor.exceptions.BadRequestException;
import com.serverMonitor.model.Server;
import com.serverMonitor.model.dto.RequestResponseDTO;
import com.serverMonitor.service.implementations.telegramModule.ChatBotService;
import com.serverMonitor.utils.converting.Conversion;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class RunScenarioService {

    private final ServerService serverService;
    private final MonitoringCrudRepository monitoringCrudRepository;
    private final CommandCrudRepository commandCrudRepository;
    private final ChatBotService chatBotService;


    public void runScenario(Long serverId, TypeCommand typeCommand, ServerStatus serverStatus) {
        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        serverService.changeStatus(serverInfo, serverStatus);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                runCommand(serverInfo.getId(), typeCommand);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!monitoringCrudRepository.existsByServerId(serverId) && (serverStatus == ServerStatus.REBOOTING || serverStatus == ServerStatus.REBUILDING)) {
                serverService.disableMonitoring(serverId);
                chatBotService.broadcast("\uD83D\uDFE2The server " + serverInfo.getTitle() + " is " + serverStatus);
            }
        });
        if (serverInfo.getStatus() == ServerStatus.REBOOTING && monitoringCrudRepository.existsByServerId(serverId)) {
            Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
            monitoring.setPingTime(new Date().getTime() + Conversion.convertMinutesToMilliseconds(monitoring.getTime()));
            monitoringCrudRepository.save(monitoring);
        }
    }

    public void runCommand(Long serverId, TypeCommand typeCommand) {
        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        String serverTitle = serverInfo.getTitle();
        List<Command> listOfCommands = commandCrudRepository.getCommandsByServerIdAndTypeCommandOrderById(serverId, typeCommand);

        if (listOfCommands.isEmpty()) {
            statusForFailedRunCommand(serverId);
            throw new BadRequestException("Server " + serverTitle + " No command for " + typeCommand + "! First add command.");
        }

        listOfCommands.forEach(command ->
                Executors.newSingleThreadExecutor().submit(() -> {
                    RequestResponseDTO requestResponseDTO = RequestResponseDTO.builder()
                            .serverId(command.getServerId())
                            .request(command.getCommand())
                            .build();
                    executeCommand(requestResponseDTO);
//                    System.out.println(command.getCommand() + " " + System.currentTimeMillis());

                    try {
                        Thread.sleep(Conversion.convertMinutesToMilliseconds(command.getInterval()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private void statusForFailedRunCommand(Long serverId) {
        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        if (serverInfo.getStatus() == ServerStatus.REBOOTING) {
            Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
            saveMonitoringRebooting(monitoring, false);
        }
        changeStatus(serverInfo);
    }

    private void changeStatus(ServerInfo serverInfo) {
        if (monitoringCrudRepository.existsByServerId(serverInfo.getId())) {
            serverService.activeMonitoring(serverInfo.getId());
        } else {
            serverService.disableMonitoring(serverInfo.getId());
        }
    }

    public void runRebuildOnceADay(Long serverId, TypeCommand typeCommand, ServerStatus serverStatus) {
        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        serverService.changeStatus(serverInfo, serverStatus);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                runCommand(serverId, typeCommand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void saveMonitoringRebooting(Monitoring monitoring, boolean isReboot) {
        monitoring.setIsReboot(isReboot);
        monitoringCrudRepository.save(monitoring);
    }

    @SneakyThrows
    public String executeCommand(RequestResponseDTO requestResponseDTO) {
        String request = requestResponseDTO.getRequest();
        if (request == null || request.isEmpty())
            throw new BadRequestException("Request is null or empty");
        ServerInfo serverInfo = serverService.getServerInfoById(requestResponseDTO.getServerId());
        Server server = new Server(serverInfo.getHost(), serverInfo.getPassword());
        return server.executeCommand(requestResponseDTO.getRequest());
    }
}