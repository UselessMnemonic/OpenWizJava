package com.madrigal.openwiz;

/**
 * Stores the result of a remote method call.
 */
public class WizResult extends WizParams {

    /**
     * Whether a method call was successful or not.
     */
    public Boolean success;

    /**
     * The RSSI of the last transmission.
     */
    public Integer rssi;
}
