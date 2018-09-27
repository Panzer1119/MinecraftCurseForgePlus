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
import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.io.file.exceptions.isnot.FileIsNotFileRuntimeException;
import de.codemakers.mcfp.Main;
import de.codemakers.mcfp.util.Log;
import de.codemakers.security.util.HashUtil;

import java.util.Base64;

public class FileOverride extends AbstractOverride {
    
    public static boolean SHOW_DATA_IN_BASE64_BEFORE_REMOVING_MODS = false;
    
    protected transient OverrideType overrideType = null;
    protected String file_2;
    
    public FileOverride(String hash, OverridePolicy overridePolicy, OverrideAction overrideAction, String file, String source, byte[] temp, String file_2) {
        super(hash, overridePolicy, overrideAction, file, source, temp);
        this.file_2 = file_2;
    }
    
    public String getFile_2() {
        return file_2;
    }
    
    public FileOverride setFile_2(String file_2) {
        this.file_2 = file_2;
        return this;
    }
    
    @Override
    public OverrideType getOverrideType() {
        return overrideType;
    }
    
    public FileOverride setOverrideType(OverrideType overrideType) {
        this.overrideType = overrideType;
        return this;
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
        final byte[] data = needsData ? resolveSource() : null;
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
                Log.addActionIfEnabled(advancedFile, () -> (advancedFile.exists() ? advancedFile.readBytes() : null), () -> data);
                if (advancedFile.writeBytes(data)) {
                    return checkHash(advancedFile.readBytes(), true);
                }
                break;
            case REMOVE:
                if (overridePolicy != OverridePolicy.FORCE && !advancedFile.exists()) {
                    return true;
                }
                if (SHOW_DATA_IN_BASE64_BEFORE_REMOVING_MODS && advancedFile.exists()) {
                    Logger.log(String.format("%s is removing \"%s\", this is the content of the file encoded with base64: %s", this, advancedFile.getAbsolutePath(), Base64.getEncoder().encodeToString(advancedFile.readBytesWithoutException())), LogLevel.FINE);
                }
                Log.addActionIfEnabled(advancedFile, () -> (advancedFile.exists() ? advancedFile.readBytes() : null), () -> null);
                return advancedFile.delete();
            case REPLACE:
                Log.addActionIfEnabled(advancedFile, () -> (advancedFile.exists() ? advancedFile.readBytes() : null), () -> null);
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
                Log.addActionIfEnabled(advancedFile_2, () -> (advancedFile_2.exists() ? advancedFile_2.readBytes() : null), () -> data);
                if (advancedFile_2.writeBytes(data)) {
                    return checkHash(advancedFile_2.readBytes(), true);
                }
                break;
            case ENABLE:
                final boolean endsWith_1 = advancedFile.getName().toLowerCase().endsWith(SUFFIX_DISABLED);
                final AdvancedFile advancedFile_with_suffix_1 = endsWith_1 ? advancedFile : new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName() + SUFFIX_DISABLED);
                final AdvancedFile advancedFile_without_suffix_1 = endsWith_1 ? new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName().substring(0, advancedFile.getName().length() - SUFFIX_DISABLED.length())) : advancedFile;
                if (advancedFile_without_suffix_1.exists() && checkHash(advancedFile_without_suffix_1.readBytes())) {
                    return true;
                }
                if (!advancedFile_with_suffix_1.exists()) {
                    return false;
                }
                if (overridePolicy != OverridePolicy.FORCE && advancedFile_without_suffix_1.exists()) {
                    if (checkHash(advancedFile_without_suffix_1.readBytes())) {
                        Log.addActionIfEnabled(advancedFile_with_suffix_1, () -> (advancedFile_with_suffix_1.exists() ? advancedFile_with_suffix_1.readBytes() : null), () -> null);
                        return advancedFile_with_suffix_1.delete();
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                final byte[] data_1 = advancedFile_with_suffix_1.readBytes();
                Log.addActionIfEnabled(advancedFile_without_suffix_1, () -> (advancedFile_without_suffix_1.exists() ? advancedFile_without_suffix_1.readBytes() : null), () -> data_1);
                if (advancedFile_without_suffix_1.writeBytes(data_1)) {
                    checkHash(advancedFile_without_suffix_1.readBytes(), HashUtil.hashSHA256(data_1), true);
                    Log.addActionIfEnabled(advancedFile_with_suffix_1, () -> (advancedFile_with_suffix_1.exists() ? advancedFile_with_suffix_1.readBytes() : null), () -> null);
                    return advancedFile_with_suffix_1.delete();
                } else {
                    return false;
                }
            case DISABLE:
                final boolean endsWith_2 = advancedFile.getName().toLowerCase().endsWith(SUFFIX_DISABLED);
                final AdvancedFile advancedFile_with_suffix_2 = endsWith_2 ? advancedFile : new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName() + SUFFIX_DISABLED);
                final AdvancedFile advancedFile_without_suffix_2 = endsWith_2 ? new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName().substring(0, advancedFile.getName().length() - SUFFIX_DISABLED.length())) : advancedFile;
                if (!advancedFile_without_suffix_2.exists()) {
                    return true;
                }
                if (overridePolicy != OverridePolicy.FORCE && advancedFile_with_suffix_2.exists()) {
                    if (checkHash(advancedFile_with_suffix_2.readBytes())) {
                        Log.addActionIfEnabled(advancedFile_without_suffix_2, () -> (advancedFile_without_suffix_2.exists() ? advancedFile_without_suffix_2.readBytes() : null), () -> null);
                        return advancedFile_without_suffix_2.delete();
                    } else if (overridePolicy != OverridePolicy.ALLOW) {
                        return false;
                    }
                }
                final byte[] data_2 = advancedFile_without_suffix_2.readBytes();
                Log.addActionIfEnabled(advancedFile_with_suffix_2, () -> (advancedFile_with_suffix_2.exists() ? advancedFile_with_suffix_2.readBytes() : null), () -> data_2);
                if (advancedFile_with_suffix_2.writeBytes(data_2)) {
                    checkHash(advancedFile_with_suffix_2.readBytes(), HashUtil.hashSHA256(data_2), true);
                    Log.addActionIfEnabled(advancedFile_without_suffix_2, () -> (advancedFile_without_suffix_2.exists() ? advancedFile_without_suffix_2.readBytes() : null), () -> null);
                    return advancedFile_without_suffix_2.delete();
                } else {
                    return false;
                }
            case CHANGE: //TODO Use the IncrementalData etc, and the change can be saved as the byte array representation of the DeltaData
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
        return "FileOverride{" + "overrideType=" + overrideType + ", file_2='" + file_2 + '\'' + ", hash='" + hash + '\'' + ", overridePolicy=" + overridePolicy + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", source='" + source + '\'' + ", sourceType=" + sourceType + '}';
    }
    
}