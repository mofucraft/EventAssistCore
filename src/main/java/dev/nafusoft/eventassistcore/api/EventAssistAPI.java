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

package dev.nafusoft.eventassistcore.api;

import dev.nafusoft.eventassistcore.EventAssistCore;
import dev.nafusoft.eventassistcore.automation.ActionOptions;
import dev.nafusoft.eventassistcore.automation.EventAutomationManager;
import dev.nafusoft.eventassistcore.automation.actions.AutomationAction;
import dev.nafusoft.eventassistcore.gameevent.GameEvent;
import dev.nafusoft.eventassistcore.gameevent.GameEventBuilder;
import dev.nafusoft.eventassistcore.gameevent.GameEventManager;
import dev.nafusoft.eventassistcore.gameevent.GameEventStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class EventAssistAPI {
    private final EventAssistCore eventAssist;


    public EventAssistAPI(EventAssistCore eventAssist) {
        this.eventAssist = eventAssist;
    }

    public static EventAssistAPI getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public GameEventManager getEventManager() {
        return eventAssist.getEventManager();
    }

    public EventAutomationManager getAutomationManager() {
        return eventAssist.getAutomationManager();
    }

    /**
     * Get registered events.
     *
     * @param id Event id to get
     * @return Returns {@link GameEvent GameEvent} if the event with the specified id has been registered, otherwise null.
     */
    public @Nullable GameEvent getEvent(@NotNull UUID id) {
        return getEventManager().getEvent(id);
    }

    /**
     * Get events for the specified status state.
     *
     * @param eventStatus The status to get, if null is specified, return all events.
     * @return List of {@link GameEvent GameEvent} with the corresponding status state
     */
    public @NotNull List<GameEvent> getEvents(@Nullable GameEventStatus eventStatus) {
        return getEventManager().getEvents(eventStatus);
    }

    /**
     * Returns a builder to create a new event.
     *
     * @return {@link GameEventBuilder GameEventBuilder}
     */
    public @NotNull GameEventBuilder getBuilder() {
        return getEventManager().getBuilder();
    }

    /**
     * Delete the event.
     *
     * @param event Event to delete
     */
    public void deleteEvent(@NotNull GameEvent event) {
        getEventManager().deleteEvent(event);
    }


    /**
     * Register an action and its options.
     *
     * @param action  Action to register
     * @param options Options to register
     */
    public void registerAction(@NotNull Class<? extends AutomationAction> action, @NotNull Class<? extends ActionOptions> options) {
        getAutomationManager().getActionRegistry().registerAction(action, options);
    }


    private static class InstanceHolder {
        private static final EventAssistAPI INSTANCE;

        static {
            EventAssistCore core = EventAssistCore.getInstance();
            if (core == null)
                INSTANCE = null;
            else
                INSTANCE = new EventAssistAPI(core);
        }
    }
}
