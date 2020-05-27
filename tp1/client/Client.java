import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) {
        try {
            Thread interfaceClient = new Thread( new Connection(InetAddress.getLocalHost(),6780));
            interfaceClient.start();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
	}
}
