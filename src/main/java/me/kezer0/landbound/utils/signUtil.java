package me.kezer0.landbound.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

public class signUtil {

    private static final GsonComponentSerializer gson = GsonComponentSerializer.gson();

    public static String serializeSignText(Component[] lines, String color, boolean glowing) {
        JsonObject obj = new JsonObject();

        JsonArray front = new JsonArray();
        for (Component line : lines) {
            front.add(gson.serialize(line));
        }

        obj.add("front", front);
        obj.addProperty("color", color);
        obj.addProperty("glowing", glowing);

        return "SIGN:" + obj;
    }

    public static void deserializeSignText(Sign sign, String customId) {

        if (customId == null || !customId.startsWith("SIGN:")) return;

        String jsonPart = customId.substring(5);

        try {
            JsonObject obj = JsonParser.parseString(jsonPart).getAsJsonObject();

            JsonArray front = obj.getAsJsonArray("front");
            String color = obj.get("color").getAsString();
            boolean glowingFront = obj.get("glowing").getAsBoolean();

            for (int i = 0; i < 4; i++) {
                String text = front.get(i).getAsString();
                Component line = gson.deserialize(text);
                sign.getSide(Side.FRONT).line(i, line);
            }
            try {
                sign.setColor(DyeColor.valueOf(color));
            } catch (IllegalArgumentException ignored) {}

            sign.getSide(Side.FRONT).setGlowingText(glowingFront);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
