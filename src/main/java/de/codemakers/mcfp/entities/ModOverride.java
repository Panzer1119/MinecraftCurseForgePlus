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

public class ModOverride extends AbstractOverride {
    
    protected String file_2;
    
    public ModOverride(String hash, OverrideAction overrideAction, String file, String url, String data, String file_2) {
        super(hash, overrideAction, file, url, data);
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
    public boolean performOverride() throws Exception {
        if (url != null && data != null) {
            throw new IllegalArgumentException("url OR data is needed, not both");
        }
        if (isOverrideAction(OverrideAction.REPLACE)) {
            if (file == null) {
                throw new IllegalArgumentException("file may not be null");
            } else if (file_2 == null) {
                throw new IllegalArgumentException("file_2 may not be null");
            } else if (url == null && data == null) {
                throw new IllegalArgumentException("url or data may not be null");
            }
            
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "ModOverride{" + "file_2='" + file_2 + '\'' + ", hash='" + hash + '\'' + ", overrideAction=" + overrideAction + ", file='" + file + '\'' + ", url='" + url + '\'' + ", data='" + data + '\'' + '}';
    }
    
}
