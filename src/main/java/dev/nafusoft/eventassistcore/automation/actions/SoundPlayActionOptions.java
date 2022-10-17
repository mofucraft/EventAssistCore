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
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

/**
 * @param soundType     See below for a list of supported sounds.
 *                      <a href="https://papermc.io/javadocs/paper/1.19/org/bukkit/Sound.html">javadoc</a>
 * @param soundCategory See below for a list of supported sound categories.
 *                      <a href="https://papermc.io/javadocs/paper/1.19/org/bukkit/SoundCategory.html">javadoc</a>
 * @param location      Location to play sound. (Format: World, LocationX, LocationY, LocationZ)
 * @param volume        Volume of the sound to play.
 * @param pitch         Pitch of the sound to play.
 */
public record SoundPlayActionOptions(String soundType, String soundCategory,
                                     String location, float volume,
                                     float pitch) implements ActionOptions {

    public Sound getSoundType() {
        return Sound.valueOf(soundType);
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.valueOf(soundCategory);
    }

    public String getLocation() {
        return location;
    }
}
