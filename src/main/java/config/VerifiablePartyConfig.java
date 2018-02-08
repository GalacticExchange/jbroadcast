package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class VerifiablePartyConfig extends BaseConfig {

    private String id;

    private String publicKey;
    private String privateKey;

    public VerifiablePartyConfig(@JsonProperty(value = "id", required = true) String id,
                                 @JsonProperty(value = "address", required = true) String address,
                                 @JsonProperty(value = "port", required = true) Integer port,
                                 @JsonProperty(value = "public_key", required = true) String publicKey,
                                 @JsonProperty(value = "private_key", required = true) String privateKey,
                                 @JsonProperty(value = "parties", required = true) ArrayList<Map> parties
    ) {
        super(address, port, parties);
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public static VerifiablePartyConfig load(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(path), VerifiablePartyConfig.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
