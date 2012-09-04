package se.vgregion.services.calendar.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Patrik Bergström
 */
public class FilePersistentCredentialStore extends AbstractPersistentCredentialStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePersistentCredentialStore.class);
    private static final File DEFAULT_CREDENTIAL_STORE_FOLDER = new File(System.getProperty("user.home")
            + "/.gCalStore");
    private static final File DEFAULT_FILE_STORE = new File(DEFAULT_CREDENTIAL_STORE_FOLDER, "store.dat");

    private File fileStore;

    private FilePersistentCredentialStore(File fileStore) {
        this.fileStore = fileStore;
    }

    public static FilePersistentCredentialStore createSerializableCredentialStore() {
        return createSerializableCredentialStore(DEFAULT_FILE_STORE);
    }

    public static FilePersistentCredentialStore createSerializableCredentialStore(File fileStore) {
        try {
            if (!fileStore.exists()) {
                fileStore.createNewFile();
                FilePersistentCredentialStore credentialStore = new FilePersistentCredentialStore(fileStore);
                credentialStore.persist();
                return credentialStore;
            } else {
                FilePersistentCredentialStore credentialStore = new FilePersistentCredentialStore(fileStore);
                credentialStore.updateFromSource();
                return credentialStore;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void updateFromSource() {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;

        try {
            if (!fileStore.exists()) {
                fileStore.createNewFile();
                // If we create it now there won't be anything stored in it, so return.
                return;
            }

            fis = new FileInputStream(fileStore);
            if (fis.available() < 1) {
                // Load nothing
                return;
            }
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);

            FilePersistentCredentialStore filePersistentCredentialStore = null;

            try {
                filePersistentCredentialStore = (FilePersistentCredentialStore) ois.readObject();
                this.store = filePersistentCredentialStore.store;
            } catch (ClassNotFoundException e) {
                // File is corrupt. Start over.
                String absolutePath = fileStore.getAbsolutePath();
                ois.close();
                bis.close();
                fis.close();
                ois = null;
                bis = null;
                fis = null;
                fileStore.renameTo(new File(absolutePath + "." + System.currentTimeMillis()));
                fileStore.createNewFile();
            } catch (InvalidClassException e) {
                // File is corrupt. Start over.
                String absolutePath = fileStore.getAbsolutePath();
                ois.close();
                bis.close();
                fis.close();
                ois = null;
                bis = null;
                fis = null;
                fileStore.renameTo(new File(absolutePath + "." + System.currentTimeMillis()));
                fileStore.createNewFile();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    void persist() throws IOException {
        ObjectOutputStream oos = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fileStore);
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
