package com.serverMonitor.service.interfaces;

public interface BotCommandService {

    String isAvailableInvitationCode(String code) throws Exception;

    String addNewUser(String name);

}
