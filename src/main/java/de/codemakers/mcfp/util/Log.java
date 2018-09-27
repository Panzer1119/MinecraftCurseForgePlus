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

import de.codemakers.base.logger.Logger;
import de.codemakers.base.os.OSUtil;
import de.codemakers.base.util.tough.ToughSupplier;
import de.codemakers.io.file.AdvancedFile;

import java.util.Base64;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Log {
    
    public static boolean LOG_ENABLE = false;
    
    public static final Queue<String> ACTIONS = new ConcurrentLinkedQueue<>();
    
    public static final String LOG_FORMAT_STRING = "FILE:%s:OLD:%s:NEW:%s";
    public static final String LOG_REGEX_STRING = "FILE:(.+):OLD:(.*):NEW:(.*)";
    public static final Pattern LOG_REGEX_PATTERN = Pattern.compile(LOG_REGEX_STRING);
    
    public static String actionToString(AdvancedFile advancedFile, byte[] data_old, byte[] data_new) {
        if (advancedFile == null) {
            return null;
        }
        return String.format(LOG_FORMAT_STRING, advancedFile.getAbsolutePath(), data_old == null ? "" : Base64.getEncoder().encodeToString(data_old), data_new == null ? "" : Base64.getEncoder().encodeToString(data_new));
    }
    
    public static boolean reverseAction(String action) {
        if (action == null || action.isEmpty()) {
            return false;
        }
        final Matcher matcher = LOG_REGEX_PATTERN.matcher(action);
        if (!matcher.matches()) {
            return false;
        }
        try {
            final AdvancedFile advancedFile = new AdvancedFile(matcher.group(1));
            final byte[] data_old = (matcher.group(2) == null || matcher.group(2).isEmpty()) ? null : Base64.getDecoder().decode(matcher.group(2));
            //final byte[] data_new = (matcher.group(3) == null || matcher.group(3).isEmpty()) ? null : Base64.getDecoder().decode(matcher.group(3)); //Is not needed, since we do not need this, but its good to have this in the log to see the full changes
            if (data_old == null) { //A file was created, so delete it
                return advancedFile.delete();
            } //Else a file was deleted or changed, so just override it
            return advancedFile.writeBytes(data_old);
        } catch (Exception ex) {
            Logger.handleError(ex);
            return false;
        }
    }
    
    public static void addActionIfEnabled(AdvancedFile advancedFile, ToughSupplier<byte[]> data_old, ToughSupplier<byte[]> data_new) {
        if (LOG_ENABLE) {
            addAction(advancedFile, data_old.getWithoutException(), data_new.getWithoutException());
        }
    }
    
    public static void addAction(AdvancedFile advancedFile, byte[] data_old, byte[] data_new) {
        ACTIONS.add(actionToString(advancedFile, data_old, data_new));
    }
    
    public static void saveActionsToFile(AdvancedFile advancedFile) {
        Objects.requireNonNull(advancedFile);
        advancedFile.writeBytesWithoutException(ACTIONS.stream().collect(Collectors.joining(OSUtil.CURRENT_OS_HELPER.getLineSeparator())).getBytes());
    }
    
}
