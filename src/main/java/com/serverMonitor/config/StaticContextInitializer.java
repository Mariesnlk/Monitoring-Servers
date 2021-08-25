package com.serverMonitor.config;

import com.serverMonitor.service.implementations.telegramModule.BotCommandServiceImpl;
import com.serverMonitor.service.implementations.telegramModule.BotStaticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class StaticContextInitializer {

    private final BotCommandServiceImpl botCommandsServiceImpl;

    @PostConstruct
    public void init() {
        BotStaticService.setBotCommandsServiceImpl(botCommandsServiceImpl);
    }
}
