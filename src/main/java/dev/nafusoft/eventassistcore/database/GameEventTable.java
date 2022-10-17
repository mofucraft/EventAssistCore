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

package dev.nafusoft.eventassistcore.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.nafusoft.eventassistcore.gameevent.DefaultGameEvent;
import dev.nafusoft.eventassistcore.gameevent.EventOptions;
import dev.nafusoft.eventassistcore.gameevent.GameEvent;
import dev.nafusoft.eventassistcore.gameevent.GameEventStatus;
import dev.nafusoft.eventassistcore.utils.PluginLogger;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameEventTable extends DatabaseTable {
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    protected GameEventTable(@Nullable String prefix, @NotNull DatabaseConnector connector) {
        super(prefix, "events", connector);
    }

    protected void createTable() throws SQLException {
        super.createTable("id VARCHAR(36) PRIMARY KEY, event_name VARCHAR(32) NOT NULL, description VARCHAR(120), " +
                "owner_id VARCHAR(36) NOT NULL, event_status VARCHAR(16) NOT NULL, start_date DATETIME, " +
                "end_date DATETIME DEFAULT 0, location JSON, entrant JSON, event_options JSON");
    }

    protected List<GameEvent> fetchEvents(final int limit) throws SQLException {
        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "SELECT * FROM " + getTableName() + " LIMIT " + limit
             )) {
            try (ResultSet resultSet = ps.executeQuery()) {
                val events = new ArrayList<GameEvent>();
                while (resultSet.next())
                    events.add(parseResult(resultSet));
                return events;
            }
        } catch (JsonProcessingException e) {
            PluginLogger.log(Level.WARNING, "An error occurred during Json processing.", e);
            return new ArrayList<>();
        }
    }

    protected GameEvent getById(@NotNull UUID eventId) throws SQLException {
        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "SELECT * FROM " + getTableName() + " WHERE id = ?"
             )) {
            ps.setString(1, eventId.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                GameEvent result = null;
                while (resultSet.next())
                    result = parseResult(resultSet);
                return result;
            }
        } catch (JsonProcessingException e) {
            PluginLogger.log(Level.WARNING, "An error occurred during Json processing.", e);
            return null;
        }
    }

    protected List<GameEvent> getByStatus(@NotNull GameEventStatus eventStatus) throws SQLException {
        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "SELECT * FROM " + getTableName() + " WHERE event_status = ?"
             )) {
            ps.setString(1, eventStatus.name());
            try (ResultSet resultSet = ps.executeQuery()) {
                val events = new ArrayList<GameEvent>();
                while (resultSet.next())
                    events.add(parseResult(resultSet));
                return events;
            }
        } catch (JsonProcessingException e) {
            PluginLogger.log(Level.WARNING, "An error occurred during Json processing.", e);
            return new ArrayList<>();
        }
    }

    protected void add(GameEvent gameEvent) throws JsonProcessingException, SQLException {
        val eventId = gameEvent.getEventId();
        val eventName = gameEvent.getEventName();
        val eventDescription = gameEvent.getEventDescription();
        val eventOwner = gameEvent.getEventOwner();
        val eventStatus = gameEvent.getEventStatus();
        val eventStartTime = gameEvent.getEventStartTime();
        val eventEndTime = gameEvent.getEventEndTime();

        String locationJson = null;
        String entrantJson = null;
        String eventOptionsJson = null;

        if (gameEvent.getEventLocation() != null)
            locationJson = MAPPER.writeValueAsString(gameEvent.getEventLocation().serialize());
        if (gameEvent.getEntrant() != null)
            entrantJson = MAPPER.writeValueAsString(gameEvent.getEntrant());
        if (gameEvent.getEventOptions() != null)
            eventOptionsJson = MAPPER.writeValueAsString(gameEvent.getEventOptions());

        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "INSERT INTO " + getTableName() +
                             " (id, event_name, description, owner_id, event_status, start_date, end_date, location, entrant, event_options) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
             )) {
            ps.setString(1, eventId.toString());
            ps.setString(2, eventName);
            ps.setString(3, eventDescription);
            ps.setString(4, eventOwner.toString());
            ps.setString(5, eventStatus.name());
            ps.setTimestamp(6, new Timestamp(eventStartTime));
            ps.setTimestamp(7, new Timestamp(eventEndTime));
            ps.setString(8, locationJson);
            ps.setString(9, entrantJson);
            ps.setString(10, eventOptionsJson);

            ps.execute();
        }
    }

    protected void setStatus(@NotNull UUID eventId, @NotNull GameEventStatus status) throws SQLException {
        PluginLogger.log(Level.INFO, "Updated sql event status: {0}", eventId.toString());

        val eventStatus = status.name();
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTableName() + " SET event_status = ? WHERE id= ?"
             )) {
            ps.setString(1, eventStatus);
            ps.setString(2, eventId.toString());

            ps.execute();
        }
    }

    protected void setEntrant(@NotNull UUID eventId, @NotNull List<UUID> entrant) throws JsonProcessingException, SQLException {
        String entrantJson = MAPPER.writeValueAsString(entrant);

        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTableName() + " SET entrant = ? WHERE id= ?"
             )) {
            ps.setString(1, entrantJson);
            ps.setString(2, eventId.toString());

            ps.execute();
        }
    }

    protected void setOptions(@NotNull UUID eventId, @Nullable EventOptions options) throws JsonProcessingException, SQLException {
        String eventOptionsJson = null;

        if (options != null)
            eventOptionsJson = MAPPER.writeValueAsString(options);

        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTableName() + " SET event_options = ? WHERE id= ?"
             )) {
            ps.setString(1, eventOptionsJson);
            ps.setString(2, eventId.toString());

            ps.execute();
        }
    }

    protected void delete(UUID eventId) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM " + getTableName() + " WHERE id = ?"
             )) {
            ps.setString(1, eventId.toString());
            ps.execute();
        }
    }

    private GameEvent parseResult(ResultSet resultSet) throws JsonProcessingException, SQLException {
        val eventId = UUID.fromString(resultSet.getString("id"));
        val eventName = resultSet.getString("event_name");
        val eventDescription = resultSet.getString("description");
        val eventOwner = UUID.fromString(resultSet.getString("owner_id"));
        val eventStatus = GameEventStatus.valueOf(resultSet.getString("event_status"));
        val eventStartTime = resultSet.getTimestamp("start_date").getTime();
        val eventEndTime = resultSet.getTimestamp("end_date").getTime();

        val locationJson = resultSet.getString("location");
        val entrantJson = resultSet.getString("entrant");
        val eventOptionsJson = resultSet.getString("event_options");

        Location eventLocation = null;
        List<UUID> entrant = new ArrayList<>();
        EventOptions eventOptions = null;

        if (!StringUtils.isEmpty(locationJson))
            eventLocation = Location.deserialize(MAPPER.readValue(locationJson, new TypeReference<>() {
            }));
        if (!StringUtils.isEmpty(entrantJson))
            entrant = MAPPER.readValue(entrantJson, new TypeReference<List<String>>() {
                    }).stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        if (!StringUtils.isEmpty(eventOptionsJson))
            eventOptions = MAPPER.readValue(eventOptionsJson, EventOptions.class);

        return new DefaultGameEvent(eventId,
                eventName,
                eventDescription,
                eventOwner,
                eventStatus,
                eventStartTime,
                eventEndTime,
                eventLocation,
                entrant,
                eventOptions);
    }
}
