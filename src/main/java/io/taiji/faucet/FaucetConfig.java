package io.taiji.faucet;

public class FaucetConfig {
    public static final String CONFIG_NAME = "faucet";

    String address;
    String password;

    public FaucetConfig() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
