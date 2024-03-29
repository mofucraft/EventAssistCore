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

package dev.nafusoft.eventassistcore.event;

import dev.nafusoft.eventassistcore.gameevent.GameEvent;
import dev.nafusoft.eventassistcore.gameevent.GameEventStatus;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEventStatusUpdateEvent extends GameEventEvent {
    private static final HandlerList handlers = new HandlerList();
    private final GameEventStatus oldStatus;
    private final GameEventStatus newStatus;

    public GameEventStatusUpdateEvent(GameEvent gameEvent, GameEventStatus oldStatus, GameEventStatus newStatus) {
        super(gameEvent);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameEventStatus getOldStatus() {
        return oldStatus;
    }

    public GameEventStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
