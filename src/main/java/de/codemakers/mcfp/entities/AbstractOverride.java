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

import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.security.util.HashUtil;
import org.apache.commons.io.IOUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

public abstract class AbstractOverride {
    
    public static final String SUFFIX_DISABLED = ".disabled";
    
    protected String hash;
    protected OverridePolicy overridePolicy;
    protected OverrideAction overrideAction;
    protected String file;
    protected String url;
    protected String data;
    //temp
    protected transient byte[] temp = null;
    
    public AbstractOverride(String hash, OverridePolicy overridePolicy, OverrideAction overrideAction, String file, String url, String data, byte[] temp) {
        this.hash = hash;
        this.overridePolicy = overridePolicy;
        this.overrideAction = overrideAction;
        this.file = file;
        this.url = url;
        this.data = data;
        this.temp = temp;
    }
    
    public abstract OverrideType getOverrideType();
    
    abstract boolean performOverrideIntern(OverridePolicy overridePolicy) throws Exception;
    
    public boolean performOverride(OverridePolicy overridePolicy) throws Exception {
        Objects.requireNonNull(overrideAction, "overrideAction may not be null");
        Objects.requireNonNull(file, "file may not be null");
        return performOverrideIntern(overridePolicy);
    }
    
    public boolean performOverride(OverridePolicy overridePolicy, ToughConsumer<Throwable> failure) {
        try {
            return performOverride(getNonNullOverridePolicy(overridePolicy));
        } catch (Exception ex) {
            if (failure != null) {
                failure.acceptWithoutException(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean performOverrideWithoutException(OverridePolicy overridePolicy) {
        return performOverride(getNonNullOverridePolicy(overridePolicy), null);
    }
    
    public boolean checkHash(byte[] data) {
        return checkHash(data, false);
    }
    
    public boolean checkHash(byte[] data, boolean throwException) {
        if (!HashUtil.isDataValidSHA256(data, getHashAsBytes())) {
            if (throwException) {
                throw new IllegalArgumentException("the hash of the data does not match the given hash");
            }
            return false;
        }
        return true;
    }
    
    public String getHash() {
        return hash;
    }
    
    public byte[] getHashAsBytes() {
        if (hash == null) {
            return HashUtil.hashSHA256(getDataOrDownload());
        }
        return Base64.getDecoder().decode(hash);
    }
    
    public AbstractOverride setHash(String hash) {
        this.hash = hash;
        return this;
    }
    
    public AbstractOverride setHashFromBytes(byte[] bytes) {
        if (bytes == null) {
            this.hash = null;
        } else {
            this.hash = Base64.getEncoder().encodeToString(bytes);
        }
        return this;
    }
    
    public OverridePolicy getNonNullOverridePolicy(OverridePolicy overridePolicy) {
        return overridePolicy == null ? (getOverridePolicy() == null ? OverridePolicy.NONE : getOverridePolicy()) : overridePolicy;
    }
    
    public OverridePolicy getOverridePolicy() {
        return overridePolicy;
    }
    
    public AbstractOverride setOverridePolicy(OverridePolicy overridePolicy) {
        this.overridePolicy = overridePolicy;
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
    
    public byte[] downloadUrl() {
        if (url == null) {
            return null;
        }
        try {
            return IOUtils.toByteArray(toURL());
        } catch (Exception ex) {
            Logger.handleError(ex);
            return null;
        }
    }
    
    public AbstractOverride setUrl(String url) {
        this.url = url;
        return this;
    }
    
    public String getData() {
        return data;
    }
    
    public byte[] getDataAsBytes() {
        if (data == null) {
            return null;
        }
        return Base64.getDecoder().decode(data);
    }
    
    public byte[] getDataOrDownload() {
        if (temp == null) {
            temp = data == null ? downloadUrl() : getDataAsBytes();
        }
        return temp;
    }
    
    public AbstractOverride setData(String data) {
        this.data = data;
        return this;
    }
    
    public AbstractOverride setDataFromBytes(byte[] bytes) {
        if (bytes == null) {
            this.data = null;
        } else {
            this.data = Base64.getEncoder().encodeToString(bytes);
        }
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
        return "AbstractOverride{" + "hash='" + hash + '\'' + ", overridePolicy=" + overridePolicy + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", url='" + url + '\'' + ", data='" + data + '\'' + '}';
    }
    
}
