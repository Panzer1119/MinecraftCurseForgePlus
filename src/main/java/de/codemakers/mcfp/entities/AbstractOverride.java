/*
 *    Copyright 2018 Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package de.codemakers.mcfp.entities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public abstract class AbstractOverride {
    
    protected String hash;
    protected OverrideAction overrideAction;
    protected String file;
    protected String url;
    protected String data;
    
    public AbstractOverride(String hash, OverrideAction overrideAction, String file, String url, String data) {
        this.hash = hash;
        this.overrideAction = overrideAction;
        this.file = file;
        this.url = url;
        this.data = data;
    }
    
    public abstract OverrideType getOverrideType();
    
    public abstract boolean performOverride() throws Exception;
    
    public final String getHash() {
        return hash;
    }
    
    public final AbstractOverride setHash(String hash) {
        this.hash = hash;
        return this;
    }
    
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
        return "AbstractOverride{" + "hash='" + hash + '\'' + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", url='" + url + '\'' + ", data='" + data + '\'' + '}';
    }
    
}
