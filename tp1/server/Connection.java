import java.io.IOException;
import java.net.*;

public class Connection implements Runnable{

    private DatagramSocket serverSocket = null;
    private int serverPort = 0;
    private byte[] buffer = new byte[1024];
    private String fileName;

    public Connection(int port){
        serverPort = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(serverPort);

            System.out.println("Démarrage du serveur : " + InetAddress.getLocalHost() + "::" + serverPort);
            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivedPacket);

                UDPPacket packetData = (UDPPacket) Serializer.deserialize(receivedPacket);
                System.out.println("Un nouveau packet a été reçu.");
                fileName = new String((packetData.getData()));

                packetData.setFromAddress(receivedPacket.getAddress());
                packetData.setFromPort(receivedPacket.getPort());

                if (packetData.isGetRequest()) {
                    System.out.println("Il faut envoyer le fichier " + fileName+ " au client.");
                    Thread putThread = new Thread(new putFile(packetData, fileName));
                    putThread.start();
                } else {
                    System.out.println("Il faut copier le fichier " + fileName+ " du client.");
                    Thread getThread = new Thread(new getFile(packetData,fileName));
                    getThread.start();
                }
            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
