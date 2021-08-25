package com.serverMonitor.model;

import com.jcraft.jsch.*;
import com.serverMonitor.database.enteties.server.ServerInfo;
import com.serverMonitor.security.encryption.AES;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Server {

    private String host;
    private String user;
    private String password;
    private boolean isReboot;

    private static final String defaultUser = "root";
    private static final String defaultPassword = "root";
    private static final Boolean defaultRebootStatus = false;

    private final static String SECRET_KEY = "fifi!fifi!!";

    public Server(String host, String password) {
        this.isReboot = defaultRebootStatus;
        this.host = host;
        this.user = defaultUser;
        this.password = password;
    }

    public Server(ServerInfo serverInfo) {
        this.isReboot = defaultRebootStatus;
        this.host = serverInfo.getHost();
        this.user = serverInfo.getUser();
        this.password = serverInfo.getPassword();
    }

    public Server(String host) {
        this.isReboot = defaultRebootStatus;
        this.host = host;
        this.user = defaultUser;
        this.password = defaultPassword;
    }

    public String executeCommand(String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        int exitCode = -1;
        Session session = jsch.getSession(this.user, this.host, 22);
        String decryptedString = AES.decrypt(this.password, SECRET_KEY);
        session.setPassword(decryptedString);
        session.setConfig("StrictHostKeyChecking", "no");

//shell request failed on channel 0

        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();
        byte[] tmp = new byte[1024];

        StringBuilder response = new StringBuilder();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                response.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0)
                    continue;
                exitCode = channel.getExitStatus();
//                log.info("[" + this.user + "@" + this.host + "] Command exit-status: " + exitCode);
                break;
            }
//            if (channel.isClosed()) {
//                System.out.println("exit-status: " + channel.getExitStatus());
//                break;
//            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                log.error("Server {} --- {}", this.host, ee.getMessage());
            }
        }
        channel.disconnect();
        session.disconnect();
        System.out.println(response);
        return response.toString();
    }


}
