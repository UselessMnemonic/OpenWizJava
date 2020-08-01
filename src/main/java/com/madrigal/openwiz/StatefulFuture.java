package com.madrigal.openwiz;

import java.util.concurrent.CompletableFuture;

/**
 * A class meant to mimic the .NET System.IAsyncResult interface by giving
 * an ordinary {@link CompletableFuture} a convenient user-defined state object.
 *
 * @param <V> The type of result stored by this Future
 */
public class StatefulFuture<V> extends CompletableFuture<V> {

    // The internal state object
    private Object stateObject;

    /**
     * Creates a StatefulFuture
     */
    public StatefulFuture() {
        super();
    }

    /**
     * Gets the user-defined state object.
     *
     * @return The state object as set by {@link StatefulFuture#setStateObject(Object)}
     */
    public Object getStateObject() {
        return stateObject;
    }

    /**
     * Sets the user-defined state object.
     *
     * @param stateObject The the user-defined state object
     */
    public void setStateObject(Object stateObject) {
        this.stateObject = stateObject;
    }
}
