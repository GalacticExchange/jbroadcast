package config;

import java.util.ArrayList;
import java.util.Map;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


//@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseConfig {

    private String address;

    private int port;

    private ArrayList<Map> parties;


    @JsonCreator
    public BaseConfig(@JsonProperty(value = "address", required = true) String address,
                      @JsonProperty(value = "port", required = true) Integer port,
                      @JsonProperty(value = "parties", required = true) ArrayList<Map> parties) {

        this.address = address;
        this.port = port;
        this.parties = parties;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList<Map> getParties() {
        return parties;
    }

    public void setParties(ArrayList<Map> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }



}
