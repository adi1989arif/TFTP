/**
 * Client.java
 * 
 * @author Adiba Arif
 */

import java.net.*;  
import java.util.Scanner;
import java.net.UnknownHostException;
import java.io.*;  

public class Client {  
	public static void main(String args[]) throws Exception {  
		DatagramSocket ClientSocket = new DatagramSocket();  
		Scanner sc=new Scanner(System.in);
		String str;
		String[] x=null;
		InetAddress IPAddress = null;
		boolean flag = false;
		int ByteCalculator=0;

		do {
			System.out.print("tftp>");
			str=sc.nextLine();
			x= str.split(" ");
			switch (x[0]) {
			case "connect":
				if (x.length>1) {
					try{
						IPAddress = InetAddress.getByName(x[1]);
						System.out.println(IPAddress);
						flag=true;
					}
					catch (Exception UnknownHostException) {
						System.out.println(x[1]+" :Unknown Host");
					}
				} else {
					System.out.println ("Provide Host Name to connect");
				}
				break;

			/** 
			 * implements get command
			 */
			case "get":
				if(x.length>1 && flag){
					Packet NewPacketSend = new Packet(x[1], "octet");
					NewPacketSend.MakePacket_Send();
					DatagramPacket RRQ_send= new DatagramPacket(NewPacketSend.setData, (NewPacketSend.setData).length, IPAddress, 69); 
					ClientSocket.send(RRQ_send);

					DatagramPacket Receive;
					byte check;
					try{
					do {
						byte[] ReceiveData = new byte[516];
						Receive= new DatagramPacket(ReceiveData, ReceiveData.length); 
						
						//to declare timeout
						int timeout=25000;
						ClientSocket.setSoTimeout(timeout);
						ClientSocket.receive(Receive);
						NewPacketSend.Packet_Break(ReceiveData, Receive.getLength());
						ByteCalculator= ByteCalculator+(Receive.getLength()-4);
						check=ReceiveData[1];
						
						NewPacketSend.MakePacket_ACK();
						DatagramPacket ACK= new DatagramPacket(NewPacketSend.setACK, (NewPacketSend.setACK).length, IPAddress, Receive.getPort()); 
						ClientSocket.send(ACK);
					}
					while(!((Receive.getLength()-4)<512));
					if (!(check==Packet.PKT_Error))
						System.out.println("Total bytes: "+ByteCalculator);
					ByteCalculator=0;
					NewPacketSend.close();
					}
					catch (Exception setSoTimeout){
						System.out.println("Socket Timeout Exception");
					}
				}
				else {
					System.out.println("First connect or provide a filename");
				}
				break;

			/**
			 * put method
			 */
			case "put":
				System.out.println("put");
				break;

			/**
			 * quit method. Implements quit command
			 */
			case "quit":
				ClientSocket.close();
				break;

			/**
			 * prints the tftp client commands
			 */
			case "?":
				System.out.println("Commands may be abbreviated. Commands are:");
				System.out.println("connect: connect to remote tftp");
				System.out.println("put: send file");
				System.out.println("get: receive file");
				System.out.println("quit: exit tftp");
				System.out.println("?: print help information");
				break;

			default:
				System.out.println("Invalid command");
				break;
			}
		}
		while(!(x[0].equals("quit"))); 
	}  
}  
