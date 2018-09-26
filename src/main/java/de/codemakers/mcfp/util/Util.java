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

package de.codemakers.mcfp.util;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.security.util.HashUtil;

import java.util.Arrays;
import java.util.Comparator;

public class Util {
    
    public static byte[] hashAdvancedFiles(AdvancedFile... advancedFiles) {
        if (advancedFiles.length == 0) {
            return null;
        }
        final byte[] hash = new byte[32];
        for (AdvancedFile advancedFile : advancedFiles) {
            hashAdvancedFile(advancedFile, hash);
        }
        return hash;
    }
    
    static void hashAdvancedFile(AdvancedFile advancedFile, byte[] hash) {
        if (advancedFile != null && advancedFile.exists() && hash != null) {
            if (advancedFile.isDirectory()) {
                advancedFile.listFiles(false).stream().sorted(Comparator.comparing(AdvancedFile::getPath)).forEach((advancedFile_) -> hashAdvancedFile(advancedFile_, hash));
            } else {
                byte[] hash_temp = HashUtil.hashSHA256(advancedFile.readBytesWithoutException());
                if (hash_temp != null) {
                    hash_temp = Arrays.copyOf(hash_temp, hash_temp.length + hash.length);
                    System.arraycopy(hash, 0, hash_temp, hash_temp.length - hash.length, hash.length);
                    System.arraycopy(HashUtil.hashSHA256(hash_temp), 0, hash, 0, hash.length);
                }
            }
        }
    }
    
}
