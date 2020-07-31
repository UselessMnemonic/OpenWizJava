package com.madrigal.openwiz;

/**
 * Stores parameter information for a remote method call.
 */
public class WizParams {

    /*
     * These parameters are common for regular operation.
     */

    /**
     * Whether the remote light is on or off.
     * Set to true if the light is on, false otherwise.
     */
    public Boolean state;

    /**
     * The Scene ID of the current scene.
     * Set to some positive integer, or null if no scene is active.
     */
    public Integer sceneId;

    /**
     * The current speed at which a scene plays.
     * Set to an integer value in [0,100], or null if no scene is active.
     */
    public Integer speed;

    /**
     * Whether the current scene is playing.
     * Set to true if a scene is playing, false if not, or null if no scene is active.
     */
    public Boolean play;

    /**
     * The current Red component of the set color.
     * Set to an RGB byte coordinate, or null if no color is chosen.
     */
    public Byte r;

    /**
     * The current Green component of the set color.
     * Set to an RGB byte coordinate, or null if no color is chosen.
     */
    public Integer g;

    /**
     * The current Blue component of the set color.
     * Set to an RGB byte coordinate, or null if no color is chosen.
     */
    public Integer b;

    /**
     * The current Cool White component of the set color.
     * Set to a byte value in [0, 100] or null if no color is chosen.
     */
    public Byte c;

    /**
     * The Warm White component of the set color.
     * Set to a byte value in [0, 100] or null if no color is chosen.
     */
    public Byte w;

    /**
     * The current real white light temperature.
     * Set to a positive integer value in the range given by {@link WizParams#extRange},
     * or null if no temperature is chosen.
     */
    public Integer temp;

    /**
     * The brightness or intensity of the light.
     * Set to a byte value in [0, 100] or null if none is given.
     */
    public Byte dimming;

    /*
     * These parameters are used in registration
     */

    /**
     * The IP of the host machine.
     * Set to an IPv4 address in dot notation, or null if none is given.
     */
    public String phoneIp;

    /**
     * The MAC of the host machine.
     * Set to a 12-digit lowercase hex string, or null if none is given.
     */
    public String phoneMac;

    /**
     * Whether a registration request is successful (?)
     * Set to true if a register request is successful, false if not, or null
     * if no value is given.
     */
    public Boolean register;

    /*
     * These parameters are for configuration
     */

    /**
     * The name of the remote light.
     * Null if no value is given.
     */
    public String moduleName;

    /**
     * The MAC of the remote light.
     * Set to a 12-digit lowercase hex string, or null if none is given.
     */
    public String mac;

    /**
     * Unsure.
     * Set to an integer value, or null if none is given.
     */
    public Integer typeId;

    /**
     * The Home ID of the remote light.
     * Set to a positive integer value, or null if none is given.
     */
    public Integer homeId;

    /**
     * The Group ID of the remote light.
     * Set to a non-negative integer value, or null if none is given.
     */
    public Integer groupId;

    /**
     * The Room ID of the remote light.
     * Set to a non-negative integer value, or null if none is given.
     */
    public Integer roomId;

    /**
     * Unsure.
     * Set to a boolean, or null if no value is given.
     */
    public Boolean homeLock;

    /**
     * Unsure.
     * Set to a boolean value, or null if none is given.
     */
    public Boolean pairingLock;

    /**
     * The remote light's firmware version.
     * Null if no value is given.
     */
    public String fwVersion;

    /**
     * The fade-in time in milliseconds.
     * Set to a non-negative integer value, or null if none is given.
     */
    public Integer fadeIn;

    /**
     * The fade-out time in milliseconds.
     * Set to a non-negative integer value, or null if none is given.
     */
    public Integer fadeOut;

    /**
     * Whether to fade the nightlight mode (?)
     * Set to a boolean value, or null if none is given.
     */
    public Boolean fadeNight;

    /**
     * Unsure.
     * Set to an integer value, or null if none is given.
     */
    public Integer dftDim;

    /**
     * Unsure.
     * Set to an array of two integers, or null if none is given.
     */
    public int[] pwmRange;

    /**
     * Unsure.
     * Set to an array of two integers, or null if none is given.
     */
    public int[] drvConf;

    /**
     * The white light temperature range of the light in Kelvin.
     * Set to an array of two integers, or null if none is given.
     */
    public int[] whiteRange;

    /**
     * The white light temperature range advertised to the user.
     * Set to an array of two integers, or null if none is given.
     */
    public int[] extRange;

    /**
     * Whether to fade the nightlight mode (?)
     * Set to a boolean value, or null if none is given.
     */
    public Boolean po;
}
