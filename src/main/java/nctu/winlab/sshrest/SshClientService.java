package nctu.winlab.sshrest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Service for handling command from sshctl and sshrest
 */
public interface SshClientService {
    public static final String ALL_CLIENTS_OPERATION_INDEX = "ALL";

    /**
     * Print all devices registered in netcfg
     */
    public void printDevices();
    /**
     * Get all devices registered in netcfg
     * 
     * @return list of devices; if no device exists, then return empty array node
     */
    public ArrayNode getDevices();
    /**
     * Get controllers of a switch or all switches
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @return JSON object included fields: error, msg, array list of controllers
     */
    public ObjectNode getController(String deviceID);
    /**
     * Set controller of a switch or all switches
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param ip IPv4 address of controller
     * @param port port of controller
     */
    public void setController(String deviceID, String ip, String port);
    /**
     * Remove controller from switch
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param ip IPv4 address of controller
     */
    public void unsetController(String deviceID, String ip);
    /**
     * Get installed flow rules of switch
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @return JSON object included fields: error, msg, array list of flow rules
     */
    public ObjectNode getFlows(String deviceID);
    /**
     * Get installed groups of switch
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @return JSON object included fields: error, msg, array list of groups
     */
    public ObjectNode getGroups(String deviceID);
    /**
     * Write system log of switch into a file
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param filename target filename; print to screen if no filename is specified
     */
    public void getLogs(String deviceID, String filename);
    /**
     * Execute non-priviledged command on server
     * 
     * @param deviceID server name; if given "ALL", then represent all servers
     * @param cmd command for execution
     */
    public void execCommand(String deviceID, String cmd);
    /**
     * Execute priviledged command on server
     * 
     * @param deviceID server name; if given "ALL", then represent all servers
     * @param cmd command for execution
     */
    public void execSudoCommand(String deviceID, String cmd);
    /**
     * Set SSID of an AP
     * 
     * @param deviceID AP name; if given "ALL", then represent all APs
     * @param ifname interface name
     * @param ssid SSID
     */
    public void setSsid(String deviceID, String ifname, String ssid);
    /**
     * Set VXLAN source loopback interface ID
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param loopbackId loopback ID
     */
    public void setVxlanSourceInterfaceLoopback(String deviceID, String loopbackId);
    /**
     * Binding VLAN ID to VXLAN VNI
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param vnid VXLAN Network Identifier (VNI)
     * @param vid VLAN ID (VID)
     */
    public void setVxlanVlan(String deviceID, String vnid, String vid);
    /**
     * Set VTEP IP and MAC for a specified VNI
     * 
     * @param deviceID switch name; if given "ALL", then represent all switches
     * @param vnid VXLAN Network Identifier (VNI)
     * @param ip IPv4 address of VTEP
     * @param mac MAC address of VTEP
     */
    public void setVxlanVtep(String deviceID, String vnid, String ip, String mac);
}