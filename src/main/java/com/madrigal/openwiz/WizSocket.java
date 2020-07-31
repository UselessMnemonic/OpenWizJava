package com.madrigal.openwiz;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * A socket-like class that wraps a standard {@link DatagramSocket} to handle communications.
 */
public class WizSocket implements Closeable {

    /**
     * Operational constants
     */
    public static final int PORT_PILOT = 38900;
    public static final int PORT_DISCOVER = 38899;
    private static final int BUFFER_SIZE = 256;

    // Performs asynchronous IO operations
    private final ExecutorService exeggutor;

    // The internal socket
    private DatagramSocket socket;

    /**
     * Creates a new UDP/IP {@link DatagramSocket} for communications.
     * @throws SocketException If the underlying socket could not be instantiated.
     */
    public WizSocket() throws SocketException {
        exeggutor = Executors.newCachedThreadPool();
        socket = new DatagramSocket(null);
    }

    /**
     * Associates this socket as a server socket.
     * @throws SocketException If the underlying socket could not bind.
     */
    public void bind() throws SocketException {
        socket.bind(new InetSocketAddress(PORT_PILOT));
    }

    /**
     * Releases all resources used by this socket and closes the underlying socket.
     */
    public void close() {
        socket.close();
    }

    /**
     * Sends data to a remote light.
     * @param s A {@link WizState} to send to the remote light
     * @param handle A {@link WizHandle} that identifies the remote light
     * @return The number of bytes sent over the network.
     * @throws IOException If the underlying socket could not perform the operation.
     */
    public int send(WizState s, WizHandle handle) throws IOException {
        byte[] data = s.toUTF8();
        DatagramPacket packet = new DatagramPacket(data, data.length, handle.getIp(), PORT_DISCOVER);
        socket.send(packet);
        return packet.getLength();
    }

    /**
     * Receives data from a remote light.
     * @param handle A {@link WizHandle} that identifies the remote light
     * @return The WizState sent by the remote light
     * @throws IOException If the underlying socket could not perform the operation.
     */
    public WizState receive(WizHandle handle) throws IOException {
        byte[] data = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        return WizState.parseUTF8(packet.getData(), 0, packet.getLength());
    }

    /**
     * Sends data asynchronously.
     * @param s The data to send.
     * @param handle The handle to the remote light.
     * @param callback The callback to invoke after the send completes.
     * @return A {@link Future} that can be used to track the operation.
     */
    public Future<Integer> beginSend(WizState s, WizHandle handle, Consumer<Future<Integer>> callback) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        exeggutor.submit(() -> {
            byte[] data = s.toUTF8();
            DatagramPacket packet = new DatagramPacket(data, data.length, handle.getIp(), PORT_DISCOVER);
            try {
                socket.send(packet);
                future.complete(packet.getLength());
            }
            catch (IOException e) {
                future.completeExceptionally(e);
            }
            finally {
                if (callback != null) {
                    callback.accept(future);
                }
            }
        });
        return future;
    }

    /**
     * Ends a pending asynchronous send.
     * @param future A Future that stores state information
     *               for this asynchronous operation.
     * @return If successful, the number of bytes sent.
     * @throws ExecutionException if the operation raised an error during execution
     */
    public int endSend(Future<Integer> future) throws ExecutionException {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Receives data asynchronously from a connected OpenWiz.WizSocket.
     * @param handle The handle to the remote light.
     * @param callback The System.AsyncCallback delegate to fall after the receive completes.
     * @return A {@link Future} that can be used to track the operation.
     */
    public Future<WizState> beginRecieve(WizHandle handle, Consumer<Future<WizState>> callback) {
        CompletableFuture<WizState> future = new CompletableFuture<>();
        exeggutor.submit(() -> {
            byte[] data = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                future.complete(WizState.parseUTF8(packet.getData(), 0, packet.getLength()));
            }
            catch (IOException e) {
                future.completeExceptionally(e);
            }
            finally {
                callback.accept(future);
            }
        });
        return future;
    }

    /**
     * Ends a pending asynchronous receive.
     * @param future The Future returned by {@link WizSocket#beginRecieve(WizHandle, Consumer)}
     * @return If successful, a WizState
     */
    public WizState endReceive(Future<WizState> future) throws ExecutionException {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Gets the underlying {@link DatagramSocket}
     * @return the underlying socket
     */
    public DatagramSocket getSocket() {
        return socket;
    }
}
