package com.serverMonitor.service.implementations.telegramModule;


import com.serverMonitor.database.enteties.telegram.TelegramInfo;
import com.serverMonitor.model.telegram.BotContext;
import com.serverMonitor.model.telegram.BotState;
import com.serverMonitor.database.repository.telegram.TelegramRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBotService extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(ChatBotService.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final TelegramRepository telegramRepository;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        final String text = update.getMessage().getText();
        final long chatId = update.getMessage().getChatId();

        TelegramInfo user = telegramRepository.findByChartId(chatId);

        BotContext context;
        BotState state;

        if (user == null) {
            state = BotState.getInitialState();

            user = new TelegramInfo(chatId, state.ordinal());

            telegramRepository.save(user);

            context = BotContext.of(this, user, text);
            state.enter(context);

            LOGGER.info("New user registered: " + chatId);

        } else {

            context = BotContext.of(this, user, text);
            state = BotState.byId(user.getStateId());

        }

        state.handleInput(context);

        do {
            state = state.nextState();
            state.enter(context);
        } while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        telegramRepository.save(user);
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message);
        } catch (TelegramApiException ignore) {
        }
    }

    public void broadcast(String text) {
        List<TelegramInfo> users = telegramRepository.findAll();
        users.forEach(user -> sendMessage(user.getChartId(), text));
    }
}