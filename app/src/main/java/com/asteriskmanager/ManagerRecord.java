package com.asteriskmanager;

public class ManagerRecord {
    String Name;
    String Secret;
    String Deny;
    String Permit;
    String Timeout;
    String Read;
    String Write;

    public String getRead() {
        return Read;
    }

    public void setRead(String read) {
        Read = read;
    }

    public String getWrite() {
        return Write;
    }

    public void setWrite(String write) {
        Write = write;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSecret() {
        return Secret;
    }

    public void setSecret(String secret) {
        Secret = secret;
    }

    public String getDeny() {
        return Deny;
    }

    public void setDeny(String deny) {
        Deny = deny;
    }

    public String getPermit() {
        return Permit;
    }

    public void setPermit(String permit) {
        Permit = permit;
    }

    public String getTimeout() {
        return Timeout;
    }

    public void setTimeout(String timeout) {
        Timeout = timeout;
    }

}
