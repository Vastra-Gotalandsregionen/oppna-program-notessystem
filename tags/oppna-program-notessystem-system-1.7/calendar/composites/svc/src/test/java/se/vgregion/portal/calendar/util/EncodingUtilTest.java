package se.vgregion.portal.calendar.util;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrik Bergström
 */
public class EncodingUtilTest {

    @Test
    public void testEncodeAndDecode() throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        map.put("key", "url");

        String s = EncodingUtil.encodeToString((Serializable) map);

        System.out.println("Encoded: " + s);

        Map<String, String> e = EncodingUtil.decodeToObject(s);

        assertEquals(map, e);
    }

}
