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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Overrides {
    
    protected String hash;
    protected List<ModOverride> modOverrides;
    //temp
    protected transient List<AbstractOverride> overrides = null;
    
    public Overrides(String hash, List<ModOverride> modOverrides) {
        this.hash = hash;
        this.modOverrides = modOverrides;
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
    
    public List<ModOverride> getModOverrides() {
        this.overrides = null;
        return modOverrides;
    }
    
    public Overrides setModOverrides(List<ModOverride> modOverrides) {
        this.modOverrides = modOverrides;
        this.overrides = null;
        return this;
    }
    
    public List<AbstractOverride> getOverrides() {
        if (overrides == null) {
            overrides = new ArrayList<>();
            overrides.addAll(getModOverrides());
            //TODO Add the config, scripts and resource overrides
        }
        return overrides;
    }
    
    @Override
    public String toString() {
        return "Overrides{" + "hash='" + hash + '\'' + ", modOverrides=" + modOverrides + '}';
    }
    
}
