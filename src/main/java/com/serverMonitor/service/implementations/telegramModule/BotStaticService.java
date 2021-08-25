package com.serverMonitor.service.implementations.telegramModule;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.enteties.server.ServerType;
import com.serverMonitor.model.dto.CommandDTO;
import com.serverMonitor.model.dto.RequestResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class BotStaticService {

    private static BotCommandServiceImpl botCommandServiceImpl;


    public static Command findCommandByServerIdAndTitle(Long serverId, String title){
        return botCommandServiceImpl.findCommandByServerIdAndTitle(serverId, title);
    }

    public static void runScenario(Long chartId, Long serverId, TypeCommand typeCommand, ServerStatus serverStatus){
        botCommandServiceImpl.runScenario(chartId, serverId, typeCommand, serverStatus);
    }

    public static String executeCommand(RequestResponseDTO requestResponseDTO){
        return botCommandServiceImpl.executeCommand(requestResponseDTO);
    }

    public static void addRebootCommand(CommandDTO commandDTO){
        botCommandServiceImpl.addRebootCommand(commandDTO);
    }

    public static ServerInfo findServerByTitle(String title){
        return botCommandServiceImpl.findServerByTitle(title);
    }

    public static List<ServerInfo> allServersByType(ServerType type){
        return botCommandServiceImpl.allServersByType(type);
    }

    public static List<Command> allCommandsByServerId(Long serverId){
        return botCommandServiceImpl.allCommandsByServerId(serverId);
    }

    public static void setBotCommandsServiceImpl(BotCommandServiceImpl botCommandServiceImpl) {
        BotStaticService.botCommandServiceImpl = botCommandServiceImpl;
    }

    public static String isAvailableInvitationCode(String code) throws Exception {
        return botCommandServiceImpl.isAvailableInvitationCode(code);
    }

    public static String addNewUser(String name) {
        return botCommandServiceImpl.addNewUser(name);
    }

    public static void pauseServerMonitoring (Long serverId) {
        botCommandServiceImpl.pauseServerMonitoring(serverId);
    }

    public static void continueServerMonitoring (Long serverId) {
        botCommandServiceImpl.continueServerMonitoring(serverId);
    }
}