package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Patrik Bergström
 */
public class FilePersistentCredentialStoreTest {

    @Test
    @Ignore
    public void testWriteToFile() throws IOException {
        File file = new File("/tmp/store.dat");

        file.delete();
        file.createNewFile();

        FilePersistentCredentialStore store1 = FilePersistentCredentialStore
                .createSerializableCredentialStore(file);

        Credential credential = new GoogleCredential();

        store1.store("myUserId", credential);

        store1.persist();

        FilePersistentCredentialStore store2 = FilePersistentCredentialStore
                .createSerializableCredentialStore(file);

        assertTrue(store2.load("myUserId", credential));

    }
}
