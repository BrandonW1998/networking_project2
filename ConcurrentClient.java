package concurrentSocketServer;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class ConcurrentClient {

	public static void main(String[] args) {
		String address;		//IP Address of server
		int command = 0;	//Command identifier
		int port;			//Port number to connect with
		int count = 0;		//Number of commands to run
		Thread[] threadArr = new Thread[100];	//Array of threads, 100 client maximum
		long startTimeNano;	//Time threads start (in Nanoseconds)
		long finishTimeNano;//Time threads finish (in Nanoseconds)
		double startTime;	//Time threads are started
		double finishTime;	//Time threads are finished
		double totalTime;	//Total time elapsed
		double avgTime;		//Average time elapsed
		
		try {
			//Scan IP Address
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("What is the IP Address of the server?");
			address = reader.readLine();
		
			//Scan port number
			System.out.println("What port would you like to use?");
			port = Integer.parseInt(reader.readLine());
			
			while (command != 7) {
				//Menu of commands
				//Scan command
				System.out.println("Which request would you like to perform? (Type the number)\n" +
						"1: Date & Time\n" +
						"2: Uptime\n" +
						"3: Memory Use\n" +
						"4: NetStat\n" +
						"5: Current Users\n" +
						"6: Running Processes\n" +
						"7: Quit Program");
				command = Integer.parseInt(reader.readLine());
				
				//Quit exception
				if (command >= 1 && command <= 6 ) {
					//Scan count
					System.out.println("How many client requests should be made?");
					count = Integer.parseInt(reader.readLine());
				}
				else if (command == 7)
					break;
				else {
					System.out.println("Error: Invalid command number\n");
					continue;
				}
			
				//Build threads
				for(int i = 0; i < count; i++) {
					Thread thread = new Thread(new ConcurrentClientThreading(address, port, command));
					threadArr[i] = thread;
				}
				
				//Start timer
				startTimeNano = System.nanoTime();
				
				//Run threads
				for (int i = 0; i < count; i++)
					threadArr[i].start();
				
				//Join threads
				for (int i = 0; i < count; i++)
					threadArr[i].join();
				
				//Stop timer
				finishTimeNano = System.nanoTime();
				
				System.out.println("\nAll threads have been completed\n" +
						"-----------------------------------");
				
				//Calculate time elapsed
				startTime = startTimeNano / 1000000;
				finishTime = finishTimeNano / 1000000;
				
				totalTime = finishTime - startTime;
				avgTime = totalTime / count;
				System.out.println("Total time elapsed: " + totalTime + "ms\n"
						+ "Average time (per thread): " + avgTime + "ms\n\n");
			}
			
			//Exit message
			System.out.println("Thank you! Goodbye!");
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

//Multi-threading class
class ConcurrentClientThreading implements Runnable {
	String address;
	int port;
	int command;
	String response;
	
	//Constructor
	//Receives IP Address, port number, command number
	public ConcurrentClientThreading(String address, int port, int command) {
		this.address = address;
		this.port = port;
		this.command = command;
	}
	
	//Runs thread
	//Executed when start() is called on thread
	@Override
	public void run() {
		try {
			//Establish connection with server
			Socket socket = new Socket(address, port);
		
			//Establish communication paths with server
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
			InputStream input = socket.getInputStream();
			Scanner reader = new Scanner(input);
			
			//Relay command
			writer.println(command);
			
			//Read server response(s)
			while(reader.hasNextLine())
				System.out.println(reader.nextLine());
			
			socket.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
