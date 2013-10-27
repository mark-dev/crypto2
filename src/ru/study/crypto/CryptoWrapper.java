package ru.study.crypto;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 10/14/13
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CryptoWrapper {
    private iCipher cipher;

    public CryptoWrapper(iCipher cipher) {
        this.cipher = cipher;
    }

    public byte[] encrypt(byte[] b) throws Exception
    {
        byte[] res = cipher.encrypt(b);
        if (res.length > 30 || b.length > 30) {
            System.out.println(String.format("encrypt(%s ...(%d) ) -> %s ... [%d]",
                    Arrays.toString(Arrays.copyOfRange(b, 1, 10)),b.length,
                    Arrays.toString(Arrays.copyOfRange(res, 1, 10)),res.length));
        } else {
            System.out.println(String.format("encrypt(%s) -> %s", Arrays.toString(b), Arrays.toString(res)));
        }
        return res;
    }

    public byte[] decrypt(byte[] b) {
        byte[] res = cipher.decrypt(b);
        if (res.length > 30 || b.length > 30) {
            System.out.println(String.format("decrypt(%s ...(%d) ) -> %s ... [%d]",
                    Arrays.toString(Arrays.copyOfRange(b, 1, 10)),b.length,
                    Arrays.toString(Arrays.copyOfRange(res, 1, 10)),res.length));
        } else {
            System.out.println(String.format("decrypt(%s) -> %s", Arrays.toString(b), Arrays.toString(res)));
        }
        return res;
    }


    public byte[] decrypt(File f) throws IOException {
        byte[] b = readBytesFromFile(f);
        System.out.println("Readed bytes: " + Arrays.toString(b));
        return cipher.decrypt(b);
    }

    public String toHex(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public byte[] toBytes(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public String getKey() {
        return cipher.getKeyString();
    }

    public byte[] readBytesFromFile(File f) throws IOException {
        byte[] data = new byte[(int) f.length()];
        try {
            new FileInputStream(f).read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
