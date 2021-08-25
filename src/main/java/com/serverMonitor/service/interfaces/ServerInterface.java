package com.serverMonitor.service.interfaces;

import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerType;
import com.serverMonitor.model.dto.ServerDTO;

import java.util.List;

public interface ServerInterface {

    List<ServerInfo> getAllServers();

    List<ServerInfo> getAllServersByType(ServerType type);

    ServerInfo getServerInfoById(Long serverId);

    void addServer(ServerDTO serverDTO);

    void deleteServer(Long serverId) throws Exception;

    ServerInfo changeServerInfo(Long serverId, ServerDTO serverDTO) throws Exception;
}
