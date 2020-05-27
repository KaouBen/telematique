import java.io.Serializable;
import java.net.InetAddress;

public class UDPPacket implements Serializable {

    private static final long serialVersionUID = 2622663736791338175L;

    private int request;
    private byte[] data = null;
    private int seq = 0;
    private int ack = 0;
    private int lastPacket = 0;
    private InetAddress toAddress;
    private InetAddress fromAddress;
    private int toPort;
    private int fromPort;

    final static public int PUTFILE = 0;
    final static public int GETFILE = 1;

    public UDPPacket(int putOrget, InetAddress fAddress, int fPort,InetAddress tAddress, int tPort) {
        this.request = putOrget;
        this.fromAddress = fAddress;
        this.fromPort = fPort;
        this.toAddress = tAddress;
        this.toPort = tPort;
    }

    public UDPPacket(int putOrget, InetAddress fAddress, int fPort,InetAddress tAddress, int tPort, int s, int a, int last, byte[] d ) {
        this.request = putOrget;
        this.fromAddress = fAddress;
        this.fromPort = fPort;
        this.toAddress = tAddress;
        this.toPort = tPort;
        this.seq = s;
        this.ack = a;
        this.lastPacket = last;
        this.data = d;
    }

    public boolean isGetRequest(){
        return (request == GETFILE);
    }

    public InetAddress getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(InetAddress fromAddress) {
        this.fromAddress = fromAddress;
    }

    public int getFromPort() {
        return fromPort;
    }

    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public int getRequest() {
        return request;
    }
    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public int getLastPacket() {
        return lastPacket;
    }

    public void setLastPacket(int lastPacket) {
        this.lastPacket = lastPacket;
    }


    public InetAddress getToAddress() {
        return toAddress;
    }
    public int getToPort() {
        return toPort;
    }

    @Override
    public String toString() {
        String output = "UDPPacket [request=" + request + ", seq=" + this.getSeq() + ", ack=" + this.getAck() + ", lastPacket=" + this.getLastPacket()
                + ", source=" + fromAddress + ", fromPort=" + fromPort + ", toAddress=" + toAddress + ", toPort=" + toPort;
        return output;
    }
}
