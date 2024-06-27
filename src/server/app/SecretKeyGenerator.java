package server.app;

import java.security.SecureRandom;

//Use to generate secret key
public class SecretKeyGenerator {

    private final String SECRET_KEY = generateSecretKey(); // Replace with your own secret key
    private final String HEADER_STRING = "Authorization";


    public String generateSecretKey() {
        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);

        String Key = bytesToHex(bytes);
        System.out.println("Generated Secret Key (256-bit): " + Key);

        return Key;
    }

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public String getHeaderString() {
        return HEADER_STRING;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
