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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.nafusoft.eventassistcore.automation.ActionOptions;
import lombok.val;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@JsonSerialize(using = ItemGiveActionOptions.ItemGiveActionOptionsSerializer.class)
@JsonDeserialize(using = ItemGiveActionOptions.ItemGiveActionOptionsDeserializer.class)
public record ItemGiveActionOptions(ItemStack itemStack) implements ActionOptions {

    public static class ItemGiveActionOptionsSerializer extends StdSerializer<ItemGiveActionOptions> {

        protected ItemGiveActionOptionsSerializer() {
            this(null);
        }

        protected ItemGiveActionOptionsSerializer(Class<ItemGiveActionOptions> t) {
            super(t);
        }

        @Override
        public void serialize(ItemGiveActionOptions value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(value.itemStack());
            dataOutput.close();
            val encodedItem = Base64Coder.encodeLines(outputStream.toByteArray());

            gen.writeStartObject();
            gen.writeStringField("itemStack", encodedItem);
            gen.writeEndObject();
        }
    }

    public static class ItemGiveActionOptionsDeserializer extends StdDeserializer<ItemGiveActionOptions> {

        protected ItemGiveActionOptionsDeserializer() {
            this(null);
        }

        public ItemGiveActionOptionsDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public ItemGiveActionOptions deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String encodedItem = node.get("itemStack").asText();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(encodedItem));
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return new ItemGiveActionOptions((ItemStack) dataInput.readObject());
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to decode class type.", e);
            }
        }
    }
}
