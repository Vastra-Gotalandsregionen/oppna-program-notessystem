package se.vgregion.portal.calendar.util;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * @author Patrik Bergström
 */
public class EncodingUtil {

    public static String encodeToString(Serializable externalSources) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        try {
            oos.writeObject(externalSources);

            byte[] byteArray = baos.toByteArray();
            String encoded = Base64.encodeBase64String(byteArray);

            return encoded;
        } finally {
            oos.close();
            baos.close();
        }
    }


    public static <V> V decodeExternalSources(String externalSourcesEncoded) throws IOException, ClassNotFoundException {

        byte[] bytes = Base64.decodeBase64(externalSourcesEncoded);

        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);

            V decoded = (V) ois.readObject();

            return decoded;
        } finally {
            if (ois != null) {
                ois.close();
            }
            if (bais != null) {
                bais.close();
            }
        }
    }
}
