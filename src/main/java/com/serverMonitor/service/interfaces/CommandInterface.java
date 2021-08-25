package com.serverMonitor.service.interfaces;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.model.dto.CommandDTO;

import java.util.List;

public interface CommandInterface {

    void addCommand(CommandDTO commandDTO);

    List<Command> getAllCommandsByServer(Long serverId);

    Command changeCommandInfo(Long commandId, CommandDTO dto);

    void deleteCommand(Long commandId);
}
