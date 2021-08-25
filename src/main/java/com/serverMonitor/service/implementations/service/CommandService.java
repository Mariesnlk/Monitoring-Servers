package com.serverMonitor.service.implementations.service;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.repository.command.CommandCrudRepository;
import com.serverMonitor.database.repository.server.ServerCrudRepository;
import com.serverMonitor.model.dto.CommandDTO;
import com.serverMonitor.exceptions.BadRequestException;
import com.serverMonitor.service.interfaces.CommandInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandService implements CommandInterface {


    private final CommandCrudRepository commandCrudRepository;
    private final ServerCrudRepository serverCrudRepository;
    private final StatisticsService statisticsService;

    @Override
    public void addCommand(CommandDTO commandDTO) {

        Long serverId = commandDTO.getServerId();
        String title = commandDTO.getTitle();
        String command = commandDTO.getCommand();
        TypeCommand typeCommand = commandDTO.getTypeCommand();
        Long interval = commandDTO.getInterval();

        if (!serverCrudRepository.existsServerById(serverId))
            throw new BadRequestException("The server does not exists");
        if (commandCrudRepository.existsCommandByServerIdAndTitle(serverId, title))
            throw new BadRequestException("The command with name " + title + " for server " + serverId + " is already exist!");

        commandCrudRepository.save(
                Command.builder()
                        .serverId(serverId)
                        .title(title)
                        .command(command)
                        .typeCommand(typeCommand)
                        .interval(interval)
                        .build()
        );

        statisticsService.addActivitiesStatistics(serverId, "add command " + title);
    }


    @Override
    public List<Command> getAllCommandsByServer(Long serverId) {
        return commandCrudRepository.getCommandsByServerId(serverId);
    }

    @Override
    public Command changeCommandInfo(Long commandId, CommandDTO dto) {

        Command command = commandCrudRepository.getCommandById(commandId);

        command.setServerId(dto.getServerId());
        command.setTitle(dto.getTitle());
        command.setCommand(dto.getCommand());
        command.setTypeCommand(dto.getTypeCommand());
        command.setInterval(dto.getInterval());
        commandCrudRepository.save(command);

        return command;
    }

    @Override
    public void deleteCommand(Long commandId) {
        Command command = commandCrudRepository.getCommandById(commandId);
        commandCrudRepository.delete(command);
    }

}