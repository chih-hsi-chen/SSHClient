package nctu.winlab.sshrest;

import com.jcraft.jsch.ChannelExec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static nctu.winlab.sshrest.SSHConstants.ANSI_BOLD;
import static nctu.winlab.sshrest.SSHConstants.ANSI_RED;
import static nctu.winlab.sshrest.SSHConstants.ANSI_RESET;

public class SshExecClient extends SshClient {
    public SshExecClient(String ip, String port, String username, String password) {
        super(ip, port, username, password);
    }

    public void connectToServer() throws Exception {
        try {
            if (session == null || !session.isConnected()) {
                session = jsch.getSession(username, ip, Integer.parseInt(port));
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect(3000);
            }
        }
        catch (Exception e) {
            System.err.printf(ANSI_RED + ANSI_BOLD + "\nFailed to connect to %s:%s\n" + ANSI_RESET, ip, port);
            throw e;
        }
    }

    public void sendCmd(String cmd) throws Exception {
        try {
            connectToServer();
            channel = session.openChannel("exec");
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            ((ChannelExec)channel).setCommand(cmd);
            channel.connect();
        }
        catch (Exception e) {
            System.err.printf("Failed to open exec channel to %s:%s", ip, port);
            throw e;
        }
    }
    
    public void sendSudoCmd(String cmd, String passwd) throws Exception {
        try {
            connectToServer();
            channel = session.openChannel("exec");
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            OutputStream out = channel.getOutputStream();
            ((ChannelExec)channel).setCommand("sudo -S -p '' " + cmd);
            channel.connect();
            out.write((passwd + "\n").getBytes());
            out.flush();
        }
        catch (Exception out) {
            // empty catch block
        }
    }

    public String recvCmd() {
        char[] buf = new char[1024];
        String reply = "";
        try {
            int nbytes;
            while ((nbytes = reader.read(buf, 0, 1024)) > -1) {
                reply = reply + String.valueOf(buf, 0, nbytes);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }
}
