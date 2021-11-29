package com.asteriskmanager.fragments.channelfragment;

public class ChannelRecord {

    String ChannelName;
    String ChannelState;
    String ChannelStateDesc;
    String CallerIDNum;
    String CallerIDName;
    String ConnectedLineNum;
    String ConnectedLineName;
    String Exten;
    String Priority;
    String Application;
    String Duration;

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getChannelState() {
        return ChannelState;
    }

    public void setChannelState(String channelState) {
        ChannelState = channelState;
    }

    public String getChannelStateDesc() {
        return ChannelStateDesc;
    }

    public void setChannelStateDesc(String channelStateDesc) {
        ChannelStateDesc = channelStateDesc;
    }

    public String getCallerIDNum() {
        return CallerIDNum;
    }

    public void setCallerIDNum(String callerIDNum) {
        CallerIDNum = callerIDNum;
    }

    public String getCallerIDName() {
        return CallerIDName;
    }

    public void setCallerIDName(String callerIDName) {
        CallerIDName = callerIDName;
    }

    public String getConnectedLineNum() {
        return ConnectedLineNum;
    }

    public void setConnectedLineNum(String connectedLineNum) {
        ConnectedLineNum = connectedLineNum;
    }

    public String getConnectedLineName() {
        return ConnectedLineName;
    }

    public void setConnectedLineName(String connectedLineName) {
        ConnectedLineName = connectedLineName;
    }

    public String getExten() {
        return Exten;
    }

    public void setExten(String exten) {
        Exten = exten;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    ChannelRecord(){

    }
}
