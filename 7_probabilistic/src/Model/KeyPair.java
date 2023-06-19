package Model;

import java.math.BigInteger;

public record KeyPair(PrivateKey privateKey, PublicKey publicKey) {

 	public BigInteger encrypt(String message) {
		return this.publicKey.encrypt(message);
    }

 	public BigInteger decrypt(String message) {
		return this.privateKey.decrypt(message);
    }
}
