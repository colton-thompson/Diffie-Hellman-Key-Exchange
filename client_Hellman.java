// Java implementation for a client 

import java.io.*; 
import java.net.*; 
import java.util.*; 
import java.math.BigInteger;
import java.security.SecureRandom; // for better randoms

import globalVars.globalVars;
import modExpPackage.modExp; // my function package
import findPrime.findPrime;
import primitiveRootFinder.primitiveRootFinder;

//import java.math.*;
//import javax.lang.model.type.*;

// Client class 
public class client_Hellman 
{
	
	private static BigInteger ZERO = new BigInteger("0"); 
	private static BigInteger ONE = new BigInteger("1");
	private static BigInteger TWO = new BigInteger("2");
	private static BigInteger THREE = new BigInteger("3");
	private static BigInteger FOUR = new BigInteger("4");
	//private static BigInteger TEN = new BigInteger("10"); //used for testing purposes
		
	public static void main(String[] args) throws IOException 
	{ 
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			// getting localhost ip 
			// should just be 127.0.0.1
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			// establish the connection with server port 5056 
			Socket s = new Socket(ip, globalVars.portNum); 
	
			// obtaining input and out streams 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	
			// the following loop performs the exchange of 
			// information between client and client handler 
			
			while (true) 
			{ 
				//dos.writeUTF("test");
				System.out.println(dis.readUTF()); 
				String tosend = scn.nextLine(); // nget user input
				dos.writeUTF(tosend); 
				
				// write a switch statement here for the tosend statements that will expect multiple lines to be returned
				// proof of concept works 
				String received = "";
				switch (tosend) {
					case "Prime":
					case "prime":
						BigInteger xRand;
						primitiveRootFinder prf = new primitiveRootFinder();
						// index 0: prime
						// index 1: primitive root 
						Vector<BigInteger> getNums = prf.mainMethod();
						//System.out.println("\n       prime: " + getNums.get(0) + "\nprimitveRoot: " + getNums.get(1));
						dos.writeUTF(getNums.get(0).toString());
						dos.writeUTF(getNums.get(1).toString());
						
						findPrime p = new findPrime();
						//add 2 to xRand so that range max is acceptable. max = p - 2
						do 
							xRand = p.getRand();
						while ((xRand.add(globalVars.TWO)).equals(getNums.get(0)));
						
						modExp expo = new modExp();
						BigInteger clientHalf = expo.modularExp(getNums.get(1), xRand, getNums.get(0)); //System.out.println("clientHalf : " + clientHalf);
						dos.writeUTF(clientHalf.toString());					
						
						//received = dis.readUTF(); 
						//System.out.println(received);
						
						//received = dis.readUTF();
						BigInteger yRand = new BigInteger(dis.readUTF());// System.out.println("yRand: " + yRand);
						
						BigInteger result = expo.modularExp(getNums.get(1), yRand.multiply(clientHalf), getNums.get(0)); 
						System.out.println("\nresult: " + result);
						
						BigInteger servResult = new BigInteger(dis.readUTF()); System.out.println("result: " + servResult);
						
						if (servResult.compareTo(result) == 0) 
							System.out.println("Key generated successfully.");
						else
							System.out.println("ERROR: something went wrong with key generation");
							
						break;
					
					case "Exit":
					case "exit":
						System.out.println("Closing this connection: " + s); 
						s.close(); 
						System.out.println("Connection closed"); 
						break; 
						
					default:
						received = dis.readUTF(); 
						System.out.println(received); 
						break;
				}
			
						
				// If client sends exit, close this connection 
				// and then break from the while loop 
				if(tosend.equals("Exit") || tosend.equals("exit")) { 
					break; 
				} 
				
				// printing date or time as requested by client 
				// this will be defualt for the switch statement 
				//String received = dis.readUTF(); 
				//System.out.println(received); 
			} 
			
			// closing resources 
			scn.close(); 
			dis.close(); 
			dos.close(); 
			System.exit(0);
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
} 
