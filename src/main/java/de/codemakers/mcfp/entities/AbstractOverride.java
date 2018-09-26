package de.codemakers.mcfp.entities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public abstract class AbstractOverride {
    
    protected OverrideAction overrideAction;
    protected String file;
    protected String url;
    protected String data;
    
    public AbstractOverride(OverrideAction overrideAction, String file, String url, String data) {
        this.overrideAction = overrideAction;
        this.file = file;
        this.url = url;
        this.data = data;
    }
    
    public abstract OverrideType getOverrideType();
    
    public OverrideAction getOverrideAction() {
        return overrideAction;
    }
    
    public AbstractOverride setOverrideAction(OverrideAction overrideAction) {
        this.overrideAction = overrideAction;
        return this;
    }
    
    public String getFile() {
        return file;
    }
    
    public AbstractOverride setFile(String file) {
        this.file = file;
        return this;
    }
    
    public String getUrl() {
        return url;
    }
    
    public URL toURL() throws MalformedURLException {
        return url == null ? null : new URL(getUrl());
    }
    
    public AbstractOverride setUrl(String url) {
        this.url = url;
        return this;
    }
    
    public final String getData() {
        return data;
    }
    
    public final AbstractOverride setData(String data) {
        this.data = data;
        return this;
    }
    
    public boolean isOverrideType(OverrideType overrideType) {
        return Objects.equals(overrideType, getOverrideType());
    }
    
    public boolean isOverrideAction(OverrideAction overrideAction) {
        return Objects.equals(overrideAction, getOverrideAction());
    }
    
    
    public boolean isOverride(Class<? extends AbstractOverride> clazz) {
        return getClass().equals(clazz) /*clazz != null && AbstractOverride.class.isAssignableFrom(clazz)*/;
    }
    
    @Override
    public String toString() {
        return "AbstractOverride{" + "overrideAction=" + overrideAction + ", file='" + file + '\'' + ", url='" + url + '\'' + ", data='" + data + '\'' + '}';
    }
    
}
