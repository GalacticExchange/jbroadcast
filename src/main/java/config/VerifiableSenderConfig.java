package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class VerifiableSenderConfig extends BaseConfig {

    public VerifiableSenderConfig(@JsonProperty(value = "address", required = true) String address,
                                  @JsonProperty(value = "port", required = true) Integer port,
                                  @JsonProperty(value = "parties", required = true) ArrayList<Map> parties) {
        super(address, port, parties);
    }


    public static VerifiableSenderConfig load(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(path), VerifiableSenderConfig.class);
    }
}
