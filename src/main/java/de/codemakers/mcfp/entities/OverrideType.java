package de.codemakers.mcfp.entities;

public enum OverrideType {
    MOD("mod"),
    CONFIG("config"),
    SCRIPT("script"),
    RESOURCE("resource"),
    CUSTOM("custom"),
    UNKNOWN(null);
    
    private final String type;
    
    OverrideType(String type) {
        this.type = type;
    }
    
    public final String getType() {
        return type;
    }
    
    public static final OverrideType ofType(String type) {
        for (OverrideType overrideType : values()) {
            if (overrideType.getType().equals(type)) {
                return overrideType;
            }
        }
        return UNKNOWN;
    }
}
