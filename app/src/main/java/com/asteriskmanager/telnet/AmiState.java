package com.asteriskmanager.telnet;

public class AmiState {
    public String action;
    Boolean ResultOperation;
    String instruction;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


    public Boolean getResultOperation() {
        return ResultOperation;
    }

    public void setResultOperation(Boolean resultOperation) {
        ResultOperation = resultOperation;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    String Description;
}