/*
 * Copyright 2022 NAFU_at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nafusoft.eventassistcore.utils;

import dev.nafusoft.eventassistcore.EventAssistCore;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.*;

public final class PluginLogger {
    private static final Logger logger;

    static {
        logger = EventAssistCore.getInstance().getLogger();
    }

    private PluginLogger() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }


    public static ResourceBundle getResourceBundle() {
        return logger.getResourceBundle();
    }

    public static String getResourceBundleName() {
        return logger.getResourceBundleName();
    }

    public static void setFilter(Filter newFilter) throws SecurityException {
        logger.setFilter(newFilter);
    }

    public static Filter getFilter() {
        return logger.getFilter();
    }

    public static void log(LogRecord record) {
        logger.log(record);
    }

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void log(Level level, Supplier<String> msgSupplier) {
        logger.log(level, msgSupplier);
    }

    public static void log(Level level, String msg, Object param1) {
        logger.log(level, msg, param1);
    }

    public static void log(Level level, String msg, Object[] params) {
        logger.log(level, msg, params);
    }

    public static void log(Level level, String msg, Throwable thrown) {
        logger.log(level, msg, thrown);
    }

    public static void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        logger.log(level, thrown, msgSupplier);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        logger.logp(level, sourceClass, sourceMethod, msg);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
        logger.logp(level, sourceClass, sourceMethod, msgSupplier);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        logger.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        logger.logp(level, sourceClass, sourceMethod, msg, params);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        logger.logp(level, sourceClass, sourceMethod, msg, thrown);
    }

    public static void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
        logger.logp(level, sourceClass, sourceMethod, thrown, msgSupplier);
    }

    @Deprecated
    public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        logger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }

    @Deprecated
    public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }

    @Deprecated
    public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }

    public static void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {
        logger.logrb(level, sourceClass, sourceMethod, bundle, msg, params);
    }

    public static void logrb(Level level, ResourceBundle bundle, String msg, Object... params) {
        logger.logrb(level, bundle, msg, params);
    }

    @Deprecated
    public static void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }

    public static void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {
        logger.logrb(level, sourceClass, sourceMethod, bundle, msg, thrown);
    }

    public static void logrb(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        logger.logrb(level, bundle, msg, thrown);
    }

    public static void entering(String sourceClass, String sourceMethod) {
        logger.entering(sourceClass, sourceMethod);
    }

    public static void entering(String sourceClass, String sourceMethod, Object param1) {
        logger.entering(sourceClass, sourceMethod, param1);
    }

    public static void entering(String sourceClass, String sourceMethod, Object[] params) {
        logger.entering(sourceClass, sourceMethod, params);
    }

    public static void exiting(String sourceClass, String sourceMethod) {
        logger.exiting(sourceClass, sourceMethod);
    }

    public static void exiting(String sourceClass, String sourceMethod, Object result) {
        logger.exiting(sourceClass, sourceMethod, result);
    }

    public static void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        logger.throwing(sourceClass, sourceMethod, thrown);
    }

    public static void severe(String msg) {
        logger.severe(msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void config(String msg) {
        logger.config(msg);
    }

    public static void fine(String msg) {
        logger.fine(msg);
    }

    public static void finer(String msg) {
        logger.finer(msg);
    }

    public static void finest(String msg) {
        logger.finest(msg);
    }

    public static void severe(Supplier<String> msgSupplier) {
        logger.severe(msgSupplier);
    }

    public static void warning(Supplier<String> msgSupplier) {
        logger.warning(msgSupplier);
    }

    public static void info(Supplier<String> msgSupplier) {
        logger.info(msgSupplier);
    }

    public static void config(Supplier<String> msgSupplier) {
        logger.config(msgSupplier);
    }

    public static void fine(Supplier<String> msgSupplier) {
        logger.fine(msgSupplier);
    }

    public static void finer(Supplier<String> msgSupplier) {
        logger.finer(msgSupplier);
    }

    public static void finest(Supplier<String> msgSupplier) {
        logger.finest(msgSupplier);
    }

    public static void setLevel(Level newLevel) throws SecurityException {
        logger.setLevel(newLevel);
    }

    public static Level getLevel() {
        return logger.getLevel();
    }

    public static boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }

    public static String getName() {
        return logger.getName();
    }

    public static void addHandler(Handler handler) throws SecurityException {
        logger.addHandler(handler);
    }

    public static void removeHandler(Handler handler) throws SecurityException {
        logger.removeHandler(handler);
    }

    public static Handler[] getHandlers() {
        return logger.getHandlers();
    }

    public static void setUseParentHandlers(boolean useParentHandlers) {
        logger.setUseParentHandlers(useParentHandlers);
    }

    public static boolean getUseParentHandlers() {
        return logger.getUseParentHandlers();
    }

    public static void setResourceBundle(ResourceBundle bundle) {
        logger.setResourceBundle(bundle);
    }

    public static Logger getParent() {
        return logger.getParent();
    }

    public static void setParent(Logger parent) {
        logger.setParent(parent);
    }
}
