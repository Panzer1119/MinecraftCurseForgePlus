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

import com.google.gson.*;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.mcfp.entities.FileOverride;
import de.codemakers.mcfp.entities.Overrides;
import de.codemakers.mcfp.util.Util;

public class GeneratorTest {
    
    public static final void main(String[] args) throws Exception {
        final AdvancedFile advancedFile_minecraft_original = new AdvancedFile(args[0]);
        final AdvancedFile advancedFile_minecraft_modified = new AdvancedFile(args[1]);
        final AdvancedFile advancedFile_overrides_json = new AdvancedFile(args[2]);
        final boolean includeData = args.length > 3 ? Boolean.parseBoolean(args[3]) : false;
        final Overrides overrides = Util.generateOverrides(advancedFile_minecraft_original, advancedFile_minecraft_modified, false, includeData);
        System.out.println(overrides);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
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
        final String json = gsonBuilder.create().toJson(overrides);
        //System.out.println(json);
        advancedFile_overrides_json.writeBytesWithoutException(json.getBytes());
    }
    
}
