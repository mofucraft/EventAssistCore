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

package dev.nafusoft.eventassistcore;

import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
    private static final EventAssistCore INSTANCE = EventAssistCore.getInstance();

    private EventAssistConfig eventAssistConfig;

    public void reloadConfig() {
        INSTANCE.reloadConfig();
        FileConfiguration config = INSTANCE.getConfig();

        val databaseType = EventAssistConfig.DatabaseType.valueOf(config.getString("initialization.database.type"));
        val address = config.getString("initialization.database.address");
        val port = config.getInt("initialization.database.port", 3306);
        val database = config.getString("initialization.database.database");
        val username = config.getString("initialization.database.username");
        val password = config.getString("initialization.database.password");
        val tablePrefix = config.getString("initialization.database.tablePrefix");

        eventAssistConfig = new EventAssistConfig(databaseType, address, port, database, username, password, tablePrefix);
    }

    public EventAssistConfig getConfig() {
        return eventAssistConfig;
    }
}
