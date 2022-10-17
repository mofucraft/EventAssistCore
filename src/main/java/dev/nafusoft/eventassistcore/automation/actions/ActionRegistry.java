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

import dev.nafusoft.eventassistcore.automation.ActionOptions;
import dev.nafusoft.eventassistcore.automation.EventAutomation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ActionRegistry holds a set of actions and options that can be incorporated into {@link EventAutomation EventAutomation}.
 */
public final class ActionRegistry {
    private final Map<Class<? extends AutomationAction>, Class<? extends ActionOptions>> actions = new LinkedHashMap<>();

    /**
     * Register an action and its options.
     *
     * @param action  Action to register
     * @param options Options to register
     */
    public void registerAction(@NotNull Class<? extends AutomationAction> action, @NotNull Class<? extends ActionOptions> options) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(options);

        // ActionOptions must be Record
        if (!options.isRecord()) throw new IllegalArgumentException("ActionOptions must be Record");

        actions.put(action, options);
    }

    public @NotNull List<Class<? extends AutomationAction>> getActions() {
        return List.copyOf(actions.keySet());
    }

    public @Nullable Class<? extends ActionOptions> getOption(Class<? extends AutomationAction> action) {
        return actions.get(action);
    }
}
