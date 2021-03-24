package nctu.winlab.sshrest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import static nctu.winlab.sshrest.SSHConstants.mapper;

public class DefaultServerClient extends SshExecClient implements ServerClient {

    public DefaultServerClient(String ip, String port, String username, String password, String model) {
        super(ip, port, username, password);
        this.model = model;
    }

    @Override
    public ObjectNode execCommand(String cmd) {
        ObjectNode res = mapper.createObjectNode();
        try {
            sendCmd(cmd);
            res.put("raw", recvCmd());
        }
        catch (Exception e) {
            res.put("error", true);
            res.put("msg", e.getMessage());
        }
        return res;
    }

    @Override
    public ObjectNode execSudoCommand(String cmd) {
        ObjectNode res = mapper.createObjectNode();
        try {
            sendSudoCmd(cmd, password);
            String reply = recvCmd();
            System.out.printf("reply: %s\n", reply);
            res.put("raw", reply);
        }
        catch (Exception e) {
            res.put("error", true);
            res.put("msg", e.getMessage());
        }
        return res;
    }
}
