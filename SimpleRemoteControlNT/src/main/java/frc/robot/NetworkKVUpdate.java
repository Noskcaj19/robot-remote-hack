package frc.robot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class NetworkKVUpdate {
    @JsonProperty("k")
    public String key;
    @JsonProperty("v")
    public JsonNode value;

    public NetworkKVUpdate() {
    }

    public NetworkKVUpdate(String key, JsonNode value) {
        this.key = key;
        this.value = value;
    }
}
