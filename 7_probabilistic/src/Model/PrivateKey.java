package Model;

import java.math.BigInteger;

import Controller.Cryptography;

public record PrivateKey(BigInteger d, BigInteger n) {

	public BigInteger decrypt(String encrypted) {
        return Cryptography.modularExponentiation(new BigInteger(encrypted), this.d, this.n);
    }
}
