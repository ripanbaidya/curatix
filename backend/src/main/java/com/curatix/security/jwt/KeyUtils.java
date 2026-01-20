package com.curatix.security.jwt;

import com.curatix.common.constant.ErrorCode;
import com.curatix.security.exception.KeyLoadException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class KeyUtils {

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String KEY_ALGORITHM = "RSA";

    // Prevent instantiation
    private KeyUtils() {
    }

    /**
     * Load private key from the classpath
     */
    public static PrivateKey loadPrivateKey(@NonNull String pemPath, @NonNull ResourceLoader resourceLoader) {
        log.info("Loading private key from {}", pemPath);
        try {
            String keyContent = readKeyFromResource(pemPath, resourceLoader);
            String cleanedKey = stripPem(keyContent, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);

            byte[] decoded = Base64.getDecoder().decode(cleanedKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            PrivateKey privateKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(spec);

            log.info("Private key loaded Successfully.");
            return privateKey;

        } catch (IllegalArgumentException ex) {
            String errorMsg = "Invalid Base64 encoding in private key file: " + pemPath;
            log.error(errorMsg, ex);
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT, errorMsg, ex);
        } catch (Exception ex) {
            String errorMsg = "Failed to load private key from: " + pemPath;
            log.error(errorMsg, ex);
            throw new KeyLoadException(ErrorCode.PRIVATE_KEY_LOAD_FAILED, errorMsg, ex);
        }
    }

    /**
     * Load public key from the classpath
     */
    public static PublicKey loadPublicKey(@NonNull String pemPath, @NonNull ResourceLoader resourceLoader) {
        log.info("Loading public key from {}", pemPath);
        try {
            String keyContent = readKeyFromResource(pemPath, resourceLoader);
            String cleanedKey = stripPem(keyContent, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);

            byte[] decoded = Base64.getDecoder().decode(cleanedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            PublicKey publicKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(keySpec);

            log.info("Public key loaded successfully");
            return publicKey;

        } catch (IllegalArgumentException e) {
            String errorMsg = "Invalid Base64 encoding in public key file: " + pemPath;
            log.error(errorMsg, e);
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT, errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Failed to load public key from: " + pemPath;
            log.error(errorMsg, e);
            throw new KeyLoadException(ErrorCode.PUBLIC_KEY_LOAD_FAILED, errorMsg, e);
        }
    }

    /* Helper method */

    /**
     * Read key content from classpath resource
     *
     * @param location the resource path
     * @return the key content as string
     * @throws KeyLoadException if file not found or cannot be read
     */
    private static String readKeyFromResource(String location, ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource(location);
            if (!resource.exists()) {
                String errorMsg = "Key file not found in classpath: " + location;
                log.error(errorMsg);
                throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_FOUND, errorMsg);
            }
            if (!resource.isReadable()) {
                String errorMsg = "Key file is not readable: " + location;
                log.error(errorMsg);
                throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_READABLE, errorMsg);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes());
            }

        } catch (KeyLoadException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "Failed to read key file: " + location;
            log.error(errorMsg, e);
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_FOUND, errorMsg, e);
        }
    }

    /**
     * Strip PEM header and footer and remove whitespace
     */
    private static String stripPem(String content, String header, String footer) {
        return content.replace(header, "").replace(footer, "")
                .replaceAll("\\s", "");
    }
}
