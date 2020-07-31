package com.madrigal.openwiz;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestWiz {

    private ConcurrentHashMap<String, WizHandle> handles;
    private WizSocket socket;

    public TestWiz() throws SocketException {
        handles = new ConcurrentHashMap<>();
        socket = new WizSocket();
        socket.bind();
    }

    public void onDiscover(WizHandle discovered)
    {
        if (!handles.containsKey(discovered.getMac())) {
            System.out.println("[INFO] TestWizDiscovery::OnDiscover found light " + discovered.getMac());
            handles.put(discovered.getMac(), discovered);
            socket.beginRecieve(discovered, (futureState) -> updateCallback(futureState, discovered));
        }
    }

    private void updateCallback(Future<WizState> futureState, WizHandle fromHandle) {
        try {
            WizState state = socket.endReceive(futureState);
            System.out.printf("[INFO] TestWizDiscovery::UpdateCallback:\n\t%s\n", state.toString());
            socket.beginRecieve(fromHandle, (nextFutureState) -> updateCallback(nextFutureState, fromHandle));
            Thread.sleep(1000);
            socket.send(WizState.MakeGetPilot(), fromHandle);
        } catch (ExecutionException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        TestWiz twd = new TestWiz();
        WizDiscoveryService wds = new WizDiscoveryService("192.168.0.100", new byte[]{ (byte)0xf0, (byte)0x18, (byte)0x98, (byte)0x09, (byte)0x1A, (byte)0xD8});
        wds.start(390198, twd::onDiscover);
        try (Scanner sc = new Scanner(System.in)) {
            while (!sc.hasNextLine()) {}
        }
    }
}