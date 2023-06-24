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

import dev.nafusoft.eventassistcore.EventAssistCore;
import dev.nafusoft.eventassistcore.event.GameEventPlayerEntryEvent;
import dev.nafusoft.eventassistcore.event.GameEventStatusUpdateEvent;
import dev.nafusoft.eventassistcore.exception.EventRegisterException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class DefaultGameEvent implements GameEvent {
    private final UUID eventId;
    private final String eventName;
    private final String eventDescription;
    private final UUID eventOwner;
    private final long eventStartTime;
    private final long eventEndTime;
    private final Location eventLocation;
    private final List<UUID> entrant;
    private final EventOptions eventOptions;
    private GameEventStatus eventStatus;

    @Override
    public List<UUID> getEntrant() {
        return Collections.unmodifiableList(entrant);
    }

    @Override
    public boolean entryEvent(Player player) {
        if (!getEntrant().contains(player.getUniqueId())) {
            val event = new GameEventPlayerEntryEvent(this, player);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                entrant.add(player.getUniqueId());
                try {
                    EventAssistCore.getInstance().getEventManager().updateEventEntrant(this);
                    return true;
                } catch (EventRegisterException e) {
                    entrant.remove(player.getUniqueId());
                }
            }
        }

        return false;
    }

    @Override
    public void changeStatus(GameEventStatus eventStatus) {
        val oldStatus = GameEventStatus.valueOf(this.eventStatus.name());
        this.eventStatus = eventStatus;
        Bukkit.getServer().getPluginManager().callEvent(new GameEventStatusUpdateEvent(this, oldStatus, eventStatus));

        EventAssistCore.getInstance().getEventManager().changeEventStatus(this, oldStatus, eventStatus);
    }
}
