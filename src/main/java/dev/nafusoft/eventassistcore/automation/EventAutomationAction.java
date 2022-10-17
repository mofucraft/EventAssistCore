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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface EventAutomationAction {

    /**
     * 設計上メインスレッドとは別スレッド上で動作するため、同期処理はそのままでは実行することができません。
     * それらの処理を実行する場合は{@link org.bukkit.scheduler.BukkitScheduler#runTask}を使用してください。
     */
    void execute(AutomationActionContext content);

    ActionOptions getOptions();
}
