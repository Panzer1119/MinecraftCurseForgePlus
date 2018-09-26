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

import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.io.file.exceptions.isnot.FileIsNotFileRuntimeException;
import de.codemakers.mcfp.Main;
import de.codemakers.security.util.HashUtil;

public class ModOverride extends AbstractOverride {
    
    protected String file_2;
    
    public ModOverride(String hash, OverridePolicy overridePolicy, OverrideAction overrideAction, String file, String source, byte[] temp, String file_2) {
        super(hash, overridePolicy, overrideAction, file, source, temp);
        this.file_2 = file_2;
    }
    
    public String getFile_2() {
        return file_2;
    }
    
    public ModOverride setFile_2(String file_2) {
        this.file_2 = file_2;
        return this;
    }
    
    @Override
    public OverrideType getOverrideType() {
        return OverrideType.MOD;
    }
    
    @Override
    boolean performOverrideIntern(OverridePolicy overridePolicy) throws Exception {
        overridePolicy = getNonNullOverridePolicy(overridePolicy);
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        boolean needsData = false;
        if (isOverrideAction(OverrideAction.REPLACE) || isOverrideAction(OverrideAction.ADD) || isOverrideAction(OverrideAction.CHANGE)) {
            needsData = true;
            if (source == null) {
                throw new IllegalArgumentException("source may not be null");
            }
            if (isOverrideAction(OverrideAction.REPLACE)) {
                if (file_2 == null) {
                    throw new IllegalArgumentException("file_2 may not be null");
                }
            }
        }
        byte[] data = needsData ? resolveSource() : null;
        final AdvancedFile advancedFile = file == null ? null : new AdvancedFile(Main.getMinecraftModsFolder(), file);
        final AdvancedFile advancedFile_2 = file_2 == null ? null : new AdvancedFile(Main.getMinecraftModsFolder(), file_2);
        AdvancedFile advancedFile_temp = null;
        if (advancedFile != null && advancedFile.exists() && !advancedFile.isFile()) {
            throw new FileIsNotFileRuntimeException(advancedFile.getAbsolutePath() + " has to be a file");
        }
        if (advancedFile_2 != null && advancedFile_2.exists() && !advancedFile_2.isFile()) {
            throw new FileIsNotFileRuntimeException(advancedFile_2.getAbsolutePath() + " has to be a file");
        }
        switch (getOverrideAction()) {
            case ADD:
                if (overridePolicy != OverridePolicy.FORCE && advancedFile.exists()) {
                    if (checkHash(advancedFile.readBytes())) {
                        return true;
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                if (advancedFile.writeBytes(data)) {
                    return checkHash(advancedFile.readBytes(), true);
                }
                break;
            case REMOVE:
                if (overridePolicy != OverridePolicy.FORCE && !advancedFile.exists()) {
                    return true;
                }
                return advancedFile.delete();
            case REPLACE:
                if (advancedFile.exists() && !advancedFile.delete()) {
                    return false;
                }
                if (overridePolicy != OverridePolicy.FORCE && advancedFile_2.exists()) {
                    if (checkHash(advancedFile_2.readBytes())) {
                        return true;
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                if (advancedFile_2.writeBytes(data)) {
                    return checkHash(advancedFile_2.readBytes(), true);
                }
                break;
            case ENABLE:
                AdvancedFile advancedFile_with_suffix_1 = null;
                AdvancedFile advancedFile_without_suffix_1 = null;
                if (advancedFile.getName().toLowerCase().endsWith(SUFFIX_DISABLED)) {
                    advancedFile_with_suffix_1 = advancedFile;
                    advancedFile_without_suffix_1 = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName().substring(0, advancedFile.getName().length() - SUFFIX_DISABLED.length()));
                } else {
                    advancedFile_without_suffix_1 = advancedFile;
                    advancedFile_with_suffix_1 = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName() + SUFFIX_DISABLED);
                }
                if (advancedFile_without_suffix_1.exists() && checkHash(advancedFile_without_suffix_1.readBytes())) {
                    return true;
                }
                if (!advancedFile_with_suffix_1.exists()) {
                    return false;
                }
                if (overridePolicy != OverridePolicy.FORCE && advancedFile_without_suffix_1.exists()) {
                    if (checkHash(advancedFile_without_suffix_1.readBytes())) {
                        return advancedFile_with_suffix_1.delete();
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                data = advancedFile_with_suffix_1.readBytes();
                if (advancedFile_without_suffix_1.writeBytes(data)) {
                    checkHash(advancedFile_without_suffix_1.readBytes(), HashUtil.hashSHA256(data), true);
                    return advancedFile_with_suffix_1.delete();
                } else {
                    return false;
                }
            case DISABLE:
                AdvancedFile advancedFile_with_suffix_2 = null;
                AdvancedFile advancedFile_without_suffix_2 = null;
                if (advancedFile.getName().toLowerCase().endsWith(SUFFIX_DISABLED)) {
                    advancedFile_with_suffix_2 = advancedFile;
                    advancedFile_without_suffix_2 = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName().substring(0, advancedFile.getName().length() - SUFFIX_DISABLED.length()));
                } else {
                    advancedFile_without_suffix_2 = advancedFile;
                    advancedFile_with_suffix_2 = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName() + SUFFIX_DISABLED);
                }
                /*if (advancedFile_with_suffix_2.exists()) {
                    //return false; //Nice try, but we have to ensure, that the file is really not existing
                }*/
                if (!advancedFile_without_suffix_2.exists()) {
                    return true;
                }
                if (overridePolicy != OverridePolicy.FORCE && advancedFile_with_suffix_2.exists()) {
                    if (checkHash(advancedFile_with_suffix_2.readBytes())) {
                        return advancedFile_without_suffix_2.delete();
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                //if (overridePolicy == OverridePolicy.FORCE || !advancedFile_temp.exists() || !checkHash(advancedFile_temp.readBytes())) {
                data = advancedFile_without_suffix_2.readBytes();
                if (advancedFile_with_suffix_2.writeBytes(data)) {
                    checkHash(advancedFile_with_suffix_2.readBytes(), HashUtil.hashSHA256(data), true);
                    return advancedFile_without_suffix_2.delete();
                } else {
                    return false;
                }
                //}
            case CHANGE:
                throw new NotYetImplementedRuntimeException("Maybe never?");
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException();
        }
        return false;
    }
    
    @Override
    byte[] getSourceFromFileIntern(String file) {
        return new AdvancedFile(Main.getMinecraftModsFolder(), file).readBytesWithoutException();
    }
    
    @Override
    public String toString() {
        return "ModOverride{" + "file_2='" + file_2 + '\'' + ", hash='" + hash + '\'' + ", overridePolicy=" + overridePolicy + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", source='" + source + '\'' + ", sourceType=" + sourceType + '}';
    }
    
}
