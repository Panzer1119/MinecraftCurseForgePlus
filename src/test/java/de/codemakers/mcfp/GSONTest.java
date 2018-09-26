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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.codemakers.mcfp.entities.Overrides;

import java.io.File;
import java.io.FileReader;

public class GSONTest {
    
    public static final void main(String[] args) throws Exception {
        System.out.println("This is a Test class");
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        final Overrides overrides = gson.fromJson(new FileReader(new File("src/test/resources/test_overrides.json")), Overrides.class);
        System.out.println(overrides);
    }
    
}
