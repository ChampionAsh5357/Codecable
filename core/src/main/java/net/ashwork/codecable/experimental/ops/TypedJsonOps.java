/*
 * Codecable - ChampionAsh5357
 * SPDX-License-Identifier: MIT
 */

package net.ashwork.codecable.experimental.ops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

//TODO: Finish, document, test
public class TypedJsonOps extends JsonOps {

    public static final TypedJsonOps INSTANCE = new TypedJsonOps(false);
    public static final TypedJsonOps COMPRESSED = new TypedJsonOps(true);

    private static final String DATA_TYPE = "ops:data_type";
    private static final String VALUE = "value";

    protected TypedJsonOps(final boolean compressed) {
        super(compressed);
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject obj && obj.has(DATA_TYPE))
            return this.createDefinedElement(outOps, obj.getAsJsonPrimitive(DATA_TYPE).getAsString(), obj.get(VALUE));
        else return super.convertTo(outOps, input);
    }

    private <U> U createDefinedElement(final DynamicOps<U> ops, final String dataType, final JsonElement value) {
        return switch (dataType) {
            case "map" -> this.convertMap(ops, value);
            case "list" -> this.convertList(ops, value);
            case "empty" -> ops.empty();
            case "string" -> ops.createString(value.getAsJsonPrimitive().getAsString());
            case "boolean" -> ops.createBoolean(value.getAsJsonPrimitive().getAsBoolean());
            case "byte" -> ops.createByte(value.getAsJsonPrimitive().getAsByte());
            case "short" -> ops.createShort(value.getAsJsonPrimitive().getAsShort());
            case "int" -> ops.createInt(value.getAsJsonPrimitive().getAsInt());
            case "long" -> ops.createLong(value.getAsJsonPrimitive().getAsLong());
            case "float" -> ops.createFloat(value.getAsJsonPrimitive().getAsFloat());
            case "double" -> ops.createDouble(value.getAsJsonPrimitive().getAsDouble());
            default -> throw new JsonParseException("Unknown data type: " + dataType);
        };
    }
}
