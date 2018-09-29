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

import com.google.gson.GsonBuilder;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.util.tough.ToughConsumer;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.mcfp.Main;
import de.codemakers.mcfp.util.Util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Overrides {
    
    protected String hash;
    protected List<FileOverride> modOverrides;
    protected List<FileOverride> configOverrides;
    protected List<FileOverride> scriptOverrides;
    protected List<FileOverride> resourceOverrides;
    protected List<FileOverride> customOverrides;
    protected Map<String, String> options;
    //temp
    protected transient List<AbstractOverride> overrides = null;
    
    public Overrides(String hash, List<FileOverride> modOverrides, List<FileOverride> configOverrides, List<FileOverride> scriptOverrides, List<FileOverride> resourceOverrides, List<FileOverride> customOverrides) {
        this.hash = hash;
        this.modOverrides = modOverrides;
        this.configOverrides = configOverrides;
        this.scriptOverrides = scriptOverrides;
        this.resourceOverrides = resourceOverrides;
        this.customOverrides = customOverrides;
        init();
    }
    
    public Overrides init() {
        if (modOverrides != null && !modOverrides.isEmpty()) {
            modOverrides.forEach((fileOverride) -> {
                fileOverride.setOverrideType(OverrideType.MOD);
                fileOverride.overrides = this;
            });
        }
        if (configOverrides != null && !configOverrides.isEmpty()) {
            configOverrides.forEach((fileOverride) -> {
                fileOverride.setOverrideType(OverrideType.CONFIG);
                fileOverride.overrides = this;
            });
        }
        if (scriptOverrides != null && !scriptOverrides.isEmpty()) {
            scriptOverrides.forEach((fileOverride) -> {
                fileOverride.setOverrideType(OverrideType.SCRIPT);
                fileOverride.overrides = this;
            });
        }
        if (resourceOverrides != null && !resourceOverrides.isEmpty()) {
            resourceOverrides.forEach((fileOverride) -> {
                fileOverride.setOverrideType(OverrideType.RESOURCE);
                fileOverride.overrides = this;
            });
        }
        if (customOverrides != null && !customOverrides.isEmpty()) {
            customOverrides.forEach((fileOverride) -> {
                fileOverride.setOverrideType(OverrideType.CUSTOM);
                fileOverride.overrides = this;
            });
        }
        if (options == null) {
            options = new HashMap<>();
        } else {
            options.keySet().forEach((key) -> options.put(key, AbstractOverride.replaceOptions(this, options.get(key), options.get(key))));
        }
        return this;
    }
    
    public String getHash() {
        return hash;
    }
    
    public byte[] getHashAsBytes() {
        if (hash == null) {
            return null;
        }
        return Base64.getDecoder().decode(hash);
    }
    
    public Overrides setHash(String hash) {
        this.hash = hash;
        return this;
    }
    
    public Overrides setHashFromBytes(byte[] bytes) {
        if (bytes == null) {
            this.hash = null;
        } else {
            this.hash = Base64.getEncoder().encodeToString(bytes);
        }
        return this;
    }
    
    public List<FileOverride> getModOverrides() {
        this.overrides = null;
        return modOverrides;
    }
    
    public Overrides setModOverrides(List<FileOverride> modOverrides) {
        this.modOverrides = modOverrides;
        this.overrides = null;
        return this;
    }
    
    public List<FileOverride> getConfigOverrides() {
        this.overrides = null;
        return configOverrides;
    }
    
    public Overrides setConfigOverrides(List<FileOverride> configOverrides) {
        this.configOverrides = configOverrides;
        this.overrides = null;
        return this;
    }
    
    public List<FileOverride> getScriptOverrides() {
        this.overrides = null;
        return scriptOverrides;
    }
    
    public Overrides setScriptOverrides(List<FileOverride> scriptOverrides) {
        this.scriptOverrides = scriptOverrides;
        this.overrides = null;
        return this;
    }
    
    public List<FileOverride> getResourceOverrides() {
        this.overrides = null;
        return resourceOverrides;
    }
    
    public Overrides setResourceOverrides(List<FileOverride> resourceOverrides) {
        this.resourceOverrides = resourceOverrides;
        this.overrides = null;
        return this;
    }
    
    public List<FileOverride> getCustomOverrides() {
        this.overrides = null;
        return customOverrides;
    }
    
    public Overrides setCustomOverrides(List<FileOverride> customOverrides) {
        this.customOverrides = customOverrides;
        this.overrides = null;
        return this;
    }
    
    public Map<String, String> getOptions() {
        return options;
    }
    
    public String getValue(String key) {
        return options.get(key);
    }
    
    public String getValueOrDefault(String key, String defaultValue) {
        final String value = getValue(key);
        return value == null ? defaultValue : value;
    }
    
    public List<AbstractOverride> getOverrides() {
        if (overrides == null) {
            final List<AbstractOverride> temp = new ArrayList<>();
            if (getModOverrides() != null) {
                temp.addAll(getModOverrides());
            }
            if (getConfigOverrides() != null) {
                temp.addAll(getConfigOverrides());
            }
            if (getScriptOverrides() != null) {
                temp.addAll(getScriptOverrides());
            }
            if (getResourceOverrides() != null) {
                temp.addAll(getResourceOverrides());
            }
            if (getCustomOverrides() != null) {
                temp.addAll(getCustomOverrides());
            }
            overrides = temp;
        }
        return overrides;
    }
    
    public boolean performOverrides(OverridePolicy overridePolicy, boolean returnOnFailedOverrides, ToughConsumer<AbstractOverride> successfulOverride) throws Exception {
        final List<AbstractOverride> overrides = getOverrides();
        for (AbstractOverride abstractOverride : overrides.stream().filter((override) -> override.getOverrideAction() == OverrideAction.COPY).collect(Collectors.toList())) {
            if (!abstractOverride.performOverride(overridePolicy)) {
                if (returnOnFailedOverrides) {
                    return false;
                }
            } else if (successfulOverride != null) {
                successfulOverride.acceptWithoutException(abstractOverride);
            }
        }
        for (AbstractOverride abstractOverride : overrides.stream().filter((override) -> override.getOverrideAction() != OverrideAction.COPY).collect(Collectors.toList())) {
            if (!abstractOverride.performOverride(overridePolicy)) {
                if (returnOnFailedOverrides) {
                    return false;
                }
            } else if (successfulOverride != null) {
                successfulOverride.acceptWithoutException(abstractOverride);
            }
        }
        return checkHash(true);
    }
    
    public boolean performOverrides(OverridePolicy overridePolicy, boolean returnOnFailedOverrides, ToughConsumer<AbstractOverride> successfulOverride, ToughConsumer<Throwable> failure) {
        try {
            return performOverrides(overridePolicy, returnOnFailedOverrides, successfulOverride);
        } catch (Exception ex) {
            if (failure != null) {
                failure.acceptWithoutException(ex);
            } else {
                Logger.handleError(ex);
            }
            return false;
        }
    }
    
    public boolean performOverridesWithoutException(OverridePolicy overridePolicy, boolean returnOnFailedOverrides, ToughConsumer<AbstractOverride> successfulOverride) {
        return performOverrides(overridePolicy, returnOnFailedOverrides, successfulOverride, null);
    }
    
    public boolean checkHash(boolean throwException) {
        if (getHashAsBytes() != null && !Arrays.equals(getHashAsBytes(), Util.hashAdvancedFiles(Main.getMinecraftModsFolder(), Main.getMinecraftConfigFolder(), Main.getMinecraftScriptsFolder(), Main.getMinecraftResourcesFolder()))) {
            if (throwException) {
                throw new IllegalArgumentException("the hash of the folder does not match the overall hash");
            }
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Overrides{" + "hash='" + hash + '\'' + ", modOverrides=" + modOverrides + ", configOverrides=" + configOverrides + ", scriptOverrides=" + scriptOverrides + ", resourceOverrides=" + resourceOverrides + ", customOverrides=" + customOverrides + ", options=" + options + ", overrides=" + overrides + '}';
    }
    
    public static Overrides fromJSON(String json) {
        Objects.requireNonNull(json);
        return new GsonBuilder().create().fromJson(json, Overrides.class).init();
    }
    
    public static Overrides fromAdvancedFile(AdvancedFile advancedFile) {
        Objects.requireNonNull(advancedFile);
        return new GsonBuilder().create().fromJson(new String(advancedFile.readBytesWithoutException()), Overrides.class).init();
    }
    
    public static Overrides fromInputStream(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        return new GsonBuilder().create().fromJson(new InputStreamReader(inputStream), Overrides.class).init();
    }
    
}
