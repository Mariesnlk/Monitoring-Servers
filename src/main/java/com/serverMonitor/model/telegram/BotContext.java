package com.serverMonitor.model.telegram;


import com.serverMonitor.database.enteties.telegram.TelegramInfo;
import com.serverMonitor.service.implementations.telegramModule.ChatBotService;

public class BotContext {

    private final ChatBotService bot;
    private final TelegramInfo user;
    private final String input;

    public static BotContext of(ChatBotService bot, TelegramInfo user, String text) {
        return new BotContext(bot, user, text);
    }

    private BotContext(ChatBotService bot, TelegramInfo user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }

    public ChatBotService getBot() {
        return bot;
    }

    public TelegramInfo getUser() {
        return user;
    }

    public String getInput() {
        return input;
    }
}

