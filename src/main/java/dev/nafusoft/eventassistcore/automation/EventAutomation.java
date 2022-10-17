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

package dev.nafusoft.eventassistcore.automation;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nafusoft.eventassistcore.automation.actions.AutomationAction;

import java.util.Collections;
import java.util.List;

public class EventAutomation {
    private final List<AutomationAction> actions;
    private final int automationDelayTime;

    public EventAutomation(@JsonProperty("actions") List<AutomationAction> actions, @JsonProperty("automationDelayTime") int automationDelayTime) {
        this.actions = actions;
        this.automationDelayTime = automationDelayTime;
    }

    public List<AutomationAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public int getAutomationDelayTime() {
        return automationDelayTime;
    }
}
