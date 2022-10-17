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

/**
 * Treat all fields in classes implementing this interface as options for the action.<br>
 * All types are String and all fields are initialized in the constructor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ActionOptions {
}
