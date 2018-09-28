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

import com.google.gson.*;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.mcfp.Main;
import de.codemakers.mcfp.entities.FileOverride;
import de.codemakers.mcfp.entities.OverrideAction;
import de.codemakers.mcfp.entities.Overrides;
import de.codemakers.security.util.HashUtil;

import java.util.*;

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
    
    public static String generateOverridesJSON(AdvancedFile advancedFile_minecraft_original, AdvancedFile advancedFile_minecraft_modified) {
        final Gson gson = new GsonBuilder().create();
        final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
        final JsonSerializer<FileOverride> jsonSerializer = (src, typeOfSrc, context) -> {
            final JsonElement jsonElement = gson.toJsonTree(src, typeOfSrc);
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("source")) {
                jsonObject.addProperty("source", (src.getSourceType() == null ? null : (src.getSourceType().getType() + ":" + src.getSource())));
            }
            return jsonObject;
        };
        gsonBuilder.registerTypeAdapter(FileOverride.class, jsonSerializer);
        return gsonBuilder.create().toJson(generateOverrides(advancedFile_minecraft_original, advancedFile_minecraft_modified));
    }
    
    public static Overrides generateOverrides(AdvancedFile advancedFile_minecraft_original, AdvancedFile advancedFile_minecraft_modified) {
        System.out.println(advancedFile_minecraft_original);
        System.out.println(advancedFile_minecraft_modified);
        final Overrides overrides = new Overrides(null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        System.out.println(overrides);
        final Map<String, byte[]> hashes_original_mods = new HashMap<>();
        final Map<String, byte[]> hashes_modified_mods = new HashMap<>();
        final Map<String, byte[]> hashes_original_config = new HashMap<>();
        final Map<String, byte[]> hashes_modified_config = new HashMap<>();
        final Map<String, byte[]> hashes_original_scripts = new HashMap<>();
        final Map<String, byte[]> hashes_modified_scripts = new HashMap<>();
        final Map<String, byte[]> hashes_original_resources = new HashMap<>();
        final Map<String, byte[]> hashes_modified_resources = new HashMap<>();
        final AdvancedFile advancedFile_minecraft_original_mods = new AdvancedFile(advancedFile_minecraft_original, Main.MODS_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_modified_mods = new AdvancedFile(advancedFile_minecraft_modified, Main.MODS_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_original_config = new AdvancedFile(advancedFile_minecraft_original, Main.CONFIG_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_modified_config = new AdvancedFile(advancedFile_minecraft_modified, Main.CONFIG_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_original_scripts = new AdvancedFile(advancedFile_minecraft_original, Main.SCRIPTS_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_modified_scripts = new AdvancedFile(advancedFile_minecraft_modified, Main.SCRIPTS_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_original_resources = new AdvancedFile(advancedFile_minecraft_original, Main.RESOURCES_FOLDER_NAME);
        final AdvancedFile advancedFile_minecraft_modified_resources = new AdvancedFile(advancedFile_minecraft_modified, Main.RESOURCES_FOLDER_NAME);
        addHashesFromFolder(advancedFile_minecraft_original_mods, hashes_original_mods);
        //System.out.println(hashes_original_mods);
        addHashesFromFolder(advancedFile_minecraft_modified_mods, hashes_modified_mods);
        //System.out.println(hashes_modified_mods);
        addHashesFromFolder(advancedFile_minecraft_original_config, hashes_original_config);
        //System.out.println(hashes_original_config);
        addHashesFromFolder(advancedFile_minecraft_modified_config, hashes_modified_config);
        //System.out.println(hashes_modified_config);
        addHashesFromFolder(advancedFile_minecraft_original_scripts, hashes_original_scripts);
        //System.out.println(hashes_original_scripts);
        addHashesFromFolder(advancedFile_minecraft_modified_scripts, hashes_modified_scripts);
        //System.out.println(hashes_modified_scripts);
        addHashesFromFolder(advancedFile_minecraft_original_resources, hashes_original_resources);
        //System.out.println(hashes_original_resources);
        addHashesFromFolder(advancedFile_minecraft_modified_resources, hashes_modified_resources);
        //System.out.println(hashes_modified_resources);
        // Mods
        for (String original : hashes_original_mods.keySet()) {
            final byte[] hash_original = hashes_original_mods.get(original);
            final byte[] hash_modified = hashes_modified_mods.get(original);
            //System.out.println(String.format("Original \"%s\": hash_original=%s, hash_modified=%s", original, Arrays.toString(hash_original), Arrays.toString(hash_modified)));
            if (hash_modified == null) {
                overrides.getModOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Arrays.equals(hash_original, hash_modified)) {
                overrides.getModOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.CHANGE, original, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_mods, original).readBytesWithoutException()), null, null));
            }
        }
        for (String modified : hashes_modified_mods.keySet()) {
            final byte[] hash_modified = hashes_modified_mods.get(modified);
            final byte[] hash_original = hashes_original_mods.get(modified);
            //System.out.println(String.format("Modified \"%s\": hash_modified=%s, hash_original=%s", modified, Arrays.toString(hash_modified), Arrays.toString(hash_original)));
            if (hash_original == null) {
                overrides.getModOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.ADD, modified, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_mods, modified).readBytesWithoutException()), null, null));
            }
        }
        // Config
        for (String original : hashes_original_config.keySet()) {
            final byte[] hash_original = hashes_original_config.get(original);
            final byte[] hash_modified = hashes_modified_config.get(original);
            if (hash_modified == null) {
                overrides.getConfigOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Arrays.equals(hash_original, hash_modified)) {
                overrides.getConfigOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.CHANGE, original, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_config, original).readBytesWithoutException()), null, null));
            }
        }
        for (String modified : hashes_modified_config.keySet()) {
            final byte[] hash_modified = hashes_modified_config.get(modified);
            final byte[] hash_original = hashes_original_config.get(modified);
            if (hash_original == null) {
                overrides.getConfigOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.ADD, modified, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_config, modified).readBytesWithoutException()), null, null));
            }
        }
        // Scripts
        for (String original : hashes_original_scripts.keySet()) {
            final byte[] hash_original = hashes_original_scripts.get(original);
            final byte[] hash_modified = hashes_modified_scripts.get(original);
            if (hash_modified == null) {
                overrides.getScriptOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Arrays.equals(hash_original, hash_modified)) {
                overrides.getScriptOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.CHANGE, original, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_scripts, original).readBytesWithoutException()), null, null));
            }
        }
        for (String modified : hashes_modified_scripts.keySet()) {
            final byte[] hash_modified = hashes_modified_scripts.get(modified);
            final byte[] hash_original = hashes_original_scripts.get(modified);
            if (hash_original == null) {
                overrides.getScriptOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.ADD, modified, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_scripts, modified).readBytesWithoutException()), null, null));
            }
        }
        // Resources
        for (String original : hashes_original_resources.keySet()) {
            final byte[] hash_original = hashes_original_resources.get(original);
            final byte[] hash_modified = hashes_modified_resources.get(original);
            if (hash_modified == null) {
                overrides.getResourceOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Arrays.equals(hash_original, hash_modified)) {
                overrides.getResourceOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.CHANGE, original, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_resources, original).readBytesWithoutException()), null, null));
            }
        }
        for (String modified : hashes_modified_resources.keySet()) {
            final byte[] hash_modified = hashes_modified_resources.get(modified);
            final byte[] hash_original = hashes_original_resources.get(modified);
            if (hash_original == null) {
                overrides.getResourceOverrides().add(new FileOverride(hash_modified == null ? null : Base64.getEncoder().encodeToString(hash_modified), null, OverrideAction.ADD, modified, "data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_resources, modified).readBytesWithoutException()), null, null));
            }
        }
        return overrides;
    }
    
    static void addHashesFromFolder(AdvancedFile advancedFile_folder, Map<String, byte[]> hashes) {
        if (advancedFile_folder == null || !advancedFile_folder.exists() || !advancedFile_folder.isDirectory()) {
            return;
        }
        advancedFile_folder.listFiles(true).stream().filter(AdvancedFile::isFile).filter((advancedFile) -> !advancedFile.getName().equals(".DS_Store")).forEach((advancedFile) -> hashes.put(advancedFile.getPath().substring(advancedFile_folder.getPath().length() + 1, advancedFile.getPath().length()), HashUtil.hashSHA256(advancedFile.readBytesWithoutException())));
    }
    
}
