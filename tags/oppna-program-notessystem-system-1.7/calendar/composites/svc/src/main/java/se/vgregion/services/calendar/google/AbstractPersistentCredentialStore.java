package se.vgregion.services.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrik Bergström
 */
public abstract class AbstractPersistentCredentialStore implements CredentialStore, Serializable {

    protected HashMap<String, SerializableCredential> store = new HashMap<String, SerializableCredential>();

    abstract void updateFromSource();

    abstract void persist() throws IOException;

    private Lock lock = new ReentrantLock();

    @Override
    public boolean load(String userId, Credential credential) throws IOException {
        try {
            lock.lock();
            SerializableCredential serializableCredential = store.get(userId);

            if (serializableCredential != null) {
                serializableCredential.load(credential);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void store(String userId, Credential credential) throws IOException {
        try {
            lock.lock();
            SerializableCredential serializableCredential = new SerializableCredential(userId, credential);
            store.put(userId, serializableCredential);
            persist();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String userId, Credential credential) throws IOException {
        try {
            lock.lock();
            store.remove(userId);
            persist();
        } finally {
            lock.unlock();
        }
    }

    protected Lock getLock() {
        return lock;
    }

    protected static class SerializableCredential implements Serializable {

        private static final long serialVersionUID = -7005768278377859784L;

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
