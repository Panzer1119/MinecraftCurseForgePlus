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

public enum OverrideAction {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace"),
    ENABLE("enable"),
    DISABLE("disable"),
    CHANGE("change"),
    UNKNOWN(null);
    
    private final String action;
    
    OverrideAction(String action) {
        this.action = action;
    }
    
    public final String getAction() {
        return action;
    }
    
    public static final OverrideAction ofAction(String action) {
        for (OverrideAction overrideAction : values()) {
            if (overrideAction.getAction().equals(action)) {
                return overrideAction;
            }
        }
        return UNKNOWN;
    }
    
}
