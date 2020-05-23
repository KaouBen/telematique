package UDP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPServer {
	private int port;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] receiveData = new byte[1024];
	private byte[] sendData = new byte[1024];
	
	public UDPServer(int port) throws Exception {
        this.port = port;
        this.socket = new DatagramSocket(this.port);
    }
	
	public void start() throws Exception {
		
		System.out.println("---- Serveur en cours d'execution sur " + InetAddress.getLocalHost()+" ----");
		String sentence;
		
		while (true) {
			
			packet = new DatagramPacket(receiveData,receiveData.length);
			
			socket.receive(packet);
			
			sentence = new String(packet.getData()).trim();
			
			System.out.println("Message recu de "+packet.getAddress().getHostName()+"est :"+sentence);

		}
	}
	
	public static void main(String[] args) throws Exception {
		//Scanner sc= new Scanner(System.in);    
		//System.out.print("Enter qlqch ");  
		//String valeur = sc.next();  
		UDPServer client = new UDPServer(50000);
		client.start();
		//sc.close();
	}
}