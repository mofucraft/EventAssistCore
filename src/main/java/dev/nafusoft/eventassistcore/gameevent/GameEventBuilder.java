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

package dev.nafusoft.eventassistcore.gameevent;

import lombok.*;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class GameEventBuilder {
    @Getter(AccessLevel.NONE)
    private final GameEventManager manager;
    private final UUID eventId;
    private final EventOptions eventOptions;
    private final List<UUID> entrant;
    private String eventName;
    private String eventDescription;
    private UUID eventOwner;
    private long eventStartTime;
    private long eventEndTime;
    private Location eventLocation;

    GameEventBuilder(GameEventManager manager) {
        this.manager = manager;
        eventId = UUID.randomUUID();
        eventOptions = new EventOptions();
        entrant = new ArrayList<>();
    }

    /**
     * Creates a new event with the entered data.
     * The event is automatically registered when this method is executed.
     *
     * @return Created {@link GameEvent GameEvent}
     * @throws IllegalStateException Thrown when input data is not complete.
     */
    public DefaultGameEvent build() throws IllegalStateException {
        if (!canBuild())
            throw new IllegalStateException("The configuration required for the build has not been completed.");

        val event = new DefaultGameEvent(
                eventId,
                eventName,
                eventDescription,
                eventOwner,
                eventStartTime,
                eventEndTime,
                eventLocation,
                entrant,
                eventOptions,
                GameEventStatus.UPCOMING
        );

        manager.registerEvent(event);

        return event;
    }

    public boolean canBuild() {
        return eventName != null && eventDescription != null && eventOwner != null && eventStartTime != 0 && eventEndTime != 0;
    }

    public GameEventBuilder name(@NonNull String eventName) {
        this.eventName = eventName;
        return this;
    }

    public GameEventBuilder description(@NonNull String eventDescription) {
        this.eventDescription = eventDescription;
        return this;
    }

    public GameEventBuilder owner(@NonNull UUID eventOwner) {
        this.eventOwner = eventOwner;
        return this;
    }

    public GameEventBuilder startTime(long eventStartTime) {
        this.eventStartTime = eventStartTime;
        return this;
    }

    public GameEventBuilder endTime(long eventEndTime) {
        this.eventEndTime = eventEndTime;
        return this;
    }

    public GameEventBuilder location(Location eventLocation) {
        this.eventLocation = eventLocation;
        return this;
    }
}
