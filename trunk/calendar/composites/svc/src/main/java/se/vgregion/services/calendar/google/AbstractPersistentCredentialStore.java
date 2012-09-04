package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;

import java.io.*;
import java.util.HashMap;

/**
 * @author Patrik Bergström
 */
public abstract class AbstractPersistentCredentialStore implements CredentialStore, Serializable {

    protected HashMap<String, SerializableCredential> store = new HashMap<String, SerializableCredential>();

    abstract void updateFromSource();

    abstract void persist() throws IOException;

    @Override
    public synchronized boolean load(String userId, Credential credential) throws IOException {
        SerializableCredential serializableCredential = store.get(userId);

        if (serializableCredential != null) {
            serializableCredential.load(credential);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void store(String userId, Credential credential) throws IOException {
        SerializableCredential serializableCredential = new SerializableCredential(userId, credential);
        store.put(userId, serializableCredential);
        persist();
    }

    @Override
    public synchronized void delete(String userId, Credential credential) throws IOException {
        store.remove(userId);
        persist();
    }

    protected static class SerializableCredential implements Serializable {

        private String userId;
        private String accessToken;
        private String refreshToken;
        private Long expirationTimeMillis;

        public SerializableCredential(String userId, Credential credential) {
            this.userId = userId;
            this.accessToken = credential.getAccessToken();
            this.refreshToken = credential.getRefreshToken();
            this.expirationTimeMillis = credential.getExpirationTimeMilliseconds();
        }

        public void load(Credential credential) {
            credential.setAccessToken(accessToken);
            credential.setRefreshToken(refreshToken);
            credential.setExpirationTimeMilliseconds(expirationTimeMillis);
        }
    }
}
