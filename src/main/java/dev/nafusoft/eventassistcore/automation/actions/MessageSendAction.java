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

package dev.nafusoft.eventassistcore.automation.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nafusoft.eventassistcore.EventAssistCore;
import dev.nafusoft.eventassistcore.automation.ActionOptions;
import dev.nafusoft.eventassistcore.automation.AutomationActionContext;
import org.bukkit.Bukkit;

import java.util.Objects;

public class MessageSendAction extends AutomationAction {

    public MessageSendAction(@JsonProperty("options") ActionOptions options) {
        super(options);
    }

    @Override
    public void execute(AutomationActionContext context) {
        MessageSendActionOptions options = (MessageSendActionOptions) getOptions();
        context.gameEvent().getEntrant().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> Bukkit.getServer().getScheduler().runTask(EventAssistCore.getInstance(), () -> p.sendMessage(options.message())));
    }
}
