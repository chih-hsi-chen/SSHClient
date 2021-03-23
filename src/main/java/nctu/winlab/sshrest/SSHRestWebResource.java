package nctu.winlab.sshrest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.onosproject.rest.AbstractWebResource;
import static org.onlab.util.Tools.readTreeFromStream;

@Path(value="devices")
public class SSHRestWebResource extends AbstractWebResource {
    @GET
    public Response queryDevices() {
        SshClientService clientService = get(SshClientService.class);
        return Response.ok(clientService.getDevices().toString(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path(value="controller/{switchName}")
    public Response queryController(@PathParam(value="switchName") String switchName) {
        SshClientService clientService = get(SshClientService.class);
        return Response.ok(clientService.getController(switchName).toString(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * 
     * @param switchName
     * @param stream
     * @return
     * @onos.rsModel setController
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="controller/{switchName}")
    public Response setController(@PathParam(value="switchName") String switchName,
                                  InputStream stream) {
        
        SshClientService clientService = get(SshClientService.class);                              
        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String ip = jsonTree.path("ip").asText("");
            String port = jsonTree.path("port").asText("6653");

            if (ip == "") {
                throw new IllegalArgumentException("there is need for controller IP address");
            }
            clientService.setController(switchName, ip, port);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        
        return Response.ok().build();
    }

    /**
     * 
     * @param switchName
     * @param stream
     * @return
     * @onos.rsModel unsetController
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "controller/{switchName}")
    public Response unsetController(@PathParam(value="switchName") String switchName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);

        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String ip = jsonTree.get("ip").asText("");

            if (ip == "")
                throw new IllegalArgumentException("Please specify controller IP address");
            clientService.unsetController(switchName, ip);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    @GET
    @Path(value="flows/{switchName}")
    public Response queryFlows(@PathParam(value="switchName") String switchName) {
        SshClientService clientService = get(SshClientService.class);
        return Response.ok(clientService.getFlows(switchName).toString(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path(value="groups/{switchName}")
    public Response queryGroups(@PathParam(value="switchName") String switchName) {
        SshClientService clientService = get(SshClientService.class);
        return Response.ok(clientService.getGroups(switchName).toString(), MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "log/{switchName}")
    public Response generateLog(@PathParam(value="switchName") String switchName,
                                @QueryParam(value = "filename") String filename) {
        SshClientService clientService = get(SshClientService.class);
        if (filename == "")
            throw new IllegalArgumentException("Please specify filename; otherwise, it will output to ONOS logs");
        clientService.getLogs(switchName, filename);
        return Response.ok().build();
    }

    /**
     * 
     * @param serverName
     * @param stream
     * @return
     * @onos.rsModel execCommand
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "command/{serverName}")
    public Response execCommand(@PathParam(value="serverName") String serverName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);

        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String cmd = jsonTree.get("command").asText("");

            if (cmd == "")
                throw new IllegalArgumentException("Please specify your command");
            clientService.execCommand(serverName, cmd);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    /**
     * 
     * @param serverName
     * @param stream
     * @return
     * @onos.rsModel execSudoCommand
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "scommand/{serverName}")
    public Response execSudoCommand(@PathParam(value="serverName") String serverName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);

        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String cmd = jsonTree.get("command").asText("");

            if (cmd == "")
                throw new IllegalArgumentException("Please specify your command");
            clientService.execSudoCommand(serverName, cmd);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    /**
     * 
     * @param apName
     * @param stream
     * @return
     * @onos.rsModel setSSID
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "ssid/{apName}")
    public Response setSSID(@PathParam(value="apName") String apName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);

        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String ifname = jsonTree.get("ifname").asText("");
            String ssid = jsonTree.get("ssid").asText("");

            if (ssid == "" || ifname == "")
                throw new IllegalArgumentException("Please specify SSID of AP");
            clientService.setSsid(apName, ifname, ssid);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    /**
     * 
     * @param switchName
     * @param stream
     * @return
     * @onos.rsModel setVxlanLoopback
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "vxlanloopback/{switchName}")
    public Response setVxlanSourceInterfaceLoopback(@PathParam(value="switchName") String switchName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);
        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String loopbackId = jsonTree.get("id").asText("");

            if (loopbackId == "")
                throw new IllegalArgumentException("Please specify VXLAN Source loopback ID");
            clientService.setVxlanSourceInterfaceLoopback(switchName, loopbackId);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    /**
     * 
     * @param switchName
     * @param stream
     * @return
     * @onos.rsModel setVxlanVlan
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "vxlanvlan/{switchName}")
    public Response setVxlanVlan(@PathParam(value="switchName") String switchName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);
        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String vnid = jsonTree.get("vnid").asText("");
            String vid = jsonTree.get("vid").asText("");

            if (vnid == "" || vid == "")
                throw new IllegalArgumentException("Please specify VNI or VLAN ID");
            clientService.setVxlanVlan(switchName, vnid, vid);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }

    /**
     * 
     * @param switchName
     * @param stream
     * @return
     * @onos.rsModel setVtep
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "vtep/{switchName}")
    public Response setVxlanVtep(@PathParam(value="switchName") String switchName, 
                                    InputStream stream) {
        SshClientService clientService = get(SshClientService.class);
        try {
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            String vnid = jsonTree.get("vnid").asText("");
            String ip = jsonTree.get("ip").asText("");
            String mac = jsonTree.get("mac").asText("");

            if (vnid == "" || ip == "")
                throw new IllegalArgumentException("Please specify both VNI and IP address");
            clientService.setVxlanVtep(switchName, vnid, ip, mac);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return Response.ok().build();
    }
}