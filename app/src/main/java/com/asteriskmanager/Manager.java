package com.asteriskmanager;

public class Manager {
    String Name;
    String Secret;
    String Deny;
    String Permit;
    String Timeout;
    String Permissions;

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

    public String getPermissions() {
        return Permissions;
    }

    public void setPermissions(String permissions) {
        Permissions = permissions;
    }

}
