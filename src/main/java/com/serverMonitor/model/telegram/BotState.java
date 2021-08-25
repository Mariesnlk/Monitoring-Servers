package com.serverMonitor.model.telegram;

import com.serverMonitor.database.enteties.command.Command;
import com.serverMonitor.database.enteties.command.TypeCommand;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.database.enteties.server.ServerStatus;
import com.serverMonitor.database.enteties.server.ServerType;
import com.serverMonitor.database.enteties.telegram.TelegramInfo;
import com.serverMonitor.model.dto.CommandDTO;
import com.serverMonitor.model.dto.RequestResponseDTO;
import com.serverMonitor.service.implementations.telegramModule.BotCommandServiceImpl;
import com.serverMonitor.service.implementations.telegramModule.BotStaticService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public enum BotState {

    Start {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Hello");
        }

        @Override
        public BotState nextState() {
            return EnterInvitationCode;
        }

    },

    EnterInvitationCode {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Please enter a secret key");
        }

        @Override
        public void handleInput(BotContext context) {
            String code = context.getInput();

            try {

                String name = BotStaticService.isAvailableInvitationCode(code);
                context.getUser().setName(name);
                context.getUser().setAdmin(name.equals("Admin"));

                next = Approved;
            } catch (Exception ex) {
                sendMessage(context, "Wrong code!");
                next = EnterInvitationCode;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    Approved(false) {
        @Override
        public void enter(BotContext context) {

            sendMessage(context, "Authorization successful !");
        }

        @Override
        public BotState nextState() {
            return Menu;
        }
    },

    Menu() {

        private BotState next;

        @Override
        public void enter(BotContext context) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();

            firstRow.add("Groups of servers");

            rows.add(firstRow);


            if (context.getUser().getAdmin()) {
                KeyboardRow secondRow = new KeyboardRow();
                secondRow.add("Add new user");
                rows.add(secondRow);
            }

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            if ("Add new user".equals(command)) {
                sendMessage(context, "Enter the name and surname of new user");
                next = AddNewUser;
            } else if("Groups of servers".equals(command)){
                next = ServersGroups;
            } else {
                sendMessage(context, "Wrong command");
                next = Menu;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },


    AddNewUser() {

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();

            firstRow.add("Back");

            rows.add(firstRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            if ("Back".equals(command)) {
                return;
            }

            try {
                sendMessage(context, BotStaticService.addNewUser(command));
            } catch (Exception ex) {
                sendMessage(context, "Wrong command!");
            }
        }

        @Override
        public BotState nextState() {
            return Menu;
        }
    },

    ServersGroups() {

        private BotState next;

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();

            KeyboardRow firstRow = new KeyboardRow();
            KeyboardRow secondRow = new KeyboardRow();

            firstRow.add(String.valueOf(ServerType.NODES));
            firstRow.add(String.valueOf(ServerType.BOTS));
            firstRow.add(String.valueOf(ServerType.EXCHANGES));

            secondRow.add(String.valueOf(ServerType.WALLETS));
            secondRow.add(String.valueOf(ServerType.EXPLORERS));

            rows.add(firstRow);
            rows.add(secondRow);

            KeyboardRow lastRow = new KeyboardRow();
            lastRow.add("Back");
            rows.add(lastRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            if ("Back".equals(command)) {
                next = Menu;
            } else {
                try {
                    TelegramInfo user = context.getUser();
                    user.setServerType(ServerType.valueOf(command));
                    next = AllServers;
                } catch (Exception ex) {
                    sendMessage(context, "Wrong command");
                    next = Menu;
                }
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }

    },

    AllServers() {

        private BotState next;

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();

            TelegramInfo user = context.getUser();

            List<ServerInfo> list = BotStaticService.allServersByType(user.getServerType());


            if(list.isEmpty())  sendMessage(context, "Empty list");


            list.forEach(el -> {
                KeyboardRow firstRow = new KeyboardRow();
                firstRow.add(el.getTitle());
                rows.add(firstRow);
            });


            KeyboardRow lastRow = new KeyboardRow();
            lastRow.add("Back");
            rows.add(lastRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            if ("Back".equals(command)) {
                next = ServersGroups;
            } else {
                try {
                    String title = BotStaticService.findServerByTitle(command).getTitle();
                    TelegramInfo user = context.getUser();
                    user.setServerTitle(title);
                    next = Actions;
                } catch (Exception ex) {
                    sendMessage(context, "Wrong command");
                    next = Menu;
                }
            }

        }

        @Override
        public BotState nextState() {
            return next;
        }

    },

    Actions() {

        private BotState next;

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();
            KeyboardRow secondRow = new KeyboardRow();
            KeyboardRow thirdRow = new KeyboardRow();
            KeyboardRow fourthRow = new KeyboardRow();
            KeyboardRow fifthRow = new KeyboardRow();


            firstRow.add("Server Info");
            firstRow.add("Check is monitoring");

            secondRow.add("Pause monitoring");
            secondRow.add("Continue monitoring");

            thirdRow.add("Run reboot command");
            thirdRow.add("Run rebuild command");

            fourthRow.add("All commands");
            fourthRow.add("Execute command");

            rows.add(firstRow);
            rows.add(secondRow);
            rows.add(thirdRow);
            rows.add(fourthRow);

            KeyboardRow lastRow = new KeyboardRow();
            lastRow.add("Back");
            rows.add(lastRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            TelegramInfo user = context.getUser();
            Long chartId = user.getChartId();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            if("Execute command".equals(command)){
                sendMessage(context, "Enter command");
                user.setRequest(context.getInput());
                next = EXECUTE;
            } else if ("All commands".equals(command)) {
                next = COMMANDS;
            } else if ("Server Info".equals(command)){
                sendMessage(context, "Server Info: \n" +
                        "\ntitle = " + BotStaticService.findServerByTitle(title).getTitle() +
                        "\nhost = " + BotStaticService.findServerByTitle(title).getHost() +
                        "\nuser = " + BotStaticService.findServerByTitle(title).getUser() +
                        "\nserver type = " + BotStaticService.findServerByTitle(title).getServerType() +
                        "\nmonitoring = " + BotStaticService.findServerByTitle(title).getIsMonitoring() +
                        "\nstatus = " + BotStaticService.findServerByTitle(title).getStatus());
                next = Actions;
            } else if ("Check is monitoring".equals(command)){
                sendMessage(context, "Monitoring = " + BotStaticService.findServerByTitle(title).getIsMonitoring().toString());
                next = Actions;
            } else if("Pause monitoring".equals(command)){
                BotStaticService.pauseServerMonitoring(id);
                sendMessage(context, "Server status = " + BotStaticService.findServerByTitle(title).getStatus());
                next = Actions;
            } else if("Continue monitoring".equals(command)){
                BotStaticService.continueServerMonitoring(id);
                sendMessage(context, "Server status = " + BotStaticService.findServerByTitle(title).getStatus());
                next = Actions;
            }
//            else if("Add reboot command".equals(command)){
//                sendMessage(context, "Enter command");
//                user.setRequest(context.getInput());
//                next = ADD_REBOOT;
//            } else if("Add rebuild command".equals(command)){
//                sendMessage(context, "Enter command");
//                user.setRequest(context.getInput());
//                next = ADD_REBUILD;
//            }
            else if("Run reboot command".equals(command)){
                BotStaticService.runScenario(chartId, id, TypeCommand.REBOOT, ServerStatus.REBOOTING);
            } else if("Run rebuild command".equals(command)){
                BotStaticService.runScenario(chartId, id, TypeCommand.REBUILD, ServerStatus.REBUILDING);
            } else if ("Back".equals(command)) {
                next = AllServers;
            } else {
                sendMessage(context, "Wrong command");
                next = Menu;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }

    },

    EXECUTE() {

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();

            firstRow.add("Back");

            rows.add(firstRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            TelegramInfo user = context.getUser();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            if ("Back".equals(command)) {
                return;
            }

            try {
                sendMessage(context, BotStaticService.executeCommand(
                        RequestResponseDTO.builder()
                                .serverId(id)
                                .request(command)
                                .build()));
            } catch (Exception ex) {
                sendMessage(context, "Wrong command!");
            }
        }

        @Override
        public BotState nextState() {
            return Actions;
        }
    },

    COMMANDS() {

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();

            TelegramInfo user = context.getUser();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            List<Command> list = BotStaticService.allCommandsByServerId(id);

            if(list.isEmpty())  sendMessage(context, "No commands");

            //.filter(obj -> obj.getTypeCommand() == TypeCommand.COMMON)

            list.forEach(el -> {
                KeyboardRow firstRow = new KeyboardRow();
                firstRow.add(el.getTitle());
                rows.add(firstRow);
            });

            KeyboardRow lastRow = new KeyboardRow();
            lastRow.add("Back");
            rows.add(lastRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            TelegramInfo user = context.getUser();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            if ("Back".equals(command)) {
                return;
            }
            Command commandInfo = BotStaticService.findCommandByServerIdAndTitle(id, command);
            try {
                sendMessage(context, BotStaticService.executeCommand(
                        RequestResponseDTO.builder()
                                .serverId(id)
                                .request(commandInfo.getCommand())
                                .build()));
            } catch (Exception ex) {
                sendMessage(context, "Wrong command!");
            }
        }

        @Override
        public BotState nextState() {
            return Actions;
        }
    },

    ADD_REBUILD() {

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();

            firstRow.add("Back");

            rows.add(firstRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            TelegramInfo user = context.getUser();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            if ("Back".equals(command)) {
                return;
            }

            try {
                BotStaticService.addRebootCommand(CommandDTO.builder()
                        .serverId(id)
                        .title("rebuild")
                        .command(command)
                        .typeCommand(TypeCommand.REBUILD)
                        .build());
                sendMessage(context, "Command was added!");
            } catch (Exception ex) {
                sendMessage(context, "This command already exists!");
            }
        }

        @Override
        public BotState nextState() {
            return Actions;
        }
    },

    ADD_REBOOT() {

        @Override
        public void enter(BotContext context) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            ArrayList<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow firstRow = new KeyboardRow();
            firstRow.add("Back");
            rows.add(firstRow);

            replyKeyboardMarkup.setKeyboard(rows);
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChartId())
                    .setText("⚪️⚫️\uD83D\uDD34\uD83D\uDD35")
                    .setReplyMarkup(replyKeyboardMarkup);
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {

            String command = context.getInput();
            BotCommandServiceImpl.logCommand(command, context.getUser().getName());

            TelegramInfo user = context.getUser();
            String title = user.getServerTitle();
            Long id = BotStaticService.findServerByTitle(title).getId();

            if ("Back".equals(command)) {
                return;
            }

            try {
                BotStaticService.addRebootCommand(CommandDTO.builder()
                        .serverId(id)
                        .title("reboot")
                        .command(command)
                        .typeCommand(TypeCommand.REBOOT)
                        .build());
                sendMessage(context, "Command was added!");
            } catch (Exception ex) {
                sendMessage(context, "This command already exists!");
            }
        }

        @Override
        public BotState nextState() {
            return Actions;
        }
    };

    private static BotState[] states;
    private final boolean inputNeeded;

    BotState() {
        this.inputNeeded = true;
    }

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static BotState getInitialState() {
        return byId(0);
    }

    public static BotState byId(int id) {
        if (states == null) {
            states = BotState.values();
        }

        return states[id];
    }

    protected void sendMessage(BotContext context, String text) {
        SendMessage message = new SendMessage()
                .setChatId(context.getUser().getChartId())
                .setText(text);
        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public void handleInput(BotContext context) throws Exception {
        // do nothing by default
    }

    public abstract void enter(BotContext context);

    public abstract BotState nextState();
}
