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

import dev.nafusoft.eventassistcore.EventAssistCore;
import dev.nafusoft.eventassistcore.automation.AutomationActionContext;
import dev.nafusoft.eventassistcore.automation.actions.AutomationAction;
import dev.nafusoft.eventassistcore.event.GameEventEndEvent;
import dev.nafusoft.eventassistcore.event.GameEventStartEvent;
import dev.nafusoft.eventassistcore.utils.PluginLogger;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class ChangeEventStateHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEventStartEvent(GameEventStartEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(EventAssistCore.getInstance(), () -> {
            PluginLogger.info("Perform event start automation: " + event.getGameEvent().getEventName());
            val automation = EventAssistCore.getInstance().getAutomationManager().getStartAutomation(event.getGameEvent().getEventId());
            if (automation != null) {
                for (AutomationAction automationAction : automation.getActions()) {
                    automationAction.execute(new AutomationActionContext(event.getGameEvent()));
                    try {
                        Thread.sleep(automation.getAutomationDelayTime() * 1000L);
                    } catch (InterruptedException e) {
                        PluginLogger.log(Level.WARNING,
                                """
                                        An interrupt has occurred in the automation thread.
                                        Normally this error is not reproduced.
                                        If the problem repeats, please report it to the developer with the status of the operation.
                                        """,
                                e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        event.getGameEvent().changeStatus(event.getNewStatus());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameEventEndEvent(GameEventEndEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(EventAssistCore.getInstance(), () -> {
            PluginLogger.info("Perform event end automation: " + event.getGameEvent().getEventName());
            val automation = EventAssistCore.getInstance().getAutomationManager().getEndAutomation(event.getGameEvent().getEventId());
            if (automation != null) {
                for (AutomationAction automationAction : automation.getActions()) {
                    automationAction.execute(new AutomationActionContext(event.getGameEvent()));
                    try {
                        Thread.sleep(automation.getAutomationDelayTime() * 1000L);
                    } catch (InterruptedException e) {
                        PluginLogger.log(Level.WARNING,
                                """
                                        An interrupt has occurred in the automation thread.
                                        Normally this error is not reproduced.
                                        If the problem repeats, please report it to the developer with the status of the operation.
                                        """,
                                e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        event.getGameEvent().changeStatus(event.getNewStatus());
    }
}
