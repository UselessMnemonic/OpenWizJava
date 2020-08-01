package com.madrigal.openwiz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class WizDiscoveryService {
    private static final int PORT_DISCOVERY = 38899;

    private final String hostIp;

    private DatagramSocket discoverySocket;
    private Consumer<WizHandle> handleConsumer;
    private byte[] hostMac;

    private volatile boolean keepAlive;

    public WizDiscoveryService(String hostIp, byte[] hostMac) {
        this.hostIp = hostIp;
        this.hostMac = hostMac;
        keepAlive = false;
    }

    public void start(int homeId, Consumer<WizHandle> handleConsumer) throws IOException {
        if (keepAlive | handleConsumer == null | homeId <= 0) {
            return;
        }
        keepAlive = true;
        discoverySocket = new DatagramSocket(PORT_DISCOVERY);
        discoverySocket.setBroadcast(true);
        this.handleConsumer = handleConsumer;

        byte[] data = WizState.MakeRegistration(homeId, hostIp, hostMac).toUTF8();
        DatagramPacket packet = new DatagramPacket(data, data.length, Inet4Address.getByName("255.255.255.255"), PORT_DISCOVERY);
        new Thread(this::discoveryLoop).start();
        discoverySocket.send(packet);
    }

    public void stop() {
        if (keepAlive) {
            keepAlive = false;
            discoverySocket.close();
            discoverySocket = null;
        }
    }

    private void discoveryLoop() {
        try {
            while (keepAlive) {
                byte[] data = new byte[256];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                discoverySocket.receive(packet);
                WizState wState = WizState.parseUTF8(data, 0, packet.getLength());
                if (wState == null) {
                    System.out.printf("[WARNING] WizDiscoveryService@%s: Got bad json message:\n\t%s\n", hostIp, new String(data, StandardCharsets.UTF_8));
                } else if (wState.error != null) {
                    System.out.printf("[WARNING] WizDiscoveryService@%s: Encountered ", hostIp);

                    if (wState.error.code == null) System.out.print("unknwon error");
                    else System.out.printf("error %d", wState.error.code);
                    if (wState.error.message != null) System.out.printf(" -- %s", wState.error.message);
                    System.out.printf(" from %s\n", packet.getAddress().toString());
                } else if (wState.result != null) {
                    System.out.printf("[INFO] WizDiscoveryService@%s: Got response:\n", hostIp);
                    System.out.printf("\t%s\n", wState.toString());
                    handleConsumer.accept(new WizHandle(wState.result.mac, (Inet4Address) packet.getAddress()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
