package ru.study.crypto;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 10/27/13
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class TooBigPlainMessageLen extends Exception {
    private int max;
    private int fact;

    public TooBigPlainMessageLen(int fact, int max) {
        this.fact = fact;
        this.max = max;
    }

    @Override
    public String getMessage() {
    return String.format("plain message for encryption is too big(%d) " +
                "\n maximum allowed is: %d " +
                "(due key strength)",fact,max);
    }
}
