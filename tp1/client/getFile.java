import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class getFile implements Runnable{
    private UDPPacket connectionPacket;
    private DatagramSocket connectionSocket = null;
    private String newFile;
    private int seq = 0;
    private int ack = 0;
    private int lastPacket = 0;

    public getFile(DatagramSocket cSocket, String fileName) {
        this.connectionSocket = cSocket;
        newFile = fileName;
    }

    @Override
    public void run() {
        start();
    }

    public void start(){
        try {
            System.out.println("Nouvelle connection sur le port " + connectionSocket.getPort());
            byte[] buffer = new byte[1500];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            //Obtenir les informations du server
            connectionSocket.receive(receivedPacket);
            connectionPacket = (UDPPacket)Serializer.deserialize(receivedPacket);
            connectionPacket.setFromAddress(receivedPacket.getAddress());
            connectionPacket.setFromPort(receivedPacket.getPort());
            //Premier packet pour confirmation de connection
            UDPPacket confirmPacket = new UDPPacket(connectionPacket.getRequest(),connectionSocket.getInetAddress(),connectionSocket.getPort(),connectionPacket.getFromAddress(),connectionPacket.getFromPort(), 1, 1, 0, new byte[1024]);
            send(confirmPacket);
            System.out.println("Envoie du premier packet de connection.");

            //Écrire les données des packets réçus dans le fichier
            BufferedOutputStream fileStream = new BufferedOutputStream(new FileOutputStream(newFile,true));
            int expectedSeq = 1;
            int ackReturn=0;
            while (true){
                connectionSocket.receive(receivedPacket);
                UDPPacket recievedData = (UDPPacket) Serializer.deserialize(receivedPacket);
                System.out.println("Reception d'un nouveau packet avec seq = "+ recievedData.getSeq());

                UDPPacket receivedACK = new UDPPacket(connectionPacket.getRequest(),connectionSocket.getInetAddress(),connectionSocket.getPort(),connectionPacket.getFromAddress(),connectionPacket.getFromPort(), expectedSeq, ackReturn, 0, new byte[1024]);

                //Écrire les données dans l'ordre, il faut avoir le bon packet
                if (recievedData.getSeq()==expectedSeq)
                {
                    fileStream.write(recievedData.getData());

                    //Incrémenter le seq
                    ackReturn = expectedSeq;
                    if(recievedData.getLastPacket() == 0)
                        expectedSeq +=recievedData.getData().length;
                }

                //Retourner le ack
                send(receivedACK);
                System.out.println("Renvoie du ack = " + receivedACK.getAck());

                //Terminer le traitement parce que le dernier packet a été reçu
                if(recievedData.getLastPacket() == 1 && recievedData.getSeq() == expectedSeq )
                {
                    //Envoyer le dernier ack
                    UDPPacket lastAck = new UDPPacket(connectionPacket.getRequest(),connectionSocket.getInetAddress(),connectionSocket.getPort(),connectionPacket.getFromAddress(),connectionPacket.getFromPort(), expectedSeq, ackReturn, 1, new byte[1024]);
                    send(lastAck);

                    fileStream.close();
                    System.out.println("Le dernier packet a été reçu. Le traitement du thread est terminé.");
                    connectionSocket.close();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Le traitement du thread est terminé.");
            Thread.currentThread().interrupt();
        }
    }
    private void send(UDPPacket packet) {
        try {
            System.out.println("Envoie d'un packet.");
            byte[] packetData = Serializer.serialize(packet);
            DatagramPacket packetToSend = new DatagramPacket(packetData,packetData.length,packet.getToAddress(),packet.getToPort());
            connectionSocket.send(packetToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
