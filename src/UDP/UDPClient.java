package UDP;

import java.io.*;
import java.net.*;

public class UDPClient {
	
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress IPAdress;
    private int port;
    private byte[] sendData = new byte[1024];
    private byte[] receiveData = new byte[1024];
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    
    public UDPClient(String destinationAddr, int port) throws Exception {
        this.IPAdress = InetAddress.getByName(destinationAddr);
        this.port = port;
        socket = new DatagramSocket();
    }
    
    public void start() throws Exception {
    	String sentence = inFromUser.readLine();
    	while (true) {
            sendData = sentence.getBytes();
            packet = new DatagramPacket(sendData, sendData.length, IPAdress, port);
            
            this.socket.send(packet);
            System.out.println("packet sent");
        }
    }
    
    public static void main(String[] args) throws Exception {
    	UDPClient sender = new UDPClient("localhost", 50000);
        System.out.println("-- Client en cours d'execution sur " + InetAddress.getLocalHost() + " --");
        sender.start();
	}
    
    
}
