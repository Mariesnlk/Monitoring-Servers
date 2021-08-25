package com.serverMonitor.database.repository.command;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.command.TypeCommand;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandCrudRepository extends CrudRepository<Command, Long> {

    List<Command> getCommandsByServerId(Long serverId);

    List<Command> getCommandsByServerIdAndTypeCommandOrderById(Long serverId, TypeCommand typeCommand);

    Command getCommandById(Long commandId);

    Boolean existsCommandByServerIdAndTitle(Long serverId, String title);

    Boolean existsCommandByServerIdAndTypeCommand(Long serverId, TypeCommand typeCommand);

    Command getCommandByServerIdAndTitle(Long serverId, String title);

}
