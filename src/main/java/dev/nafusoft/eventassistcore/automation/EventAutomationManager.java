/*
 * Copyright 2023 NAFU_at
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

package dev.nafusoft.eventassistcore.automation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.nafusoft.eventassistcore.automation.actions.ActionRegistry;
import dev.nafusoft.eventassistcore.automation.actions.AutomationAction;
import dev.nafusoft.eventassistcore.database.DatabaseConnector;
import dev.nafusoft.eventassistcore.database.EventAutomationTable;
import dev.nafusoft.eventassistcore.exception.EventRegisterException;
import dev.nafusoft.eventassistcore.gameevent.GameEvent;
import dev.nafusoft.eventassistcore.gameevent.GameEventBuilder;
import dev.nafusoft.eventassistcore.utils.PluginLogger;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public final class EventAutomationManager extends EventAutomationTable {
    private static final Gson gson = new Gson();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ActionRegistry actionRegistry;

    public EventAutomationManager(@Nullable String prefix, @NotNull DatabaseConnector connector) throws SQLException {
        super(prefix, "automations", connector);
        createTable();

        actionRegistry = new ActionRegistry();
    }

    /**
     * Returns the {@link ActionRegistry ActionRegistry} instance.
     *
     * @return {@link ActionRegistry ActionRegistry} instance.
     */
    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    /**
     * Returns a builder to create an event automation.
     *
     * @param event          Event to create automation
     * @param automationType Type of automation
     * @return {@link GameEventBuilder GameEventBuilder}
     */
    public @NotNull AutomationBuilder getBuilder(@NotNull GameEvent event, AutomationType automationType) {
        try {
            add(event.getEventId());
        } catch (SQLException e) {
            throw new EventRegisterException("Failed to register automation", e);
        }

        return new AutomationBuilder(this, event, automationType);
    }

    public @Nullable EventAutomation getStartAutomation(@NotNull UUID eventId) {
        try {
            String[] automations = getById(eventId);
            return readJsonSafe(automations[0], eventId);
        } catch (SQLException e) {
            PluginLogger.log(Level.WARNING, "Failed to get event data.", e);
            return null;
        }
    }

    public @Nullable EventAutomation getEndAutomation(@NotNull UUID eventId) {
        try {
            String[] automations = getById(eventId);
            return readJsonSafe(automations[1], eventId);
        } catch (SQLException e) {
            PluginLogger.log(Level.WARNING, "Failed to get event data.", e);
            return null;
        }
    }

    void updateAutomation(@NotNull UUID eventId, @NotNull EventAutomation automation, AutomationType type) {
        try {
            if (type == AutomationType.START_AUTOMATION)
                updateStartAutomation(eventId, automation);
            else if (type == AutomationType.END_AUTOMATION)
                updateEndAutomation(eventId, automation);
        } catch (SQLException | JsonProcessingException e) {
            PluginLogger.log(Level.WARNING, "Failed to update automation.", e);
        }
    }

    private @Nullable EventAutomation readJsonSafe(@Nullable String automationJson, @NotNull UUID eventId) {
        if (StringUtils.isEmpty(automationJson))
            return null;

        val loadFailed = new ArrayList<String>(); // Stores the Json of Automation Actions that failed to load.

        JsonObject baseJson = gson.fromJson(automationJson, JsonObject.class);

        int automationDelayTime = baseJson.get("automationDelayTime").getAsInt();
        JsonArray actionsArray = baseJson.getAsJsonArray("actions");
        PluginLogger.log(Level.INFO, "Loading automation (EventID = {0}, Find = {1})", new Object[]{eventId, actionsArray.size()});

        // Start load action
        val actions = new ArrayList<AutomationAction>();
        for (JsonElement action : actionsArray) {
            val actionBody = action.getAsJsonObject().toString();

            try {
                val actionObject = MAPPER.readValue(actionBody, AutomationAction.class);

                if (actionObject != null)
                    actions.add(actionObject);
            } catch (JsonProcessingException e) {
                PluginLogger.log(Level.WARNING, "Failed to load action!", e);
                loadFailed.add(actionBody);
            }
        }

        // Build EventAutomation object
        val loadedEventAutomation = new LoadedEventAutomation(actions, automationDelayTime, loadFailed);
        PluginLogger.log(Level.INFO,
                "Automation loaded. (EventID = {0}, Loaded = {1}, Failed = {2})",
                new Object[]{eventId, actions.size(), loadFailed.size()});

        return loadedEventAutomation;
    }
}
