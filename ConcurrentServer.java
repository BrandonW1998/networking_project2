package concurrentSocketServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.lang.management.*;

public class ConcurrentServer {

	public static void main(String[]args) {
		
		//error msg if program launched wrong
		if (args.length < 1 || args.length > 1) {
			System.err.println("\nUsage: java IterativeServer <port number>\n");
			return;
		}
		
		//creating port number
		int portNum = Integer.parseInt(args[0]);
		
		//shows user that server is waiting for clients
		System.out.println("\nAwaiting clients...");
		
		//establishing connection with a thread, completing the requested action, and ending connection
		try(ServerSocket server = new ServerSocket(portNum, 100)){
			
			while(true) {
				//accepting client
				Socket client = server.accept();
				System.out.println("New Client Connected.");
				
				Thread thread = new Thread(new ConcurrentServerThreading(client));
				thread.start();
			}
			
		}
		catch(IOException ex) {
			System.out.println("Server Exception: " + ex.getMessage());
            ex.printStackTrace();
		}
		
	}
	
}

class ConcurrentServerThreading implements Runnable{
	Socket client;

	public ConcurrentServerThreading(Socket client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
			//creating tools for communication between client and server
			InputStream input = client.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = client.getOutputStream();
			PrintWriter writer = new PrintWriter(output,true);
			
			//initializing choice value
			int choice;
			
			//reading choice from thread
			choice = Integer.parseInt(reader.readLine());
			
			switch(choice) {
			
			//if client wants date and time
			case 1:
				System.out.println("Client Request: Date and Time on Server");
				
				//get date/time and send to client
				String date = new Date().toString();
				writer.println(date);
				
				//display date/time on server
//				System.out.println("[" + date + "]");
				break;
				
			//if client wants uptime	
			case 2:
				System.out.println("Client Request: Uptime (how long server has been running since last bootup)");
				
				RuntimeMXBean rtBean = ManagementFactory.getRuntimeMXBean();
				long uptime = TimeUnit.MILLISECONDS.toSeconds(rtBean.getUptime());
				
				//send uptime to client
				writer.println("Uptime " + uptime + "s");
				
				//display uptime on server
//				System.out.println("[Uptime: " + uptime +"s" + "]");
				break;
				
			//if client wants memory usage
			case 3:
				System.out.println("Client Request: Memory Usage on Server");
				
				Runtime runtime = Runtime.getRuntime();
				long memoryInUse = runtime.totalMemory() - runtime.freeMemory();
				
				//send memory use to client
				writer.println("Total Memory on Server: " + runtime.totalMemory() + "Bytes");
				writer.println("Memory in Use:" + memoryInUse + "Bytes");
				//display memory use on server
//				System.out.println("[Total Memory on Server: " + runtime.totalMemory() + "Bytes]");
//				System.out.println("[Memory in Use:" + memoryInUse + "Bytes]");
				break;
			
			//if client wants netstat
			case 4:
				System.out.println("Client Request: Netstat (network connections on server)");
				
				//command to display all sockets
				String socketsCommand = "netstat --all";
				//process to execute external system command
				Process netStatProcess = Runtime.getRuntime().exec(socketsCommand);
				//read the output from the command
				BufferedReader netstatReader = new BufferedReader(new InputStreamReader(netStatProcess.getInputStream()));
				String line;
				while((line = netstatReader.readLine()) != null) {
					//send to client
					writer.println(line);
					//display on server
//					System.out.println(line);
				}
				break;
			
			//if client wants current users
			case 5:
				System.out.println("Client Request: Current Users on Server");
				
				//command to display users currently connected to server
				String usersCommand = "who -H";
				//process to execute an external system command
				Process listUsersProcess = Runtime.getRuntime().exec(usersCommand);
				//read the output from the command 
				BufferedReader listUsersReader = new BufferedReader(new InputStreamReader(listUsersProcess.getInputStream()));
				String listUsers;
				while ((listUsers = listUsersReader.readLine()) != null) {
					//send to client
					writer.println(listUsers);
					//display on server
//					System.out.println(listUsers);
				}
				break;
				
			//if client wants running processes
			case 6:
				System.out.println("Client Request: Running Processes on Server");
				
				String processCommand = "ps -ef";
				//process to execute an external system command
				Process pstatusProcess = Runtime.getRuntime().exec(processCommand);
				//read the output from the command
				BufferedReader pstatusReader = new BufferedReader(new InputStreamReader(pstatusProcess.getInputStream()));
				String pstatus;
				while((pstatus = pstatusReader.readLine()) != null) {
					//send to client
					writer.println(pstatus);
					//display on server
//					System.out.println(pstatus);
				}
				break;
				
			//client quits
			default:
				System.out.println("Client Terminated Connection.\n");
				break;
			}
			
			client.close();
//			System.out.println("\nRequest Complete.");
//			System.out.println("Client Disconnected.\n");
//			System.out.println("----------------------");
		}
		catch(IOException ex) {
			System.out.println("Server Exception: " + ex.getMessage());
            ex.printStackTrace();
		}
	}
}