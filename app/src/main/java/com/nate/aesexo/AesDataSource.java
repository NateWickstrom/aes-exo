package com.nate.aesexo;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DataSourceInputStream;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.util.Assertions;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A {@link DataSource} that decrypts data read from an upstream source.
 */
final class AesDataSource implements DataSource {

    private final DataSource upstream;
    private final byte[] encryptionKey;
    private final byte[] encryptionIv;

    private CipherInputStream cipherInputStream;

    /**
     * @param upstream      The upstream {@link DataSource}.
     * @param encryptionKey The encryption key.
     * @param encryptionIv  The encryption initialization vector.
     */
    public AesDataSource(DataSource upstream, byte[] encryptionKey, byte[] encryptionIv) {
        this.upstream = upstream;
        this.encryptionKey = encryptionKey;
        this.encryptionIv = encryptionIv;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        Key cipherKey = new SecretKeySpec(encryptionKey, "AES");
        AlgorithmParameterSpec cipherIV = new IvParameterSpec(encryptionIv);

        try {
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, cipherIV);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        cipherInputStream = new CipherInputStream(
                new DataSourceInputStream(upstream, dataSpec), cipher);

        return C.LENGTH_UNBOUNDED;
    }

    @Override
    public void close() throws IOException {
        cipherInputStream = null;
        upstream.close();
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        Assertions.checkState(cipherInputStream != null);
        int bytesRead = cipherInputStream.read(buffer, offset, readLength);
        if (bytesRead < 0) {
            return -1;
        }
        return bytesRead;
    }

}
