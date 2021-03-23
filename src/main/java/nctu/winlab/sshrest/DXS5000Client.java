package nctu.winlab.sshrest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DXS5000Client extends SshShellClient implements SwitchClient {
    private static Logger log = Logger.getLogger(DXS5000Client.class.getName());
    private static ObjectMapper mapper = new ObjectMapper();

    public DXS5000Client(String ip, String port, String username, String password, String model) {
        super(ip, port, username, password);
        this.model = model;
    }

    @Override
    public ObjectNode getController() {
        ObjectNode res = mapper.createObjectNode();
        ArrayNode controllerList = res.putArray("controllers");
        try {
            String[] reply = commander.addCmd("enable").addMainCmd("show openflow configured controller", new String[0]).addCmd("exit").sendCmd().recvCmd().split("[\r\n]+");
            log.info(formatString("\u001b[32m\u001b[1m\n%s -- %s\n\u001b[0m", ip, model));
            String[] controllers = Arrays.copyOfRange(reply, 3, reply.length);
            log.info(formatString("%-17s%-7s%-6s%s\n", "IP", "Port", "Mode", "Role"));
            for (String controller : controllers) {
                String[] infos = (String[])Stream.of(controller.split("[ \t]+")).filter(i -> !i.isEmpty()).toArray(x$0 -> new String[x$0]);
                ObjectNode c = mapper.createObjectNode();
                log.info(formatString("%-17s%-7s%-6s%s\n", infos[0], infos[1], infos[2], infos[3]));
                c.put("ip", infos[0]);
                c.put("port", infos[1]);
                c.put("mode", infos[2]);
                c.put("role", infos[3]);
                controllerList.add((JsonNode)c);
            }
        }
        catch (Exception reply) {
            // empty catch block
        }
        return res;
    }

    @Override
    public void setController(String ip, String port) {
        try {
            String reply = commander.addCmd("enable", "configure").addMainCmd("openflow controller " + ip + " " + port).addCmd("exit", "exit").sendCmd().recvCmd();
            log.info(reply);
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public void unsetController(String ip) {
        try {
            String reply = commander.addCmd("enable", "configure").addMainCmd("no openflow controller " + ip).addCmd("exit", "exit").sendCmd().recvCmd();
            log.info(reply);
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public ObjectNode getFlows() {
        ObjectNode res = mapper.createObjectNode();
        ArrayNode flowList = res.putArray("flows");
        try {
            String reply = commander.addCmd("enable").addMainCmd("show openflow installed flows", " ", " ", " ", " ").addCmd("exit").sendCmd().recvCmd();
            log.info(reply);
            ObjectNode flowNode = mapper.createObjectNode();
            for (String flow : reply.split("(?=\r\nFlow type)")) {
                String[] items = flow.split("(Match criteria:|Actions:|Status:)");
                flowNode.put("type", processFlowType(items[0]));
                flowNode.set("matches", (JsonNode)processKeyValue(items[1]));
                flowNode.set("actions", (JsonNode)processKeyValue(items[2]));
                flowNode.set("status", (JsonNode)processKeyValue(items[3]));
            }
            flowList.add((JsonNode)flowNode);
        }
        catch (Exception reply) {
            // empty catch block
        }
        return res;
    }

    @Override
    public ObjectNode getGroups() {
        ObjectNode res = mapper.createObjectNode();
        try {
            String reply = commander.addCmd("enable").addMainCmd("show openflow installed groups", " ", " ", " ", " ").addCmd("exit").sendCmd().recvCmd();
            log.info(reply);
            res.set("groups", (JsonNode)processGroups(reply));
        }
        catch (Exception reply) {
            // empty catch block
        }
        return res;
    }

    @Override
    public void getLogs(FileWriter writer) {
        try {
            String reply = commander.addCmd("enable").addMainCmd("show logging buffered", "q").addCmd("exit").sendCmd().recvCmd();
            String title = formatString("\u001b[32m\u001b[1m\n%s -- %s\n\u001b[0m", ip, model);
            if (writer == null) {
                log.info(title);
                log.info(reply);
            } else {
                writer.write(title, 0, title.length());
                writer.write(reply, 0, reply.length());
            }
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanSourceInterfaceLoopback(String loopbackId) {
        try {
            String reply = commander.addCmd("enable", "configure").addCmd("vxlan enable").addMainCmd("vxlan source-interface loopback " + loopbackId, new String[0]).addCmd("exit", "exit").sendCmd().recvCmd();
            log.info(reply);
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanVlan(String vnid, String vid) {
        try {
            String reply = commander.addCmd("enable", "configure").addCmd("vxlan enable").addMainCmd("vxlan " + vnid + " vlan " + vid, new String[0]).addCmd("exit", "exit").sendCmd().recvCmd();
            log.info(reply);
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanVtep(String vnid, String ip, String mac) {
        try {
            String reply = commander.addCmd("enable", "configure").addCmd("vxlan enable").addMainCmd("vxlan " + vnid + " vtep " + ip + (mac.isEmpty() ? "" : new StringBuilder().append(" tenant-system ").append(mac).toString()), new String[0]).addCmd("exit", "exit").sendCmd().recvCmd();
            log.info(reply);
        }
        catch (Exception e) {
            return;
        }
    }

    private String processFlowType(String flowType) {
        return flowType.replace("Flow type ", "").replace("DOT", ".").replaceAll("\r\n|\"", "");
    }

    private ObjectNode processKeyValue(String raw) {
        String[] lineFields = raw.split("\r\n");
        ObjectNode dictNode = mapper.createObjectNode();
        for (String line : lineFields) {
            String[] patterns = line.split(" : ");
            if ((line = line.strip()).length() == 0)
                continue;
            for (String pat : patterns) {
                String value = pat.replaceFirst("([a-zA-Z\\s]+)(?<!\\w{2}:\\w{2}:\\w{2})", "");
                String key = pat.replace(value, "").strip();
                value = value.replace(":", "").strip();
                dictNode.put(key, value);
            }
        }
        return dictNode;
    }

    private ArrayNode processGroups(String raw) {
        ObjectNode group = null;
        ObjectNode bucket = null;
        ArrayNode groups = mapper.createArrayNode();
        ArrayNode buckets = null;
        String[] rawGroups = raw.split("[\r\n]+");
        String[] reducedRawGroups = Arrays.copyOfRange(rawGroups, 7, rawGroups.length);
        int state = -1;
        for (String line : reducedRawGroups) {
            String key;
            Matcher matcher;
            String value;
            Pattern pattern;
            if (line.indexOf("Group Id") == 0) {
                state = 0;
                group = mapper.createObjectNode();
                groups.add((JsonNode)group);
                buckets = group.putArray("buckets");
                pattern = Pattern.compile("(\\d+)");
                matcher = pattern.matcher(line);
                if (!matcher.find()) continue;
                group.put("id", Integer.parseInt(matcher.group(1).strip()));
                continue;
            }
            if (line.contains("Group Type")) {
                state = 1;
                pattern = Pattern.compile("Group Type : (.*)");
                matcher = pattern.matcher(line);
                if (!matcher.find()) continue;
                group.put("type", matcher.group(1).strip());
                continue;
            }
            if (line.contains("Bucket entry list for group")) {
                state = 2;
                bucket = mapper.createObjectNode();
                buckets.add((JsonNode)bucket);
                continue;
            }
            if (state == 1) {
                pattern = Pattern.compile("([a-zA-Z\\s]+):[\\s]+(\\d+)");
                matcher = pattern.matcher(line);
                while (matcher.find()) {
                    key = matcher.group(1).strip();
                    value = matcher.group(2).strip();
                    group.put(key, value);
                }
                continue;
            }
            if (state != 2) continue;
            pattern = Pattern.compile("([a-zA-Z\\s]+|Output port):?[\\s]+(NA|\\w{2}(:\\w{2}){5}|\\d+(\\/\\d+)?)");
            matcher = pattern.matcher(line);
            while (matcher.find()) {
                key = matcher.group(1).strip();
                value = matcher.group(2).strip();
                if (key.contains("Index")) continue;
                bucket.put(key, value);
            }
        }
        return groups;
    }
}