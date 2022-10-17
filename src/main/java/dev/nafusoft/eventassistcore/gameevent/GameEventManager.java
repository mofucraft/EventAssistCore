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

package dev.nafusoft.eventassistcore.gameevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.nafusoft.eventassistcore.database.DatabaseConnector;
import dev.nafusoft.eventassistcore.database.GameEventTable;
import dev.nafusoft.eventassistcore.exception.EventRegisterException;
import dev.nafusoft.eventassistcore.utils.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public final class GameEventManager extends GameEventTable {
    private final Map<GameEventStatus, List<GameEvent>> eventStore;
    private final Map<Player, GameEventBuilder> builderStore;

    public GameEventManager(@Nullable String prefix, @NotNull DatabaseConnector connector) throws SQLException {
        super(prefix, connector);
        createTable();

        eventStore = new EnumMap<>(GameEventStatus.class);
        eventStore.put(GameEventStatus.UPCOMING, getByStatus(GameEventStatus.UPCOMING));
        eventStore.put(GameEventStatus.HOLDING, getByStatus(GameEventStatus.HOLDING));
        eventStore.put(GameEventStatus.ENDED, new ArrayList<>());

        builderStore = new HashMap<>();
    }

    /**
     * Get registered events.
     *
     * @param id Event id to get
     * @return Returns {@link GameEvent GameEvent} if the event with the specified id has been registered, otherwise null.
     */
    public @Nullable GameEvent getEvent(@NotNull UUID id) {
        GameEvent result;

        // first, check current event.
        result = getEvents(null).stream().filter(event -> event.getEventId().equals(id)).findFirst().orElse(null);

        // second, check ended event.
        if (result == null) {
            try {
                result = getById(id);
            } catch (SQLException e) {
                PluginLogger.log(Level.WARNING, "Failed to get event data.", e);
            }
        }

        // If the result is null here, there is no event corresponding to that id.
        return result;
    }

    /**
     * Get events for the specified status state.
     *
     * @param eventStatus The status to get, if null is specified, return all events.
     * @return List of {@link GameEvent GameEvent} with the corresponding status state
     */
    public @NotNull List<GameEvent> getEvents(@Nullable GameEventStatus eventStatus) {
        if (eventStatus == null)
            return eventStore.keySet().stream().flatMap(key -> eventStore.get(key).stream()).toList();
        return Collections.unmodifiableList(eventStore.get(eventStatus));
    }

    /**
     * Returns a builder to create a new event.
     *
     * @return {@link GameEventBuilder GameEventBuilder}
     */
    public @NotNull GameEventBuilder getBuilder() {
        return new GameEventBuilder(this);
    }

    /**
     * Delete the event.
     *
     * @param event Event to delete
     */
    public void deleteEvent(@NotNull GameEvent event) {
        try {
            delete(event.getEventId());
            eventStore.get(event.getEventStatus()).remove(event);
        } catch (SQLException e) {
            PluginLogger.log(
                    Level.WARNING,
                    "An error has occurred while registering event information.",
                    e
            );

            throw new EventRegisterException("");
        }
    }


    void registerEvent(@NotNull GameEvent event) {
        try {
            add(event);
            eventStore.get(event.getEventStatus()).add(event);
            builderStore.remove(Bukkit.getServer().getPlayer(event.getEventOwner()));
        } catch (JsonProcessingException | SQLException e) {
            PluginLogger.log(
                    Level.WARNING,
                    "An error has occurred while registering event information.",
                    e
            );

            throw new EventRegisterException("");
        }
    }

    void updateEventOptions(@NotNull GameEvent event) {
        try {
            setOptions(event.getEventId(), event.getEventOptions());
        } catch (JsonProcessingException | SQLException e) {
            PluginLogger.log(
                    Level.WARNING,
                    "An error has occurred while registering event information.",
                    e
            );

            throw new EventRegisterException("");
        }
    }

    void updateEventEntrant(@NotNull GameEvent event) {
        try {
            setEntrant(event.getEventId(), event.getEntrant());
        } catch (JsonProcessingException | SQLException e) {
            PluginLogger.log(
                    Level.WARNING,
                    "An error has occurred while registering event information.",
                    e
            );

            throw new EventRegisterException("");
        }
    }

    void changeEventStatus(GameEvent gameEvent, GameEventStatus oldStatus, GameEventStatus newStatus) {
        PluginLogger.info("Updated event status: " + gameEvent.getEventName() + "(" + oldStatus.name() + " -> " + newStatus.name() + ")");

        eventStore.get(oldStatus).remove(gameEvent);
        eventStore.get(newStatus).add(gameEvent);

        try {
            setStatus(gameEvent.getEventId(), newStatus);
        } catch (SQLException e) {
            PluginLogger.log(
                    Level.WARNING,
                    "An error has occurred while registering event information.",
                    e
            );

            throw new EventRegisterException("");
        }
    }
}
