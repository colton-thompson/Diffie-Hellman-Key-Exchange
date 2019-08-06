// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 

import java.io.*; 
import java.text.*; 
import java.util.*; 	//for vector
import java.net.*; 
import java.math.BigInteger;

import findPrime.findPrime;
import globalVars.globalVars;
import modExpPackage.modExp; // my function package

// Server class 
public class server_Diffie 
{ 
	public static void main(String[] args) throws IOException 
	{ 
		// server is listening on port 5056 
		ServerSocket servSoc = new ServerSocket(globalVars.portNum); 
		
		// running infinite loop for getting 
		// client request 
		while (true) 
		{ 
			Socket s = null; 
			
			try
			{ 
				// socket object to receive incoming client requests 
				s = servSoc.accept(); 
				
				System.out.println("A new client is connected: " + s); 
				
				// obtaining input and out streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				System.out.println("Assigning new thread for this client"); 

				// create a new thread object 
				Thread t = new ClientHandler(s, dis, dos); 

				// Invoking the start() method 
				t.start(); 
				
			} 
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
		} 
	} 
} 

// ClientHandler class 
class ClientHandler extends Thread 
{ 
	DateFormat fordate = new SimpleDateFormat("MM/dd/yyyy"); 
	DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 
	BigInteger n = new BigInteger("34234213412423452354234524523452345635675894647200"); 
	
	
	// Constructor 
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
	} 

	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
		BigInteger result = globalVars.ONE.negate();
		BigInteger prime, primitiveRoot, clientHalf, serverHalf;
		
		while (true) 
		{ 
			try { 				
				// Ask user what he wants 
				dos.writeUTF("What do you want? [Date | Time]..\n"+ "Type Exit to terminate connection."); 
				
				// receive the answer from client 
				received = dis.readUTF(); 
				
				if(received.equals("Exit") || received.equals("exit")) 
				{ 
					System.out.println("Client " + this.s + " sends exit..."); 
					System.out.println("Closing this connection."); 
					this.s.close(); 
					System.out.println("Connection closed."); 
					break; 
				} 
				
				// creating Date object 
				Date date = new Date(); 
				
				// write on output stream based on the 
				// answer from the client 
				switch (received) { 
				
					case "Date" : 
					case "date" :
						toreturn = fordate.format(date); 
						dos.writeUTF(toreturn); 
						break; 
						
					case "Time" : 
					case "time" :
						toreturn = fortime.format(date); 
						dos.writeUTF(toreturn); 
						break; 
					
					case "Prime" :
					case "prime" :
						//get prime
						//received = dis.readUTF();
						prime = new BigInteger(dis.readUTF()); //System.out.println("   rec prime: " + prime);
						
						//received = dis.readUTF();
						primitiveRoot = new BigInteger(dis.readUTF()); //System.out.println("rec primRoot: " + primitiveRoot);
						//get primitive root
						
						received = dis.readUTF();
						clientHalf = new BigInteger(received);// System.out.println("clientHalf: " + clientRand);
						//dos.writeUTF("\nreceived the pair");
						
						BigInteger yRand;
						findPrime p = new findPrime();
						//add 2 to xRand so that range max is acceptable. max = p - 2
						do 
							yRand = p.getRand();
						while ((yRand.add(globalVars.TWO)).equals(prime));
					
						//compute server side secret						
						modExp expo = new modExp();
						serverHalf = expo.modularExp(primitiveRoot, yRand, prime);
						dos.writeUTF(serverHalf.toString());
						
						result = expo.modularExp(primitiveRoot, clientHalf.multiply(yRand), prime);
						System.out.println("result: " + result);
						
						break;
					
					
					case "Key" :
					case "key" :
		
						dos.writeUTF(result.toString());
						break;
						
					default: 
						dos.writeUTF("Invalid input"); 
						break; 
				} 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 
		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 