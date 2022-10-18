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

import dev.nafusoft.eventassistcore.automation.actions.AutomationAction;
import dev.nafusoft.eventassistcore.exception.EventBuilderException;
import dev.nafusoft.eventassistcore.gameevent.GameEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString
public class AutomationBuilder {
    @Getter(AccessLevel.NONE)
    private final EventAutomationManager manager;
    private final GameEvent gameEvent;
    private final AutomationType type;

    private final List<AutomationAction> actions;
    private int actionDelayTime = 1;

    AutomationBuilder(EventAutomationManager manager, GameEvent gameEvent, AutomationType type) {
        this.manager = manager;
        this.gameEvent = gameEvent;
        this.type = type;

        actions = new ArrayList<>();
    }

    public ActionBuilder getActionBuilder(@NotNull Class<? extends AutomationAction> actionClass) {
        return new ActionBuilder(actionClass, gameEvent, manager);
    }

    public List<AutomationAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public EventAutomation build() {
        val eventAutomation = new EventAutomation(actions, actionDelayTime);

        manager.updateAutomation(getGameEvent().getEventId(), eventAutomation, getType());
        return eventAutomation;
    }

    public AutomationBuilder automationAction(AutomationAction automationAction) {
        actions.add(automationAction);
        return this;
    }

    public AutomationBuilder actionDelayTime(int actionDelayTime) {
        this.actionDelayTime = actionDelayTime;
        return this;
    }


    public static class ActionBuilder {
        private final GameEvent gameEvent;

        private final Class<? extends AutomationAction> actionClass;
        private final Class<? extends ActionOptions> optionsClass;
        private final Map<String, RecordComponent> options;
        private final Map<String, Object> actionOptionsMap;

        private ActionBuilder(@NotNull Class<? extends AutomationAction> actionClass,
                              @NotNull GameEvent gameEvent,
                              @NotNull EventAutomationManager manager) {
            Objects.requireNonNull(actionClass);
            Objects.requireNonNull(gameEvent);

            this.actionClass = actionClass;
            this.optionsClass = Objects.requireNonNull(manager.getActionRegistry().getOption(actionClass));
            this.gameEvent = gameEvent;

            options = Arrays.stream(optionsClass.getRecordComponents())
                    .collect(Collectors.toMap(RecordComponent::getName, c -> c, (a, b) -> b, LinkedHashMap::new));

            actionOptionsMap = new LinkedHashMap<>();
            options.keySet().forEach(key -> actionOptionsMap.put(key, null));
        }

        public GameEvent getGameEvent() {
            return gameEvent;
        }

        public AutomationAction build() {
            if (!canBuild())
                throw new IllegalStateException("The configuration required for the build has not been completed.");

            AutomationAction eventAutomationAction;
            ActionOptions actionOptions;

            try {
                // Create ActionOptions
                actionOptions = optionsClass.getDeclaredConstructor(
                        options.values().stream()
                                .map(RecordComponent::getType)
                                .toArray(Class[]::new)
                ).newInstance(actionOptionsMap.values().toArray());

                // Create AutomationAction
                eventAutomationAction = actionClass.getDeclaredConstructor(ActionOptions.class).newInstance(actionOptions);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new EventBuilderException("Failed to create AutomationAction.", e);
            }

            return eventAutomationAction;
        }

        /**
         * @return true if the configuration required for the build has been completed.
         */
        public boolean canBuild() {
            return options.values().stream().allMatch(component -> actionOptionsMap.get(component.getName()) != null || isNullable(component));
        }

        public ActionBuilder setActionOption(@NotNull String field, Object value) {
            final RecordComponent component = options.get(field);
            if (component == null) {
                throw new IllegalArgumentException("The field does not exist in the options class.");
            } else if (value == null) {
                // Check option's field nullability
                if (!isNullable(component))
                    throw new IllegalArgumentException("The field '" + field + "' is primitive type or cannot be null.");

                actionOptionsMap.putIfAbsent(field, null); // nullを許容するフィールドの場合はnullを許容する
            } else {
                // Check if the value's object type matches the option's type
                if (!checkType(component.getType(), value))
                    throw new IllegalArgumentException("The value's object type does not match the option's type. " +
                            "(value: " + value.getClass().getName() + ", option: " + component.getType().getName() + ")");

                actionOptionsMap.putIfAbsent(field, value);
            }

            return this;
        }

        /**
         * Returns option's field names and types
         *
         * @return option's field names and types
         */
        public Map<String, Class<?>> getOptions() {
            return options.values().stream()
                    .collect(Collectors.toMap(RecordComponent::getName, RecordComponent::getType, (a, b) -> b, LinkedHashMap::new));
        }

        /**
         * Returns the class of the action you are building.
         *
         * @return the class of the action you are building.
         */
        public @NotNull Class<? extends AutomationAction> getActionClass() {
            return actionClass;
        }

        /**
         * Returns the {@link ActionOptions} class that will be paired with the action you are building.
         *
         * @return the {@link ActionOptions} class that will be paired with the action you are building.
         */
        public @NotNull Class<? extends ActionOptions> getOptionsClass() {
            return optionsClass;
        }

        /**
         * Returns a description attached to the option.
         *
         * @param field Option name to get description
         * @return a description attached to the option.
         */
        public @NotNull String getOptionDescription(@NotNull String field) {
            Objects.requireNonNull(field);

            val description = options.get(field).getAnnotation(OptionDescription.class);
            return description != null ? description.value() : "";
        }


        private boolean isNullable(RecordComponent component) {
            return !(component.getType().isPrimitive() || Arrays.stream(component.getAnnotations()).noneMatch(a ->
                    a.annotationType().equals(org.jetbrains.annotations.Nullable.class)
                            || a.annotationType().equals(javax.annotation.Nullable.class)));
        }

        private boolean checkType(Class<?> type, Object value) {
            if (type.isPrimitive()) {
                if (value == null) return false;
                if (type.equals(boolean.class)) return value instanceof Boolean;
                if (type.equals(byte.class)) return value instanceof Byte;
                if (type.equals(short.class)) return value instanceof Short;
                if (type.equals(int.class)) return value instanceof Integer;
                if (type.equals(long.class)) return value instanceof Long;
                if (type.equals(float.class)) return value instanceof Float;
                if (type.equals(double.class)) return value instanceof Double;
                if (type.equals(char.class)) return value instanceof Character;
            } else {
                return type.isInstance(value);
            }
            return false;
        }
    }
}
