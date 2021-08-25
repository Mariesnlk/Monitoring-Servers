package com.serverMonitor.database.repository.server;


import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ServerCrudRepository extends CrudRepository<ServerInfo, Long> {

    List<ServerInfo> findAll();

    Boolean existsByHostAndTitle(String host, String title);

    Boolean existsServerById(Long serverId);

    Optional<ServerInfo> findServerByTitle(String title);

    Optional<ServerInfo> findServerById(Long serverId);

    List<ServerInfo> findServersByServerType(ServerType serverType);

    @Query("SELECT e FROM ServerInfo e WHERE e.serverType = 'FRONTS'")
    List<ServerInfo> findServerInfosByServerType();

}
