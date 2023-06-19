package Model;

import java.math.BigInteger;

import Controller.Cryptography;

public record PublicKey(BigInteger e, BigInteger n) {

 	public BigInteger encrypt(String message) {
        return Cryptography.modularExponentiation(new BigInteger(message), this.e, this.n);
    }
}
