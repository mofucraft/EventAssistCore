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
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nafusoft.eventassistcore.automation.EventAutomation;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class EventAutomationTable extends DatabaseTable {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public EventAutomationTable(String prefix, String tablename, DatabaseConnector connector) {
        super(prefix, tablename, connector);
    }

    public void createTable() throws SQLException {
        super.createTable("id VARCHAR(36) PRIMARY KEY, start_automation JSON, end_automation JSON");
    }

    protected String[] getById(@NotNull UUID eventId) throws SQLException {
        val automationJson = new String[2];

        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "SELECT * FROM " + getTableName() + " WHERE id = ?"
             )) {
            ps.setString(1, eventId.toString());

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    automationJson[0] = resultSet.getString("start_automation");
                    automationJson[1] = resultSet.getString("end_automation");
                }

                return automationJson;
            }
        }
    }

    protected void add(@NotNull UUID eventId) throws SQLException {
        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "INSERT IGNORE INTO " + getTableName() + " (id, start_automation, end_automation) VALUES (?, ?, ?)"
             )) {
            ps.setString(1, eventId.toString());
            ps.setNull(2, Types.LONGVARCHAR);
            ps.setNull(3, Types.LONGVARCHAR);
            ps.execute();
        }
    }

    protected void updateStartAutomation(@NotNull UUID eventId, @Nullable EventAutomation startAutomation) throws SQLException, JsonProcessingException {
        String startAutomationJson = null;

        if (startAutomation != null)
            startAutomationJson = MAPPER.writeValueAsString(startAutomation);

        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "UPDATE " + getTableName() + " SET start_automation = ? WHERE id = ?"
             )) {
            ps.setString(1, startAutomationJson);
            ps.setString(2, eventId.toString());
            ps.execute();
        }
    }

    protected void updateEndAutomation(@NotNull UUID eventId, @Nullable EventAutomation endAutomation) throws SQLException, JsonProcessingException {
        String endAutomationJson = null;

        if (endAutomation != null)
            endAutomationJson = MAPPER.writeValueAsString(endAutomation);

        try (val connection = getConnector().getConnection();
             val ps = connection.prepareStatement(
                     "UPDATE " + getTableName() + " SET end_automation = ? WHERE id = ?"
             )) {
            ps.setString(1, endAutomationJson);
            ps.setString(2, eventId.toString());
            ps.execute();
        }
    }
}
