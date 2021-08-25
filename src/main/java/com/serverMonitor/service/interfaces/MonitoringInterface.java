package com.serverMonitor.service.interfaces;

import com.serverMonitor.database.enteties.monitoring.Monitoring;
import com.serverMonitor.model.request.MonitoringInfoRunCommandRequest;
import com.serverMonitor.model.request.MonitoringInfoRunRequest;


public interface MonitoringInterface {

    void addMonitoringRunRequest(MonitoringInfoRunRequest monitoringInfoRunRequest) throws Exception;

    void addMonitoringRunCommand(MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) throws Exception;

    void deleteMonitoring(Long serverId) throws Exception;

    Monitoring updateMonitoringInfoByRunCommand(MonitoringInfoRunCommandRequest monitoringInfoRunCommandRequest) throws Exception;

    Monitoring updateMonitoringInfoByRunRequest(MonitoringInfoRunRequest monitoringInfoRunRequest) throws Exception;

    Monitoring getMonitoringInfo(Long monitoringId);
}
