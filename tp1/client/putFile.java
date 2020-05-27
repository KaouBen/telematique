import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class putFile implements Runnable{
    private UDPPacket connectionPacket;
    private DatagramSocket connectionSocket;
    private String newFile;
    private int firstPacket = 0;
    private int seq = 0;
    private int ack = 0;
    private int lastPacket = 0;
    private Hashtable<Integer, UDPPacket> packetList = new Hashtable<Integer,UDPPacket>();
    private Timer windowTimer = null;


    public putFile(DatagramSocket connPacket, String fileName) {
        this.connectionSocket = connPacket;
        newFile = fileName;
    }
    @Override
    public void run() {
        start();
    }

    public void start() {
        try {
            //connectionSocket = new DatagramSocket();

            System.out.println("Nouvelle connection sur le port " + connectionSocket.getPort());
            byte[] buffer = new byte[1500];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            //Obtenir les informations du server
            connectionSocket.receive(receivedPacket);
            connectionPacket = (UDPPacket) Serializer.deserialize(receivedPacket);
            connectionPacket.setFromAddress(receivedPacket.getAddress());
            connectionPacket.setFromPort(receivedPacket.getPort());

            //Premier packet pour confirmation de connection
            seq = 1;
            ack = 1;
            firstPacket =1;
            UDPPacket confirmPacket = new UDPPacket(connectionPacket.getRequest(),connectionSocket.getInetAddress(),connectionSocket.getPort(),connectionPacket.getFromAddress(),connectionPacket.getFromPort(), seq, ack, lastPacket, new byte[1024]);
            send(confirmPacket);
            System.out.println("Envoie du premier packet de connection.");

            //Boucler jusqu'à temps que l'on recoit la confirmation
            while(true){
                connectionSocket.receive(receivedPacket);
                UDPPacket firstPacket = (UDPPacket) Serializer.deserialize(receivedPacket);
                if(firstPacket.getSeq() >= 1){
                    System.out.println("Connection établie.");
                    break;
                }
            }
            //Lire le fichier et envoyer les données lentement
            windowTimer = new Timer();
            windowTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    readFile();
                    sendPackets();
                }
            }, 0, 500);

            //Envoyer les données du fichier
            while(true) {
                connectionSocket.receive(receivedPacket);
                UDPPacket receivedAck = (UDPPacket) Serializer.deserialize(receivedPacket);
                retirerAck(receivedAck);
                System.out.println("Reception du ack = " + receivedAck.getAck());

                //Terminer le traitement parce que le dernier packet a été reçu
                if(receivedAck.getLastPacket() == 1){
                    windowTimer.cancel();
                    System.out.println("Le dernier packet a été envoyé. Le traitement du thread est terminé.");
                    connectionSocket.close();
                    Thread.currentThread().interrupt();
                    break;
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

    private synchronized void sendPackets(){
        //Envoie des packets de la packetList
        for (UDPPacket packet : packetList.values()) {
            send(packet);
        }
    }
    private synchronized void readFile(){
        //Lit le fichier et enmagasine les packets dans une liste
        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(newFile));
            byte[] buffer = new byte[1024];
            reader.skip(seq-1);
            while(reader.read(buffer,0,buffer.length) != -1 && packetList.size() < 4  ){

                //La lastPacket du fichier a été atteint
                if( reader.available() <= 0 ){
                    System.out.println("La fin du fichier a été atteint, fermeture du thread dans 30sec.");

                    lastPacket = 1;
                    //Fermer le thread dans 30 sec afin de terminer le traitement
                    Timer endTimer = new Timer();
                    endTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            connectionSocket.close();
                            Thread.currentThread().interrupt();
                            windowTimer.cancel();
                        }
                    }, 30000);
                }
                UDPPacket dataPacket = new UDPPacket(connectionPacket.getRequest(),connectionSocket.getInetAddress(),connectionSocket.getPort(),connectionPacket.getFromAddress(),connectionPacket.getFromPort(), seq, ack, lastPacket, buffer);

                packetList.put(seq, dataPacket);
                seq += buffer.length;

                buffer = null;
                buffer = new byte[1024];
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void retirerAck(UDPPacket packet) {
        //Retirer le ack s'il a été reçu
        if(packet.getAck() == firstPacket){
            this.packetList.remove(packet.getAck());
            System.out.println("Retrait du ack = " + packet.getAck());
            firstPacket += 1024;
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
