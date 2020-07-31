package com.madrigal.openwiz;
import com.google.gson.*;
import java.nio.charset.StandardCharsets;

/**
 * Stores a state sent to, or received by, a light
 */
public class WizState {

    private static final Gson gson = new Gson();

    /**
     * The method name, required for all state objects.
     */
    public WizMethod method;

    /**
     * Describes the parameters of a method call.
     */
    public WizParams params;

    /**
     * Describes the result of a method call.
     */
    public WizResult result;

    /**
     * Describes an error returned by a Wiz light.
     */
    public WizError error;

    /**
     * The ID of the state.
     */
    public Integer id;

    /**
     * Generates an object that can be used to register the host with a Wiz light.
     * @param homeId The Home ID of the Wiz light
     * @param hostIp The IPv4 of the host machine, in standard dot notation
     * @param hostMac The MAC of the host machine's interface card
     * @return A WizState
     */
    public static WizState MakeRegistration(int homeId, String hostIp, byte[] hostMac) {
        WizState state = new WizState();
        state.method = WizMethod.registration;
        state.params = new WizParams();
        state.params.homeId = homeId;
        state.params.phoneIp = hostIp;
        state.params.phoneMac = Utils.bytesToHexString(hostMac);
        state.params.register = true;
        return state;
    }

    /**
     * Generates an object that can be used to request the current state of a light.
     * @return An object containing the request.
     */
    public static WizState MakeGetPilot() {
        WizState state = new WizState();
        state.method = WizMethod.getPilot;
        return state;
    }

    /**
     * Generates an object that can be used to request the user's configuration of a light.
     * @return An object containing the request.
     */
    public static WizState MakeGetUserConfig() {
        WizState state = new WizState();
        state.method = WizMethod.getUserConfig;
        return state;
    }

    /**
     * Generates an object that can be used to request the internal configuration of a light.
     * @return An object containing the request.
     */
    public static WizState MakeGetSystemConfig() {
        WizState state = new WizState();
        state.method = WizMethod.getSystemConfig;
        return state;
    }

    /**
     * Deserializes JSON data from a byte array.
     * @param data The data to deserialize.
     * @param length The length of the data to deserialize
     * @return A WizState, or null if the data is not valid json
     */
    public static WizState parseUTF8(byte[] data, int offset, int length) {
        return parse(new String(data, offset, length, StandardCharsets.UTF_8));
    }

    /**
     * Deserializes a JSON string into a WizState object.
     * @param json The JSON string to deserialize.
     * @return A WizState, or null if the string is not valid json.
     */
    public static WizState parse(String json) {
        WizState state = null;
        try {
            state = gson.fromJson(json, WizState.class);
        }
        catch (JsonSyntaxException ignored) {
        }
        return state;
    }

    /**
     * Serializes this object into a byte array for transmission.
     * @return A utf-8 encoded JSON string as a byte array.
     */
    public byte[] toUTF8() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gets the JSON representation of this state.
     * @return A JSON string.
     */
    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
