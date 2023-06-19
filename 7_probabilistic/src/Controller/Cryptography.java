package Controller;

import java.math.BigInteger;
import java.util.Random;

import Model.KeyPair;
import Model.PrivateKey;
import Model.PublicKey;

public class Cryptography {

	private Cryptography() {
		throw new IllegalStateException("Utility class");
	}

	public static BigInteger generatePrime(int numberOfDigits, int seed) {
		final Random random = new Random(seed);
		final BigInteger lowerBound = BigInteger.TEN.pow(numberOfDigits - 1);
		final BigInteger upperBound = BigInteger.TEN.pow(numberOfDigits).subtract(BigInteger.ONE);

		BigInteger prime;
		do {
			prime = new BigInteger(upperBound.bitLength(), random);
		} while (prime.compareTo(lowerBound) < 0 || prime.compareTo(upperBound) > 0 || !prime.isProbablePrime(100));

		return prime;
	}

	public static BigInteger modularExponentiation(BigInteger base, BigInteger exponent, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            base = base.multiply(base).mod(modulus);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

	public static KeyPair generateRSAKeyPair(String pStr, String qStr, int seed) {
		final BigInteger p = new BigInteger(pStr);
		final BigInteger q = new BigInteger(qStr);

		final BigInteger n = p.multiply(q);

		// Compute φ(n) = (p - 1) * (q - 1)
		final BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

		final Random rng = new Random(seed);

		// Choose e such that 0 < e < φ(n) and e and φ(n) are coprime
		BigInteger e;
        do {
            e = new BigInteger(phi.bitLength() - 1, rng);
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !phi.gcd(e).equals(BigInteger.ONE));

		// Compute the modular multiplicative inverse of e modulo φ(n)
		final BigInteger d = e.modInverse(phi);

		return new KeyPair(new PrivateKey(d, n), new PublicKey(e, n));
	}
}
