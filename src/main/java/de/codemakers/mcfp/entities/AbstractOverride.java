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

import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.security.util.HashUtil;
import org.apache.commons.io.IOUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractOverride {
    
    public static final String SUFFIX_DISABLED = ".disabled";
    public static final String SOURCE_REGEX_STRING = "(" + SourceType.URL.getType() + "|" + SourceType.FILE.getType() + "|" + SourceType.DATA.getType() + "):(.+)";
    public static final Pattern SOURCE_REGEX_PATTERN = Pattern.compile(SOURCE_REGEX_STRING);
    
    protected String hash;
    protected OverridePolicy overridePolicy;
    protected OverrideAction overrideAction;
    protected String file;
    protected String source = null;
    protected transient SourceType sourceType = null;
    //temp
    protected transient byte[] temp = null;
    
    public AbstractOverride(String hash, OverridePolicy overridePolicy, OverrideAction overrideAction, String file, String source, byte[] temp) {
        this.hash = hash;
        this.overridePolicy = overridePolicy;
        this.overrideAction = overrideAction;
        this.file = file;
        this.source = source;
        this.temp = temp;
        init();
    }
    
    public void init() {
        this.temp = null;
        if (sourceType == null) {
            if (source == null) {
                sourceType = null;
                return;
            }
            final Matcher matcher = SOURCE_REGEX_PATTERN.matcher(source);
            if (matcher.matches()) {
                sourceType = SourceType.ofType(matcher.group(1));
                source = matcher.group(2);
            } else {
                Logger.log(String.format("Found no %s for \"%s\"", SourceType.class.getSimpleName(), source), LogLevel.WARNING);
                sourceType = SourceType.UNKNOWN;
            }
        }
    }
    
    public abstract OverrideType getOverrideType();
    
    abstract boolean performOverrideIntern(OverridePolicy overridePolicy) throws Exception;
    
    public boolean performOverride(OverridePolicy overridePolicy) throws Exception {
        Objects.requireNonNull(overrideAction, "overrideAction may not be null");
        Objects.requireNonNull(file, "file may not be null");
        init();
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
            return HashUtil.hashSHA256(resolveSource());
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
    
    public final String getSource() {
        return source;
    }
    
    public final AbstractOverride setSource(String source) {
        this.source = source;
        this.sourceType = null;
        this.temp = null;
        init();
        return this;
    }
    
    public AbstractOverride setURLAsSource(String url) {
        this.source = url;
        this.sourceType = SourceType.URL;
        this.temp = null;
        return this;
    }
    
    public AbstractOverride setDataAsSource(byte[] bytes) {
        if (bytes == null) {
            this.source = null;
        } else {
            this.source = Base64.getEncoder().encodeToString(bytes);
        }
        this.sourceType = SourceType.DATA;
        this.temp = null;
        return this;
    }
    
    public AbstractOverride setFileAsSource(String file) {
        this.source = file;
        this.sourceType = SourceType.FILE;
        this.temp = null;
        return this;
    }
    
    public URL sourceToURL() throws MalformedURLException {
        if (source == null || sourceType != SourceType.URL) {
            return null;
        }
        return new URL(source);
    }
    
    public byte[] getSourceFromURL() {
        if (source == null || sourceType != SourceType.URL) {
            return null;
        }
        try {
            return IOUtils.toByteArray(sourceToURL());
        } catch (Exception ex) {
            Logger.handleError(ex);
            return null;
        }
    }
    
    public byte[] getSourceFromData() {
        if (source == null || sourceType != SourceType.DATA) {
            return null;
        }
        return Base64.getDecoder().decode(source);
    }
    
    abstract byte[] getSourceFromFileIntern(String file);
    
    public byte[] getSourceFromFile() {
        if (source == null || sourceType != SourceType.FILE) {
            return null;
        }
        return getSourceFromFileIntern(source);
    }
    
    public byte[] resolveSource() {
        if (temp == null) {
            if (source == null) {
                return null;
            }
            switch (sourceType) {
                case URL:
                    temp = getSourceFromURL();
                    break;
                case FILE:
                    temp = getSourceFromFile();
                    break;
                case DATA:
                    temp = getSourceFromData();
                    break;
                case UNKNOWN:
                    return null;
            }
        }
        return temp;
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
        return "AbstractOverride{" + "hash='" + hash + '\'' + ", overridePolicy=" + overridePolicy + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", source='" + source + '\'' + ", sourceType=" + sourceType + '}';
    }
    
}
