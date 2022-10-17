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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;

import java.util.Objects;

public class TitleShowAction extends AutomationAction {

    public TitleShowAction(@JsonProperty("options") ActionOptions options) {
        super(options);
    }

    @Override
    public void execute(AutomationActionContext context) {
        TitleShowActionOptions options = (TitleShowActionOptions) getOptions();
        TextComponent titleComponent =
                Component.text(options.title()).color(NamedTextColor.NAMES.value(options.titleColorName()));
        TextComponent subTitleComponent =
                Component.text(options.subTitle()).color(NamedTextColor.NAMES.value(options.subTitleColorName()));
        context.gameEvent().getEntrant().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> Bukkit.getServer().getScheduler().runTask(EventAssistCore.getInstance(),
                        () -> EventAssistCore.getInstance().getAdventure().player(p).showTitle(Title.title(titleComponent, subTitleComponent, Title.DEFAULT_TIMES))));
    }
}
