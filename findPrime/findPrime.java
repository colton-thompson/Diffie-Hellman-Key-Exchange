package findPrime;

import java.math.*; // for BigInteger
import java.io.*; 	// for file handling
import java.util.*; // for Random and Vector
import java.security.SecureRandom; // for better randoms

import modExpPackage.modExp; // my function package
import globalVars.globalVars;

public class findPrime 
{
	private static BigInteger ZERO = new BigInteger("0"); 
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	private static BigInteger THREE = new BigInteger("3");
	private static BigInteger FOUR = new BigInteger("4");
	//private static BigInteger TEN = new BigInteger("10"); //used for testing purposes
	
	static boolean millerRabinTest(BigInteger d, BigInteger n) { 
		modExp mrt = new modExp();
		// Pick a random number in [2..n-2] 	
		//System.out.println("numbits: " + (n.subtract(TWO)).bitLength());
		BigInteger a = new BigInteger(n.subtract(TWO).bitLength(), new SecureRandom());
		a = a.add(TWO); //System.out.println("a: " + a);
		
		// Compute a^d % n  
		BigInteger x = mrt.modularExp(a, d, n); //System.out.println("x: " + x);
		if (x.equals(ONE) || ((x.equals(n.subtract(ONE)))))
			return true;
		
		//square x repeatedly while:
		//	d is not n - 1
		//	x^2 mod n != 1
		//	x^2 mod n != n - 1
		while (!(d.equals(n.subtract(ONE)))) {
			x = (x.multiply(x)).mod(n); //x = (x * x) % n; 
			d = d.multiply(TWO); 	//d *= 2; 
			
			if (x.equals(ONE)) //(x == 1) 
				return false; 
				
			if (x.equals(n.subtract(ONE))) //(x == n - 1) 
				return true; 
		} 
		return false; // Return composite 
	} //End millerRabinTest() 
	
	// false if n is composite, true if n is probably prime 
	// input k is number of iterations, more iterations more accurate (1/4^k)
	public static boolean isPrime(BigInteger num, int numIterations) { 
		// Corner cases 
		//if (n <= 1 || n == 4) 
		if (num.compareTo(ONE) == -1 || num.equals(FOUR))
			return false; 
		//if (n <= 3) 
		if (num.compareTo(THREE) == -1)
			return true; 

		//setup d:
		//	- subtract one to make it even
		//	- while d (mod 2) = 0, divide a two out to set up n - 1 = d * 2^r
		BigInteger d = num.subtract(ONE);	// d = n - 1; 
		while (d.mod(TWO).compareTo(ZERO) == 0) { //(d % 2) == 0
			d = d.divide(TWO);
		}

		// Iterate while i is less than numIterations 
		for (int i = 0; i < numIterations; i++) {
			if (!millerRabinTest(d, num)) {
				//Miller-Rabin Test found factor, therefore composite
				return false; 
			}
		}
		//passed all the tests, it most likely prime (safe to assume)
		return true; 
	} //End isPrime()
	
	public static BigInteger getRand() {
		//Vector<BigInteger> randVec = new Vector<BigInteger>();
		int iterations = globalVars.numMillerRabinIterations, numBits = globalVars.numbits, numDigits;
		BigInteger randBigInt = ONE;
		
		// generate number at random
		// test if generated number is prime
		// if it is prime, end loop and add number to vector 
		// else generate new random until we get a large prime
		for (int i = 0; i < 1; i++) {
			do {
				// add the i value to gaurentee difference of 10^95
				randBigInt = new BigInteger(numBits + i, new SecureRandom());
				//count the number of digits in the generated prime
				numDigits = randBigInt.toString().length(); //System.out.println("numDigts " + i + ": " + numDigits);
			
			//do this while randBigInt is not prime or has less than 100 decimal digits	
			} while (numDigits < 25 || !(isPrime(randBigInt, iterations)) );
			//randVec.add(randBigInt);
		}
			//System.out.println("Chance on not prime: 1/4^" + iterations);
			/*
			//used for testing that difference is greater than 10^95
			BigInteger test1 = randVec.get(1); BigInteger test2 = randVec.get(0); BigInteger test = (test1.subtract(test2));
			System.out.println("size test: " + test + "\t\tis larger?: " + test.compareTo(TEN.pow(95)));
			*/
		return randBigInt; //returns vector with p and q
	} //End getRand()
	
	
	public static void main (String[] args) 
	{ 
		BigInteger primeRand = getRand();
		System.out.println("prime: " + primeRand);
		System.out.println("p has " + primeRand.bitLength() + " bits");
		System.out.println("p has " + primeRand.toString().length() + " digits");
	} 
}
