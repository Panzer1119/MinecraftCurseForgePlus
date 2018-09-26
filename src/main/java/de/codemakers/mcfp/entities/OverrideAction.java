package de.codemakers.mcfp.entities;

public enum OverrideAction {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace"),
    ENABLE("enable"),
    DISABLE("disable"),
    CHANGE("change"),
    UNKNOWN(null);
    
    private final String action;
    
    OverrideAction(String action) {
        this.action = action;
    }
    
    public final String getAction() {
        return action;
    }
    
    public static final OverrideAction ofAction(String action) {
        for (OverrideAction overrideAction : values()) {
            if (overrideAction.getAction().equals(action)) {
                return overrideAction;
            }
        }
        return UNKNOWN;
    }
    
}
