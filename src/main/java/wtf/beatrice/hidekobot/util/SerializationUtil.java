package wtf.beatrice.hidekobot.util;

import org.apache.commons.lang3.SerializationException;

import java.io.*;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class SerializationUtil
{

    public static String serializeBase64(List dataList) {

        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream so = new ObjectOutputStream(bo)) {
            so.writeObject(dataList);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        }
        catch (IOException ignored) {}
        return null;
    }

    public static LinkedList deserializeBase64(String dataStr) {

        byte[] b = Base64.getDecoder().decode(dataStr);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si;
        try {
            si = new ObjectInputStream(bi);
            return LinkedList.class.cast(si.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error during deserialization", e);
        }
    }
}
