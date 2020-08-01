package com.madrigal.openwiz;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
     * Creates a new {@link DatagramSocket} for communication.
     * It is encouraged the user create one socket to service several lights.
     *
     * @throws SocketException If the underlying socket could not be instantiated.
     */
    public WizSocket() throws SocketException {
        exeggutor = Executors.newCachedThreadPool();
        socket = new DatagramSocket(null);
    }

    /**
     * Associates this socket as a server socket.
     * This allows the user to accept incoming transmissions without polling a light first.
     *
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
     *
     * @param s      A {@link WizState} to send to the remote light
     * @param handle A {@link WizHandle} that identifies the remote light on the network
     * @return The number of bytes given to the OS for writing.
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
     *
     * @param handle A {@link WizHandle} that identifies the remote light
     * @return A WizState constructed from the received data.
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
     *
     * @param s      The data to send.
     * @param handle The handle to the remote light.
     * @return A {@link Future} that can be used to track the operation.
     */
    public Future<Integer> sendAsync(WizState s, WizHandle handle) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        exeggutor.submit(() -> {
            byte[] data = s.toUTF8();
            DatagramPacket packet = new DatagramPacket(data, data.length, handle.getIp(), PORT_DISCOVER);
            try {
                socket.send(packet);
                future.complete(packet.getLength());
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Receives data asynchronously.
     *
     * @param handle The handle to the remote light.
     * @return A {@link Future} that can be used to track the operation.
     */
    public Future<WizState> receiveAsync(WizState s, WizHandle handle) {
        CompletableFuture<WizState> future = new CompletableFuture<>();
        exeggutor.submit(() -> {
            byte[] data = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                future.complete(WizState.parseUTF8(packet.getData(), 0, packet.getLength()));
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Sends data asynchronously.
     *
     * @param s        The data to send.
     * @param handle   The handle to the remote light.
     * @param callback The callback to invoke after the send completes.
     * @param state    A user defined state object
     * @return A {@link StatefulFuture} that can be used to track the operation.
     */
    public StatefulFuture<Integer> beginSend(WizState s, WizHandle handle, Consumer<StatefulFuture<Integer>> callback, Object state) {
        StatefulFuture<Integer> future = new StatefulFuture<>();
        future.setStateObject(state);
        exeggutor.submit(() -> {
            byte[] data = s.toUTF8();
            DatagramPacket packet = new DatagramPacket(data, data.length, handle.getIp(), PORT_DISCOVER);
            try {
                socket.send(packet);
                future.complete(packet.getLength());
            } catch (IOException e) {
                future.completeExceptionally(e);
            } finally {
                callback.accept(future);
            }
        });
        return future;
    }

    /**
     * Waits for an asynchronous send to end, and returns the result.
     *
     * @param future The Future returned by a call to {@link WizSocket#beginSend}
     * @return If successful, the number of bytes sent.
     * @throws ExecutionException if the operation raised an error during execution
     */
    public int endSend(Future<Integer> future) throws ExecutionException {
        while (!future.isDone() && !future.isCancelled()) ;
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Receives data asynchronously.
     *
     * @param handle   The handle to the remote light.
     * @param callback The callback to invoke after the send completes.
     * @param state    A user defined state object
     * @return A {@link StatefulFuture} that can be used to track the operation.
     */
    public Future<WizState> beginReceive(WizHandle handle, Consumer<StatefulFuture<WizState>> callback, Object state) {
        StatefulFuture<WizState> future = new StatefulFuture<>();
        future.setStateObject(state);
        exeggutor.submit(() -> {
            byte[] data = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                future.complete(WizState.parseUTF8(packet.getData(), 0, packet.getLength()));
            } catch (IOException e) {
                future.completeExceptionally(e);
            } finally {
                callback.accept(future);
            }
        });
        return future;
    }

    /**
     * Waits for an asynchronous receive to end, and returns the result.
     *
     * @param future The Future returned by a call to {@link WizSocket#beginReceive}
     * @return If successful, the received data.
     * @throws ExecutionException if the operation raised an error during execution
     */
    public WizState endReceive(Future<WizState> future) throws ExecutionException {
        while (!future.isDone() && !future.isCancelled()) ;
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Gets the underlying {@link DatagramSocket}
     *
     * @return the underlying socket
     */
    public DatagramSocket getSocket() {
        return socket;
    }

}
