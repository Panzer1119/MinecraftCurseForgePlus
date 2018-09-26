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

public class ModOverride extends AbstractOverride {
    
    protected String file_2;
    
    public ModOverride(String hash, OverrideAction overrideAction, String file, String url, String data, String file_2) {
        super(hash, overrideAction, file, url, data);
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
    public boolean performOverride() throws Exception {
        if (url != null && data != null) {
            throw new IllegalArgumentException("url OR data is needed, not both");
        }
        if (file == null) {
            throw new IllegalArgumentException("file may not be null");
        }
        boolean needsData = false;
        if (isOverrideAction(OverrideAction.REPLACE) || isOverrideAction(OverrideAction.ADD) || isOverrideAction(OverrideAction.CHANGE)) {
            needsData = true;
            if (url == null && data == null) {
                throw new IllegalArgumentException("url or data may not be null");
            }
            if (isOverrideAction(OverrideAction.REPLACE)) {
                if (file_2 == null) {
                    throw new IllegalArgumentException("file_2 may not be null");
                }
            }
        }
        byte[] data = needsData ? getDataOrDownload() : null;
        final AdvancedFile advancedFile = file == null ? null : new AdvancedFile(Main.getMinecraftModsFolder(), file);
        final AdvancedFile advancedFile_2 = file_2 == null ? null : new AdvancedFile(Main.getMinecraftModsFolder(), file_2);
        AdvancedFile advancedFile_temp = null;
        if (advancedFile != null && !advancedFile.isFile()) {
            throw new FileIsNotFileRuntimeException("file must be a file");
        }
        if (advancedFile_2 != null && !advancedFile_2.isFile()) {
            throw new FileIsNotFileRuntimeException("file_2 must be a file");
        }
        switch (getOverrideAction()) {
            case ADD:
                if (advancedFile.exists() && checkHash(advancedFile.readBytes())) {
                    return true;
                }
                if (advancedFile.writeBytes(data)) {
                    return checkHash(advancedFile.readBytes(), true);
                }
                break;
            case REMOVE:
                if (!advancedFile.exists()) {
                    return true;
                }
                return advancedFile.delete();
            case REPLACE:
                if (advancedFile.exists() && !advancedFile.delete()) {
                    return false;
                }
                if (advancedFile_2.exists() && checkHash(advancedFile_2.readBytes())) {
                    return true;
                }
                if (advancedFile_2.writeBytes(data)) {
                    return checkHash(advancedFile_2.readBytes(), true);
                }
                break;
            case ENABLE:
                if (!advancedFile.exists()) {
                    return false;
                }
                if (advancedFile.getName().toLowerCase().endsWith(SUFFIX_DISABLED)) {
                    advancedFile_temp = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName().substring(0, advancedFile.getName().length() - SUFFIX_DISABLED.length()));
                    if (!advancedFile_temp.exists() || !checkHash(advancedFile_temp.readBytes())) {
                        data = advancedFile.readBytes();
                        if (advancedFile_temp.writeBytes(data)) {
                            return checkHash(advancedFile_temp.readBytes(), true);
                        } else {
                            return false;
                        }
                    }
                    return advancedFile.delete();
                }
                return checkHash(advancedFile.readBytes(), true);
            case DISABLE:
                if (!advancedFile.exists()) {
                    return true;
                }
                advancedFile_temp = new AdvancedFile(Main.getMinecraftModsFolder(), advancedFile.getName() + SUFFIX_DISABLED);
                if (!advancedFile_temp.exists() || !checkHash(advancedFile_temp.readBytes())) {
                    data = advancedFile.readBytes();
                    if (advancedFile_temp.writeBytes(data)) {
                        return checkHash(advancedFile_temp.readBytes(), true);
                    } else {
                        return false;
                    }
                }
                return advancedFile.delete();
            case CHANGE:
                throw new NotYetImplementedRuntimeException("Maybe never?");
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException();
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ModOverride{" + "file_2='" + file_2 + '\'' + ", hash='" + hash + '\'' + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", url='" + url + '\'' + ", data='" + data + '\'' + '}';
    }
    
}
