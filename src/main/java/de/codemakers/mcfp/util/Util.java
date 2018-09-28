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
import de.codemakers.base.os.OSUtil;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.mcfp.Main;
import de.codemakers.mcfp.entities.FileOverride;
import de.codemakers.mcfp.entities.OverrideAction;
import de.codemakers.mcfp.entities.Overrides;
import de.codemakers.security.util.HashUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    
    public static final byte[] HASH_EMPTY = HashUtil.hashSHA256("".getBytes());
    public static final String HASH_EMPTY_STRING = Base64.getEncoder().encodeToString(HASH_EMPTY);
    public static final byte[] HASH_WHITESPACE = HashUtil.hashSHA256(" ".getBytes());
    public static final String HASH_WHITESPACE_STRING = Base64.getEncoder().encodeToString(HASH_WHITESPACE);
    public static final byte[] HASH_TAB = HashUtil.hashSHA256("\t".getBytes());
    public static final String HASH_TAB_STRING = Base64.getEncoder().encodeToString(HASH_TAB);
    public static final byte[] HASH_NEW_LINE = HashUtil.hashSHA256("\n".getBytes());
    public static final String HASH_NEW_LINE_STRING = Base64.getEncoder().encodeToString(HASH_NEW_LINE);
    public static final byte[] HASH_LOCAL_NEW_LINE = HashUtil.hashSHA256(OSUtil.CURRENT_OS_HELPER.getLineSeparator().getBytes());
    public static final String HASH_LOCAL_NEW_LINE_STRING = Base64.getEncoder().encodeToString(HASH_LOCAL_NEW_LINE);
    
    public static boolean isHashOk(byte[] hash) {
        return hash != HASH_EMPTY && hash != HASH_WHITESPACE && hash != HASH_TAB && hash != HASH_NEW_LINE && hash != HASH_LOCAL_NEW_LINE;
    }
    
    public static boolean isHashOk(String hash) {
        return !Objects.equals(hash, HASH_EMPTY_STRING) && !Objects.equals(hash, HASH_WHITESPACE_STRING) && !Objects.equals(hash, HASH_TAB_STRING) && !Objects.equals(hash, HASH_NEW_LINE_STRING) && !Objects.equals(hash, HASH_LOCAL_NEW_LINE_STRING);
    }
    
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
    
    public static String generateOverridesJSON(AdvancedFile advancedFile_minecraft_original, AdvancedFile advancedFile_minecraft_modified, boolean addHashes, boolean includeData) {
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
        return gsonBuilder.create().toJson(generateOverrides(advancedFile_minecraft_original, advancedFile_minecraft_modified, addHashes, includeData));
    }
    
    public static Overrides generateOverrides(AdvancedFile advancedFile_minecraft_original, AdvancedFile advancedFile_minecraft_modified, boolean addHashes, boolean includeData) {
        System.out.println(advancedFile_minecraft_original);
        System.out.println(advancedFile_minecraft_modified);
        final Overrides overrides = new Overrides(null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        System.out.println(overrides);
        final Map<String, String> hashes_original_mods = new HashMap<>();
        final Map<String, String> hashes_modified_mods = new HashMap<>();
        final Map<String, String> hashes_original_config = new HashMap<>();
        final Map<String, String> hashes_modified_config = new HashMap<>();
        final Map<String, String> hashes_original_scripts = new HashMap<>();
        final Map<String, String> hashes_modified_scripts = new HashMap<>();
        final Map<String, String> hashes_original_resources = new HashMap<>();
        final Map<String, String> hashes_modified_resources = new HashMap<>();
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
        final Set<String> used_original = new HashSet<>();
        final Set<String> used_modified = new HashSet<>();
        // ===========================================================================================
        // ===========================================================================================
        // Mods
        // ===========================================================================================
        // ===========================================================================================
        //TODO Add recognition of OverrideAction.REPLACE of mods (new version etc.)
        for (String original : hashes_original_mods.keySet().stream().sorted().collect(Collectors.toList())) {
            if (used_original.contains(original) || hashes_modified_mods.containsKey(original)) {
                continue;
            }
            final String hash_original = hashes_original_mods.get(original);
            if (!isHashOk(hash_original)) {
                continue;
            }
            boolean renamed = false;
            for (String modified : hashes_modified_mods.keySet().stream().sorted().collect(Collectors.toList())) {
                if (used_modified.contains(modified)) {
                    continue;
                }
                final String hash_modified = hashes_modified_mods.get(modified);
                if (Objects.equals(hash_original, hash_modified) && !Objects.equals(original, modified)) {
                    if (renamed) {
                        overrides.getModOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.COPY, modified, null, null, original));
                    } else {
                        renamed = true;
                        overrides.getModOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.RENAME, modified, null, null, original));
                    }
                    used_modified.add(modified);
                }
            }
            if (renamed) {
                used_original.add(original);
            }
        }
        //
        for (String modified : hashes_modified_mods.keySet()) {
            final int path_count = StringUtils.countMatches(modified, OSUtil.CURRENT_OS_HELPER.getFileSeparator());
            String lowest_difference = modified;
            String lowest_difference_name = modified;
            for (String original : hashes_original_mods.keySet()) {
                if (path_count != StringUtils.countMatches(original, OSUtil.CURRENT_OS_HELPER.getFileSeparator())) {
                    continue;
                }
                final String temp = StringUtils.difference(original, modified);
                if (lowest_difference.length() > temp.length()) {
                    lowest_difference = temp;
                    lowest_difference_name = original;
                }
            }
            if (lowest_difference_name != modified && lowest_difference_name.contains(getFirstChars(modified))) {
                System.out.println(String.format("SIMILAR MOD NAMES: \"%s\" -> \"%s\" (%s)", lowest_difference_name, modified, lowest_difference));
                overrides.getModOverrides().add(new FileOverride(addHashes ? hashes_modified_mods.get(modified) : null, null, OverrideAction.REPLACE, modified, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_mods, modified).readBytesWithoutException())) : null, null, lowest_difference_name));
                used_modified.add(modified);
                used_original.add(lowest_difference_name);
            } else {
                System.out.println(String.format("FOUND NO SIMILAR MOD NAME FOR \"%s\"", modified));
            }
        }
        //
        for (String original : hashes_original_mods.keySet()) {
            final String hash_original = hashes_original_mods.get(original);
            final String hash_modified = hashes_modified_mods.get(original);
            if (used_original.contains(original)) {
                continue;
            }
            if (hash_modified == null) {
                overrides.getModOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Objects.equals(hash_original, hash_modified)) {
                overrides.getModOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.CHANGE, original, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_mods, original).readBytesWithoutException())) : null, null, null));
            }
        }
        for (String modified : hashes_modified_mods.keySet()) {
            final String hash_modified = hashes_modified_mods.get(modified);
            final String hash_original = hashes_original_mods.get(modified);
            if (used_modified.contains(modified)) {
                continue;
            }
            if (hash_original == null) {
                overrides.getModOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.ADD, modified, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_mods, modified).readBytesWithoutException())) : null, null, null));
            }
        }
        //
        used_original.clear();
        used_modified.clear();
        // ===========================================================================================
        // ===========================================================================================
        // Config
        // ===========================================================================================
        // ===========================================================================================
        for (String original : hashes_original_config.keySet().stream().sorted().collect(Collectors.toList())) {
            if (used_original.contains(original) || hashes_modified_config.containsKey(original)) {
                continue;
            }
            final String hash_original = hashes_original_config.get(original);
            if (!isHashOk(hash_original)) {
                continue;
            }
            boolean renamed = false;
            for (String modified : hashes_modified_config.keySet().stream().sorted().collect(Collectors.toList())) {
                if (used_modified.contains(modified)) {
                    continue;
                }
                final String hash_modified = hashes_modified_config.get(modified);
                if (Objects.equals(hash_original, hash_modified) && !Objects.equals(original, modified)) {
                    if (renamed) {
                        overrides.getConfigOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.COPY, modified, null, null, original));
                    } else {
                        renamed = true;
                        overrides.getConfigOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.RENAME, modified, null, null, original));
                    }
                    used_modified.add(modified);
                }
            }
            if (renamed) {
                used_original.add(original);
            }
        }
        //
        for (String original : hashes_original_config.keySet()) {
            final String hash_original = hashes_original_config.get(original);
            final String hash_modified = hashes_modified_config.get(original);
            if (hash_modified == null) {
                overrides.getConfigOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Objects.equals(hash_original, hash_modified)) {
                overrides.getConfigOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.CHANGE, original, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_config, original).readBytesWithoutException())) : null, null, null));
            }
        }
        for (String modified : hashes_modified_config.keySet()) {
            final String hash_modified = hashes_modified_config.get(modified);
            final String hash_original = hashes_original_config.get(modified);
            if (hash_original == null) {
                overrides.getConfigOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.ADD, modified, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_config, modified).readBytesWithoutException())) : null, null, null));
            }
        }
        //
        used_original.clear();
        used_modified.clear();
        // ===========================================================================================
        // ===========================================================================================
        // Scripts
        // ===========================================================================================
        // ===========================================================================================
        for (String original : hashes_original_scripts.keySet().stream().sorted().collect(Collectors.toList())) {
            if (used_original.contains(original) || hashes_modified_scripts.containsKey(original)) {
                continue;
            }
            final String hash_original = hashes_original_scripts.get(original);
            if (!isHashOk(hash_original)) {
                continue;
            }
            boolean renamed = false;
            for (String modified : hashes_modified_scripts.keySet().stream().sorted().collect(Collectors.toList())) {
                if (used_modified.contains(modified)) {
                    continue;
                }
                final String hash_modified = hashes_modified_scripts.get(modified);
                if (Objects.equals(hash_original, hash_modified) && !Objects.equals(original, modified)) {
                    if (renamed) {
                        overrides.getScriptOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.COPY, modified, null, null, original));
                    } else {
                        renamed = true;
                        overrides.getScriptOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.RENAME, modified, null, null, original));
                    }
                    used_modified.add(modified);
                }
            }
            if (renamed) {
                used_original.add(original);
            }
        }
        //
        for (String original : hashes_original_scripts.keySet()) {
            final String hash_original = hashes_original_scripts.get(original);
            final String hash_modified = hashes_modified_scripts.get(original);
            if (hash_modified == null) {
                overrides.getScriptOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Objects.equals(hash_original, hash_modified)) {
                overrides.getScriptOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.CHANGE, original, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_scripts, original).readBytesWithoutException())) : null, null, null));
            }
        }
        for (String modified : hashes_modified_scripts.keySet()) {
            final String hash_modified = hashes_modified_scripts.get(modified);
            final String hash_original = hashes_original_scripts.get(modified);
            if (hash_original == null) {
                overrides.getScriptOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.ADD, modified, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_scripts, modified).readBytesWithoutException())) : null, null, null));
            }
        }
        //
        used_original.clear();
        used_modified.clear();
        // ===========================================================================================
        // ===========================================================================================
        // Resources
        // ===========================================================================================
        // ===========================================================================================
        for (String original : hashes_original_resources.keySet().stream().sorted().collect(Collectors.toList())) {
            if (used_original.contains(original) || hashes_modified_resources.containsKey(original)) {
                continue;
            }
            final String hash_original = hashes_original_resources.get(original);
            if (!isHashOk(hash_original)) {
                continue;
            }
            boolean renamed = false;
            for (String modified : hashes_modified_resources.keySet().stream().sorted().collect(Collectors.toList())) {
                if (used_modified.contains(modified)) {
                    continue;
                }
                final String hash_modified = hashes_modified_resources.get(modified);
                if (Objects.equals(hash_original, hash_modified) && !Objects.equals(original, modified)) {
                    if (renamed) {
                        overrides.getResourceOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.COPY, modified, null, null, original));
                    } else {
                        renamed = true;
                        overrides.getResourceOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.RENAME, modified, null, null, original));
                    }
                    used_modified.add(modified);
                }
            }
            if (renamed) {
                used_original.add(original);
            }
        }
        //
        for (String original : hashes_original_resources.keySet()) {
            final String hash_original = hashes_original_resources.get(original);
            final String hash_modified = hashes_modified_resources.get(original);
            if (hash_modified == null) {
                overrides.getResourceOverrides().add(new FileOverride(null, null, OverrideAction.REMOVE, original, null, null, null));
            } else if (!Objects.equals(hash_original, hash_modified)) {
                overrides.getResourceOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.CHANGE, original, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_resources, original).readBytesWithoutException())) : null, null, null));
            }
        }
        for (String modified : hashes_modified_resources.keySet()) {
            final String hash_modified = hashes_modified_resources.get(modified);
            final String hash_original = hashes_original_resources.get(modified);
            if (hash_original == null) {
                overrides.getResourceOverrides().add(new FileOverride(addHashes ? hash_modified : null, null, OverrideAction.ADD, modified, includeData ? ("data:" + Base64.getEncoder().encodeToString(new AdvancedFile(advancedFile_minecraft_modified_resources, modified).readBytesWithoutException())) : null, null, null));
            }
        }
        //
        used_original.clear();
        used_modified.clear();
        return overrides;
    }
    
    static void addHashesFromFolder(AdvancedFile advancedFile_folder, Map<String, String> hashes) {
        if (advancedFile_folder == null || !advancedFile_folder.exists() || !advancedFile_folder.isDirectory()) {
            return;
        }
        advancedFile_folder.listFiles(true).stream().filter(AdvancedFile::isFile).filter((advancedFile) -> !advancedFile.getName().equals(".DS_Store")).forEach((advancedFile) -> hashes.put(advancedFile.getPath().substring(advancedFile_folder.getPath().length() + 1, advancedFile.getPath().length()), Base64.getEncoder().encodeToString(HashUtil.hashSHA256(advancedFile.readBytesWithoutException()))));
    }
    
    public static final String REGEX = "([A-Za-z]+)(?:-|[0-9]).*";
    public static final Pattern PATTERN = Pattern.compile(REGEX);
    
    static String getName(String path) {
        final int index = path.lastIndexOf(OSUtil.CURRENT_OS_HELPER.getFileSeparator());
        if (index != -1) {
            return path.substring(index + OSUtil.CURRENT_OS_HELPER.getFileSeparator().length());
        }
        return path;
    }
    
    static String getFirstChars(String path) {
        final String name = getName(path);
        final Matcher matcher = PATTERN.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return name;
    }
    
}
