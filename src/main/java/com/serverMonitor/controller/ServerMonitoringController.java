package com.serverMonitor.controller;

import com.serverMonitor.config.rest.model.MVCResponse;
import com.serverMonitor.config.rest.model.MVCResponseObject;
import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.model.dto.*;
import com.serverMonitor.model.request.MonitoringInfoRunCommandRequest;
import com.serverMonitor.model.request.MonitoringInfoRunRequest;
import com.serverMonitor.security.encryption.AES;
import com.serverMonitor.service.implementations.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Api(value = "server monitoring")
@Slf4j
public class ServerMonitoringController {

    private final ServerService serverService;
    private final MonitoringService monitoringService;
    private final StatisticsService statisticsService;
    private final CommandService commandService;
    private final RunScenarioService runScenarioService;


    @ApiOperation(value = "Add a server")
    @RequestMapping(value = "/add_server", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse addServer(@RequestBody ServerDTO serverDTO) {
        serverService.addServer(serverDTO);
        return new MVCResponseObject(200, "OK");
    }

    @ApiOperation(value = "Add a monitoring to ping server by run command")
    @RequestMapping(value = "/add_monitoring_to_ping_by_run_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse addMonitoringToPingByRunCommand(@RequestBody MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) throws Exception {
        monitoringService.addMonitoringRunCommand(monitoringInfoRunCommandRequest);
        return new MVCResponse(200);
    }

    @ApiOperation(value = "Add a monitoring to ping server by run request")
    @RequestMapping(value = "/add_monitoring_to_ping_by_run_request", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse addMonitoringToPingByRunRequest(@RequestBody MonitoringInfoRunRequest monitoringInfoRunRequest) throws Exception {
        monitoringService.addMonitoringRunRequest(monitoringInfoRunRequest);
        return new MVCResponse(200);
    }

    @ApiOperation(value = "Add a command")
    @RequestMapping(value = "/add_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse addCommand(@RequestBody CommandDTO commandDTO) {
        commandService.addCommand(commandDTO);
        return new MVCResponse(200);
    }

    @ApiOperation(value = "Get all servers")
    @RequestMapping(value = "/get_all_servers", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse getAllServers() {
        return new MVCResponseObject(200, serverService.getAllServers());
    }


    @ApiOperation(value = "Update server info")
    @RequestMapping(value = "/change_server_info", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse changeServerInfo(@RequestParam("id") Long serverId, @RequestBody ServerDTO serverDTO) {
        return new MVCResponseObject(200, serverService.changeServerInfo(serverId, serverDTO));
    }

    @ApiOperation(value = "Update monitoring info by run command")
    @RequestMapping(value = "/change_monitoring_to_ping_by_run_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse changeMonitoringToPingByRunCommand(@RequestBody MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) {
        return new MVCResponseObject(200, monitoringService.updateMonitoringInfoByRunCommand(monitoringInfoRunCommandRequest));
    }

    @ApiOperation(value = "Update monitoring info by run request")
    @RequestMapping(value = "/change_monitoring_to_ping_by_run_request", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse changeMonitoringToPingByRunRequest(@RequestBody MonitoringInfoRunRequest monitoringInfoRunRequest) {
        return new MVCResponseObject(200, monitoringService.updateMonitoringInfoByRunRequest(monitoringInfoRunRequest));
    }

    @ApiOperation(value = "Update command info")
    @RequestMapping(value = "/change_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse changeCommandInfo(@RequestParam("id") Long commandId, @RequestBody CommandDTO dto) {
        return new MVCResponseObject(200, commandService.changeCommandInfo(commandId, dto));
    }

    @ApiOperation(value = "Delete server")
    @RequestMapping(value = "/delete_server", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse deleteServer(@RequestParam("id") Long serverId) {
        serverService.deleteServer(serverId);
        return new MVCResponse(200);
    }

    @ApiOperation(value = "Delete monitoring")
    @RequestMapping(value = "/delete_monitoring", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse deleteMonitoring(@RequestParam("id") Long serverId) {
        monitoringService.deleteMonitoring(serverId);
        return new MVCResponse(200);
    }

    @ApiOperation(value = "Delete command")
    @RequestMapping(value = "/delete_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse deleteCommand(@RequestParam("id") Long commandId) {
        commandService.deleteCommand(commandId);
        return new MVCResponse(200);
    }

    @RequestMapping(value = "/get_server_info", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse getServerInfo(@RequestParam("id") Long serverId) {
        ServerInfo serverInfo = serverService.getServerInfoById(serverId);
        serverInfo.setPassword(AES.decrypt(serverInfo.getPassword(), "fifi!fifi!!"));
        return new MVCResponseObject(200, serverInfo);
    }


    @RequestMapping(value = "/get_server_monitoring_info", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse getMonitoringServerInfo(@RequestParam("id") Long serverId) {
        return new MVCResponseObject(200, monitoringService.getMonitoringInfo(serverId));
    }

    @ApiOperation(value = "Get monitoring statistics each of server by it`s id")
    @RequestMapping(value = "/get_monitoring_statistics_by_server", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse getMonitoringStatisticsByServerId(@RequestParam("id") Long serverId) {
        return new MVCResponseObject(200, statisticsService.getAllMonitoringStatisticsByServerId(serverId));
    }

    @ApiOperation(value = "get all commands for server by it`s id")
    @RequestMapping(value = "/get_all_commands_by_server", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse getAllCommandsByServer(@RequestParam("id") Long serverId) {
        return new MVCResponseObject(200, commandService.getAllCommandsByServer(serverId));
    }

    @RequestMapping(value = "/pause_monitoring", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse pauseMonitoring(@RequestParam("id") Long serverId) {
        serverService.pauseServerMonitoring(serverId);
        return new MVCResponse(200);
    }


    @RequestMapping(value = "/continue_monitoring", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse continueMonitoring(@RequestParam("id") Long serverId) {
        serverService.continueMonitoring(serverId);
        return new MVCResponse(200);
    }


    @ApiOperation(value = "execute command on server")
    @RequestMapping(value = "/execute_command", method = RequestMethod.POST, produces = "application/json")
    public MVCResponse executeCommand(@RequestBody RequestResponseDTO requestResponseDTO) {
        return new MVCResponseObject(200, runScenarioService.executeCommand(requestResponseDTO));
    }

//    @ApiOperation(value = "all activities statistics")
//    @RequestMapping(value = "/all_activities_statistics", method = RequestMethod.POST, produces = "application/json")
//    public MVCResponse allActivitiesStatistics(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
//                                               @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
//        return new MVCResponseObject(200, statisticsService.getAllActivitiesStatistics(PageRequest.of(page, size)));
//    }

}