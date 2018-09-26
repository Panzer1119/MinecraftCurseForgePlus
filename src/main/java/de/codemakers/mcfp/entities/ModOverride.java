package de.codemakers.mcfp.entities;

public class ModOverride extends AbstractOverride {
    
    protected String file_2;
    
    public ModOverride(OverrideAction overrideAction, String file, String url, String data, String file_2) {
        super(overrideAction, file, url, data);
        this.file_2 = file_2;
    }
    
    public final String getFile_2() {
        return file_2;
    }
    
    public final ModOverride setFile_2(String file_2) {
        this.file_2 = file_2;
        return this;
    }
    
    @Override
    public OverrideType getOverrideType() {
        return OverrideType.MOD;
    }
    
    @Override
    public String toString() {
        return "ModOverride{" + "file='" + file + '\'' + ", file_2='" + file_2 + '\'' + ", overrideAction=" + overrideAction + ", url='" + url + '\'' + ", data=" + data + '}';
    }
    
}
