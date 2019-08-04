package modExpPackage;

import java.math.*; // for BigInteger
import java.io.*; 	// for file handling
import java.util.*; // for Random and Vector

public class modExp {	
	private static BigInteger ZERO = new BigInteger("0"); 
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	
	public static BigInteger modularExp(BigInteger base, BigInteger exponent, BigInteger modulus) {
		BigInteger result = ONE;
		//exponent.compareTo(ZERO) --> (exponent > 0)
		//will return 1 if greater, 0 if equal, -1 if less than
		while (exponent.compareTo(ZERO) > 0) {
				//exp % 2 == 1
			if ((exponent.mod(TWO)).equals(ONE)) {
				//result = (result * base) % modulus
				result = ((result.multiply(base))).mod(modulus);
			}
			//dividing by 2 is same as 1 bit shift (>>)
			//exponent = exponent.divide(TWO); 
			exponent = exponent.shiftRight(1);
			base = ((base.multiply(base))).mod(modulus); 
		} 
		return result; //System.out.println("result: " +  result);
	} //End modularExp()
	
	public static void fileWrite(String fileName, BigInteger bignum) throws IOException {
		//create writer object, write to file determined by fileName var
		//write bignum to the file, then close file
		PrintWriter writer = new PrintWriter(fileName);
		writer.println(bignum); 
		writer.close();
	} //End fileWrite()
	
	public static Vector<BigInteger> fileRead(String fileName) throws IOException {
		BigInteger bigIntFromFile;
		String line = "";
		Vector<BigInteger> file = new Vector<BigInteger>();		
		//how to read from a file 
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		//get first line, if null the file is empty 
		line = reader.readLine();
		if (line != null) {
			do {
				//takes line string and makes it a BigInteger
				bigIntFromFile = new BigInteger(line);
				file.add(bigIntFromFile);
				//update line variable 
				line = reader.readLine();
			} while (line != null);

		} else { 
			//System.out.println("Error: File is empty.");
			file.add(ONE.negate());
		}
		reader.close();
		return file;
	} //End fileRead()
	
	public void success(String n) {        
		System.out.println(n + " written successfully...");
	} //End success()
	
}