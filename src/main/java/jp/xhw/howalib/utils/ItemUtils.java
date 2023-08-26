package jp.xhw.howalib.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemUtils {

    public static String toBase64(ItemStack itemStack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream output = new BukkitObjectOutputStream(outputStream);

            output.writeObject(itemStack);

            output.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (Exception e) {
            throw new IllegalStateException("シリアライズに失敗しました");
        }
    }

    public static ItemStack fromBase64(String data) throws IllegalArgumentException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream input = new BukkitObjectInputStream(inputStream);

            ItemStack itemStack = (ItemStack) input.readObject();

            input.close();
            return itemStack;

        } catch (Exception e) {
            throw new IllegalArgumentException("デシリアライズに失敗しました");
        }
    }

}
