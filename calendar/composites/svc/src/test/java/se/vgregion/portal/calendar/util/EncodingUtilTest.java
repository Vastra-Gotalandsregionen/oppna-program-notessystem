package se.vgregion.portal.calendar.util;

import org.junit.Test;
import se.vgregion.core.domain.calendar.ExternalSource;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrik Bergström
 */
public class EncodingUtilTest {

    @Test
    public void testEncodeAndDecode() throws Exception {
        ExternalSource source = new ExternalSource("key", "url");

        String s = EncodingUtil.encodeToString(source);

        System.out.println("Encoded: " + s);

        ExternalSource e = EncodingUtil.decodeExternalSources(s);

        assertEquals(source, e);

    }

}
