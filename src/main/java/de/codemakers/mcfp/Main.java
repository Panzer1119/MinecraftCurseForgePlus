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

package de.codemakers.mcfp;

import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;

public class Main {
    
    public static final String MODS_FOLDER_NAME = "mods";
    public static final String CONFIG_FOLDER_NAME = "config";
    public static final String SCRIPTS_FOLDER_NAME = "scripts";
    public static final String RESOURCES_FOLDER_NAME = "resources";
    
    private static AdvancedFile MINECRAFT_FOLDER = null;
    private static AdvancedFile MINECRAFT_MODS_FOLDER = null;
    private static AdvancedFile MINECRAFT_CONFIG_FOLDER = null;
    private static AdvancedFile MINECRAFT_SCRIPTS_FOLDER = null;
    private static AdvancedFile MINECRAFT_RESOURCES_FOLDER = null;
    
    public static final void main(String[] args) {
        if (args != null && args.length > 0) {
            setMinecraftFolder(new AdvancedFile(args[0]));
            Logger.log(String.format("Setted Minecraft Folder to \"%s\" (absolute: \"%s\")", getMinecraftFolder(), getMinecraftFolder().getAbsolutePath()), LogLevel.FINE);
        }
        Logger.log("This is the " + Main.class.getSimpleName() + " class", LogLevel.FINEST);
        Logger.log(String.format("Minecraft Mods Folder: \"%s\" (absolute: \"%s\")", getMinecraftModsFolder(), getMinecraftModsFolder().getAbsolutePath()), LogLevel.FINER);
        Logger.log(String.format("Minecraft Config Folder: \"%s\" (absolute: \"%s\")", getMinecraftConfigFolder(), getMinecraftConfigFolder().getAbsolutePath()), LogLevel.FINER);
        Logger.log(String.format("Minecraft Scripts Folder: \"%s\" (absolute: \"%s\")", getMinecraftScriptsFolder(), getMinecraftScriptsFolder().getAbsolutePath()), LogLevel.FINER);
        Logger.log(String.format("Minecraft Resources Folder: \"%s\" (absolute: \"%s\")", getMinecraftResourcesFolder(), getMinecraftResourcesFolder().getAbsolutePath()), LogLevel.FINER);
    }
    
    static void setMinecraftFolder(AdvancedFile advancedFile) {
        MINECRAFT_FOLDER = advancedFile;
        MINECRAFT_MODS_FOLDER = null;
    }
    
    public static AdvancedFile getMinecraftFolder() {
        return MINECRAFT_FOLDER;
    }
    
    public static AdvancedFile getMinecraftModsFolder() {
        if (MINECRAFT_FOLDER == null) {
            return null;
        }
        if (MINECRAFT_MODS_FOLDER == null) {
            MINECRAFT_MODS_FOLDER = new AdvancedFile(MINECRAFT_FOLDER, MODS_FOLDER_NAME);
        }
        return MINECRAFT_MODS_FOLDER;
    }
    
    public static AdvancedFile getMinecraftConfigFolder() {
        if (MINECRAFT_FOLDER == null) {
            return null;
        }
        if (MINECRAFT_CONFIG_FOLDER == null) {
            MINECRAFT_CONFIG_FOLDER = new AdvancedFile(MINECRAFT_FOLDER, CONFIG_FOLDER_NAME);
        }
        return MINECRAFT_CONFIG_FOLDER;
    }
    
    public static AdvancedFile getMinecraftScriptsFolder() {
        if (MINECRAFT_FOLDER == null) {
            return null;
        }
        if (MINECRAFT_SCRIPTS_FOLDER == null) {
            MINECRAFT_SCRIPTS_FOLDER = new AdvancedFile(MINECRAFT_FOLDER, SCRIPTS_FOLDER_NAME);
        }
        return MINECRAFT_SCRIPTS_FOLDER;
    }
    
    public static AdvancedFile getMinecraftResourcesFolder() {
        if (MINECRAFT_FOLDER == null) {
            return null;
        }
        if (MINECRAFT_RESOURCES_FOLDER == null) {
            MINECRAFT_RESOURCES_FOLDER = new AdvancedFile(MINECRAFT_FOLDER, RESOURCES_FOLDER_NAME);
        }
        return MINECRAFT_RESOURCES_FOLDER;
    }
    
}
