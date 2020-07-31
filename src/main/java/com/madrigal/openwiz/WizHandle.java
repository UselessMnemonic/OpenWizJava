package com.madrigal.openwiz;
import java.net.Inet4Address;

/**
 * A class that uniquely identifies a remote light.
 */
public class WizHandle {

    // MAC Address of a remote light
    private String mac;

    // IPv4 Address of a remote light
    private Inet4Address ip;

    /**
     * Gets the MAC address of the remote light.
     * @return an unformatted hex string
     */
    public String getMac() {
        return mac;
    }

    /**
     * Gets the IPv4 address of the remote light.
     * @return an {@link Inet4Address}
     */
    public Inet4Address getIp() {
        return ip;
    }

    /**
     * Creates a handle that can be used to connect to, or identify, a remote light.
     * Meant to be used with a {@link WizHandle}
     * @param mac A 12 digit hex string representing the MAC of a remote light
     * @param ip The {@link Inet4Address} of a remote light
     */
    public WizHandle(String mac, Inet4Address ip){
        if (mac == null) throw new IllegalArgumentException("MAC cannot be null.");
        if (ip == null) throw new IllegalArgumentException("IP cannot be null.");
        if (mac.length() != 12) throw new IllegalArgumentException("MAC must be 12 hex digits.");

        for (char c : mac.toCharArray()) {
            if ((c < '0' || c > '9') &&
                (c < 'a' || c > 'f')) throw new IllegalArgumentException("MAC must be a hex string.");
        }

        this.mac = mac.toLowerCase();
        this.ip = ip;
    }
}
