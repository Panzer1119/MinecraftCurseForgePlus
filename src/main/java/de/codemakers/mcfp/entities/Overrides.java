package de.codemakers.mcfp.entities;

import java.util.List;

public class Overrides {
    
    protected String overallHash;
    protected List<ModOverride> modOverrides;
    
    public Overrides(String overallHash, List<ModOverride> modOverrides) {
        this.overallHash = overallHash;
        this.modOverrides = modOverrides;
    }
    
    public final String getOverallHash() {
        return overallHash;
    }
    
    public final Overrides setOverallHash(String overallHash) {
        this.overallHash = overallHash;
        return this;
    }
    
    public final List<ModOverride> getModOverrides() {
        return modOverrides;
    }
    
    public final Overrides setModOverrides(List<ModOverride> modOverrides) {
        this.modOverrides = modOverrides;
        return this;
    }
    
    @Override
    public String toString() {
        return "Overrides{" + "overallHash='" + overallHash + '\'' + ", modOverrides=" + modOverrides + '}';
    }
    
}
