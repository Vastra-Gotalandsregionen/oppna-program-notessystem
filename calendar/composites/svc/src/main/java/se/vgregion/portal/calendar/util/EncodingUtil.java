package se.vgregion.portal.calendar.util;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * Class for transforming between strings and objects.
 *
 * @author Patrik Bergström
 */
public final class EncodingUtil {

    private EncodingUtil() {
        // Utility class with only static methods.
    }

    /**
     * Encode/serialize a serializable object into a string which can be decoded/deserialized later.
     *
     * @param serializable the object to be serialized
     * @return the encoded string
     * @throws IOException if serialization fails
     */
    public static String encodeToString(Serializable serializable) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        try {
            oos.writeObject(serializable);

            byte[] byteArray = baos.toByteArray();
            String encoded = Base64.encodeBase64String(byteArray);

            return encoded;
        } finally {
            oos.close();
            baos.close();
        }
    }


    /**
     * Decode/deserialize a string into an object of type V.
     *
     * @param stringEncodedObject a string which can be deserialized into an object
     * @param <V> the type of the object to be returned
     * @return the decoded/deserialized object
     * @throws IOException IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static <V> V decodeToObject(String stringEncodedObject) throws IOException, ClassNotFoundException {

        byte[] bytes = Base64.decodeBase64(stringEncodedObject);

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
