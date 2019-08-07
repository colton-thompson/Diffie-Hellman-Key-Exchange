import java.util.Vector;
import java.math.BigInteger;
import java.security.SecureRandom;

import modExpPackage.modExp; // my function package
import findPrime.findPrime;
import globalVars.globalVars;
import primitiveRootFinder.primitiveRootFinder;
import javax.crypto.*;

public class elGamal {
	public static void main(String[] args) {
		
		primitiveRootFinder prf = new primitiveRootFinder();
		findPrime fp = new findPrime();
		modExp mE = new modExp();
		BigInteger prime, primitiveRoot, a, k, r, beta, t;
		BigInteger m = new BigInteger ("123456789012345678901234567890123456789012345678901234567890");
		
		
		Vector<BigInteger> test = prf.mainMethod();
		prime = test.get(0); primitiveRoot = test.get(1);
		//System.out.println("\n       prime: " + prime + "\nprimitveRoot: " + primitiveRoot);
		
		do {
			a = new BigInteger(globalVars.numbits, new SecureRandom());
			//System.out.println(randNum.equals(prime.subtract(globalVars.TWO)));
		} while (a.compareTo(prime.subtract(globalVars.TWO)) == -1);
		beta = mE.modularExp(primitiveRoot, a, prime);
		//System.out.println("     randNum: " + a); 
	
		do {
			k = new BigInteger(globalVars.numbits, new SecureRandom());
		} while (k.compareTo(prime.subtract(globalVars.TWO)) == -1);
		r = mE.modularExp(primitiveRoot, k, prime);
		//System.out.println("     randNum: " + k); 
		
		t = mE.modularExp(beta.multiply(m), k, prime);
		//t = (t.multiply(m)).mod(prime);
		System.out.println("\n" +t);
		
		//FIXME - i think modexp is breaking because of having a negative exponent
		// either fix here or make modexp more robust 
		BigInteger ciphertext = mE.modularExp(t, a.negate(),  prime);
		ciphertext = (ciphertext.multiply(r).mod(prime));
		System.out.println(ciphertext);
		
		System.exit(0);
	}
}