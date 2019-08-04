package primitiveRootFinder;

import java.math.*; // for BigInteger
import java.util.Random;
import java.util.Vector;
import java.util.Collections; // for Random and Vector
import java.security.SecureRandom;
import java.util.Timer; 
import java.util.TimerTask; 
import java.text.SimpleDateFormat;  
import java.util.Date; 
import java.util.concurrent.*; 

import modExpPackage.modExp; // my function package
import findPrime.findPrime;
import globalVars.globalVars;
import java.util.*;

//import javax.xml.ws.*;

 

public class primitiveRootFinder {	
	private static BigInteger ZERO = new BigInteger("0"); 
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	private static BigInteger THREE = new BigInteger("3");
		
	public static Vector<BigInteger> primeFactors(BigInteger n, BigInteger index, Vector<BigInteger> factorVec) 
	{ 
		findPrime p = new findPrime();
		
		//while (n%2==0)
		while (n.mod(TWO).equals(ZERO)) 
		{ 
			//System.out.print(2 + " "); 
			factorVec.add(TWO); //System.out.println("new factor is 2"); 
			n = n.divide(TWO); // n /= 2;
		} 
	
		//System.out.println("n value before logic: " + n);
		if (p.isPrime(n, globalVars.numMillerRabinIterations)) {
			//System.out.println(n + " is prime");
			factorVec.add(n);
		} else {
			//System.out.println(n + " is composite");
			while (!(n.mod(index)).equals(ZERO)) {
				//index = nextPrime(index);
				index = index.add(TWO);
				//System.out.println("index: " + index);
			}
			factorVec.add(index); //System.out.println("new factor is " + index);
			//System.out.println("next prime: " + nextPrime(index));
			n = n.divide(index);
			primeFactors(n, index, factorVec);
		}
 		//return vector of factors
		return factorVec;
	} //End primeFactors()
	
	public static Vector<BigInteger> maximaDivisors(Vector<BigInteger> factorVec, BigInteger p) {
		Vector<BigInteger> maximaDiv = new Vector<BigInteger>();
		//take a factor out and add result to maximaDiv vector
		for (int i = 0; i < factorVec.size(); i++) {
			//System.out.println("index " + i + ": " + p.divide(factorVec.get(i)));
			maximaDiv.add(p.divide(factorVec.get(i)));
		}
		return maximaDiv;
	} //End maximaDivisors()
	
	public static Boolean testPrimitiveRoot(Vector<BigInteger> maxDivs, BigInteger p, BigInteger num){
		modExp mE = new modExp();
		for (int i = 0; i < maxDivs.size(); i++) {
			// check that modExp does not equal 1
			//System.out.println("index " + i + ": " + mE.modularExp(num, maxDivs.get(i), p));
			if (mE.modularExp(num, maxDivs.get(i), p).equals(ONE)) {
				//System.out.println("not a primitive root");
				System.out.print(".");
				return true;
			}
		}
		return false;
	} //End testPrimitiveRoot()
	
	public static BigInteger findPrimitiveRoot(Vector<BigInteger> maxDivs, BigInteger p){
		BigInteger numRange = p.subtract(TWO);
		
		//Vector<BigInteger> randVec = new Vector<BigInteger>();
		//int iterations = globalVars.numMillerRabinIterations, 
		int numBits = globalVars.numbits;
		BigInteger randInt = ONE;
		int isSmaller = 0;
		
		// generate number at random
		// take prime^maximaDivisors mod p
		// if any result in 1 then find new number 

		do {
			randInt = new BigInteger(numBits, new SecureRandom());
			//System.out.println("test : " + 
			//call function to test if number passes modExp test
			isSmaller = (randInt.add(TWO)).compareTo(p);
			
		} while (isSmaller == 1 || testPrimitiveRoot(maxDivs, p, randInt)); //while num is not a primitive root

		return randInt; 
	} //End findPrimitiveRoot()
	
	//get random number in range of [1, p-2]
	//find factors of phi(n)
	
	public static Vector<BigInteger> primeFactorTimer (){
		findPrime p = new findPrime();
		Vector<BigInteger> returnVec = new Vector<BigInteger>();
		//Boolean flagged = false;
		final ExecutorService service = Executors.newSingleThreadExecutor();
			try {
				final Future<Vector<BigInteger>> f = service.submit(() -> {
					BigInteger n = p.getRand().subtract(ONE);
					//System.out.println("n: " + n);
					
					Vector<BigInteger> factorVec = new Vector<BigInteger>();
					factorVec = primeFactors(n, THREE, factorVec); 
					
					//Vector<BigInteger> returnVec = new Vector<BigInteger>();
					for (int i = 0; i < factorVec.size(); i++) {
						returnVec.add(factorVec.get(i));
					}
					return returnVec;
				});
			
			//System.out.println(f.get(1, TimeUnit.SECONDS));
			f.get(globalVars.timeEscape, TimeUnit.SECONDS);
			} catch (final TimeoutException e) {
				System.err.print(".");
			} catch (final Exception e) {
				throw new RuntimeException(e);
			} finally {
				service.shutdown();
			}
			return returnVec;
	}	

	public static Vector<BigInteger> mainMethod() 
	//public static void mainMethod(Vector<BigInteger> test)
	{ 		
		findPrime p = new findPrime();
		BigInteger n = ONE;
		Vector<BigInteger> returnVec = new Vector<BigInteger>();
		Vector<BigInteger> goodFactors = new Vector<BigInteger>();
		
		//if this loop times out it should break causing the outer loop to restart	
		System.out.print("Generating Primitive Root...");
		do {
			goodFactors.clear();
			//System.out.println("getting goodfactors..." + goodFactors);		
			goodFactors = primeFactorTimer();
			//System.out.println("got goodfactors... " + goodFactors);
		} while (goodFactors.isEmpty());	
		
		// get what n was
		// this seems like a bad way of doing this, but it's a creative fix
		for (BigInteger num : goodFactors) {
			n = n.multiply(num);
		}
		n = n.add(ONE);
		returnVec.add(n);
		//get maxima divisors 
		//reverse factorVec order
		Collections.reverse(goodFactors);
		Vector<BigInteger> maximas = maximaDivisors(goodFactors, n);
		//System.out.println("max div: " + maximas);
		BigInteger numRange = findPrimitiveRoot(maximas, n);
		returnVec.add(numRange);
		//System.out.println("prime:         " + n.add(ONE));
		//System.out.println("primitive root:" + numRange);
		
		return returnVec;
		//System.exit(0);
	}
	
	public static void main(String[] args) 
	{
		Vector<BigInteger> test = mainMethod();
		//System.out.println("\n       prime: " + test.get(0) + "\nprimitveRoot: " + test.get(1));
		System.exit(0);
	}
}