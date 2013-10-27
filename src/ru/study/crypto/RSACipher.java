package ru.study.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 10/27/13
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RSACipher implements iCipher {
    private final static BigInteger one = new BigInteger("1");
    private final static SecureRandom random = new SecureRandom();
    /*Это для правильного хранения набора байтов в BigInteger-е, который для этого вообще-то не предназначен
    * К исходному тексту добавляется в начале это число, а при расшифровании - удаляется из результата
    * Если первый байт в списке - отрицательный, то BigInteger считает что и он тоже должен быть отрицательным
    * Что приводит к неправильной работе с utf-8 encoded строками
    * */
    private final static byte RSA_MAGIC_HEADER = 100;
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;
    private int maxMsgLen;

    // generate an N-bit (roughly) public and private key
    public RSACipher(int N) {
        maxMsgLen = (N / 8 - 1);
        BigInteger p = BigInteger.probablePrime(N / 2, random);
        BigInteger q = BigInteger.probablePrime(N / 2, random);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

        modulus = p.multiply(q);
        publicKey = new BigInteger("65537");     // common value in practice = 2^16 + 1
        privateKey = publicKey.modInverse(phi);

    }

    public static byte[] addTag(byte[] arr) {
        byte[] result = new byte[arr.length + 1];
        result[0] = RSA_MAGIC_HEADER;
        for (int i = 0; i < arr.length; i++)
            result[i + 1] = arr[i];
        return result;
    }

    public static byte[] removeTag(byte[] arr) {
        return Arrays.copyOfRange(arr, 1, arr.length);
    }

    @Override
    public byte[] encrypt(byte[] raw) throws TooBigPlainMessageLen {
        if (maxMsgLen < raw.length) {
            throw new TooBigPlainMessageLen(raw.length, maxMsgLen);
        }
        BigInteger res = new BigInteger(addTag(raw)).modPow(privateKey, modulus);
        return res.toByteArray();
    }

    @Override
    public byte[] decrypt(byte[] crypted) {
        return removeTag(new BigInteger(crypted).modPow(publicKey, modulus).toByteArray());
    }

    @Override
    public String getKeyString() {
        return String.format("RSA Keys: \n private: %s \n public: %s",
                privateKey, publicKey);
    }

    public static boolean eq(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            return false;
        } else {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] != b2[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            RSACipher r = new RSACipher(128);
            CryptoWrapper cw = new CryptoWrapper(r);
            byte[] PLAIN = new byte[]{1, -2};
            byte[] tagged = addTag(PLAIN);
            System.out.println("addTag: " + Arrays.toString(tagged));
            System.out.println("removeTag: " + Arrays.toString(removeTag(tagged)));
            System.out.println("Plain: " + Arrays.toString(PLAIN));
            byte[] crypted = cw.encrypt(PLAIN);
            System.out.println("Crypted: " + Arrays.toString(crypted));
            byte[] decrypted = cw.decrypt(crypted);
            if (!eq(PLAIN, decrypted)) {
                System.out.println("ERROR: " + Arrays.toString(PLAIN) + "=/=" + Arrays.toString(decrypted));
                System.exit(1);
            }
            System.out.println("PASSED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

