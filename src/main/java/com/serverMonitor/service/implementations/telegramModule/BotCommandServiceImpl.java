package com.serverMonitor.service.implementations.telegramModule;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.enteties.server.ServerType;
import com.serverMonitor.database.enteties.telegram.CodeStatus;
import com.serverMonitor.database.enteties.telegram.InvitationCode;
import com.serverMonitor.database.repository.command.CommandCrudRepository;
import com.serverMonitor.database.repository.telegram.InvitationCodeRepository;
import com.serverMonitor.database.repository.monitoring.MonitoringCrudRepository;
import com.serverMonitor.database.repository.server.ServerCrudRepository;
import com.serverMonitor.model.dto.CommandDTO;
import com.serverMonitor.model.dto.RequestResponseDTO;
import com.serverMonitor.service.implementations.service.CommandService;
import com.serverMonitor.service.implementations.service.RunScenarioService;
import com.serverMonitor.service.implementations.service.ServerService;
import com.serverMonitor.service.interfaces.BotCommandService;
import com.serverMonitor.utils.email.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotCommandServiceImpl implements BotCommandService {

    private final InvitationCodeRepository invitationCodeRepository;
    private final ServerCrudRepository serverCrudRepository;
    private final CommandCrudRepository commandCrudRepository;
    private final MonitoringCrudRepository monitoringCrudRepository;
    private final ServerService serverService;
    private final CommandService commandService;
    private final ChatBotService chatBotService;
    private final RunScenarioService runScenarioService;


    @Value("${admin.invite.code}")
    private String ADMIN_INVITE_CODE;

    @Value("${default.invite.code}")
    private String DEFAULT_INVITE_CODE;

    public Command findCommandByServerIdAndTitle(Long serverId, String title){
        return commandCrudRepository.getCommandByServerIdAndTitle(serverId, title);
    }

    public void runScenario(Long chartId, Long serverId, TypeCommand typeCommand, ServerStatus serverStatus){
        if(!commandCrudRepository.existsCommandByServerIdAndTypeCommand(serverId, typeCommand)){
            chatBotService.sendMessage(chartId, "First add command");
        } else {
            if (serverStatus == ServerStatus.REBOOTING && monitoringCrudRepository.existsByServerId(serverId)) {
                Monitoring monitoring = monitoringCrudRepository.getMonitoringByServerId(serverId);
                monitoring.setIsReboot(true);
                monitoringCrudRepository.save(monitoring);
            }
            runScenarioService.runScenario(serverId, typeCommand, serverStatus);
        }
    }

    public String executeCommand(RequestResponseDTO requestResponseDTO){
        return runScenarioService.executeCommand(requestResponseDTO);
    }

    public ServerInfo findServerByTitle(String title){
        return  serverCrudRepository.findServerByTitle(title).orElseThrow(
                () -> new RuntimeException("Can`t find server - " + title)
        );
    }

    public List<ServerInfo> allServersByType(ServerType type) {
        return serverCrudRepository.findServersByServerType(type);
    }

    public List<Command> allCommandsByServerId(Long serverId) {
        return commandCrudRepository.getCommandsByServerId(serverId);
    }

    public void pauseServerMonitoring(Long serverId) {
        serverService.pauseServerMonitoring(serverId);
    }

    public void continueServerMonitoring(Long serverId) {
        serverService.continueMonitoring(serverId);
    }

    public  void addRebootCommand(CommandDTO commandDTO){
        commandService.addCommand(commandDTO);
    }

    public static void logCommand(String command, String userName) {
        System.out.println("------------------" + command + " : " + userName + "------------------------");
    }

    public String isAvailableInvitationCode(String code) throws Exception {

        if (code.equals(DEFAULT_INVITE_CODE)) {
            return "DefaultUser" + Math.rint(1000000);
        } else if (code.equals(ADMIN_INVITE_CODE)) {
            return "Admin";
        } else throw new Exception("Invalid invitation code");
    }

    public String addNewUser(String name) {
        InvitationCode invitationCode = new InvitationCode();

        invitationCode.setName(name);
        invitationCode.setCode(Util.getRandomHash());
        invitationCode.setStatus(CodeStatus.ACTIVE);

        invitationCodeRepository.save(invitationCode);

        return "User - " + invitationCode.getName() + " success registered, invitation code is - " + invitationCode.getCode();
    }

    @PostConstruct
    private void createDefaultAdminCode() {
        if (invitationCodeRepository.findByName("Admin").isEmpty()) {
            InvitationCode invitationCode = new InvitationCode();

            invitationCode.setName("Admin");
            invitationCode.setCode(ADMIN_INVITE_CODE);
            invitationCode.setStatus(CodeStatus.ACTIVE);

            invitationCodeRepository.save(invitationCode);
        }
    }
}