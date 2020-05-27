import java.io.*;
import java.net.*;
import java.util.*;

public class Connection implements Runnable{

    private DatagramSocket clientSocket =null;
    private DatagramPacket initpacket = null;
	private InetAddress address;
	private int port;

    public Connection(InetAddress toAddress, int toPort) {
        this.address = toAddress;
        this.port = toPort;
    }
    @Override
    public void run() {
        try {
            start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

	public void start() throws IOException {
        System.out.println("Entrez votre commande: put/get filename.ext");
        String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
        StringTokenizer tokens = new StringTokenizer(input);
        String userChoice =  tokens.nextToken();
        String fileName = tokens.nextToken();

        clientSocket = new DatagramSocket();

        switch (userChoice)
        {
            case "put":
                UDPPacket putPacket = new UDPPacket(UDPPacket.PUTFILE,InetAddress.getLocalHost(),clientSocket.getPort(),address,port);
                putPacket.setData(fileName.getBytes());

                Thread putThread = new Thread(new putFile(clientSocket, fileName));
                putThread.start();
                byte[] putData = Serializer.serialize(putPacket);
                initpacket = new DatagramPacket(putData,putData.length,putPacket.getToAddress(),putPacket.getToPort());
                clientSocket.send(initpacket);
                break;

            case "get":
                UDPPacket getPacket = new UDPPacket(UDPPacket.GETFILE,InetAddress.getLocalHost(),clientSocket.getPort(),address,port);
                getPacket.setData(fileName.getBytes());

                Thread getThread = new Thread(new getFile(clientSocket,fileName));
                getThread.start();
                byte[] getData = Serializer.serialize(getPacket);
                initpacket = new DatagramPacket(getData,getData.length,getPacket.getToAddress(),getPacket.getToPort());
                clientSocket.send(initpacket);
                break;

            default:
                System.out.println("Commande inconnue!");
                break;
        }
    }
}