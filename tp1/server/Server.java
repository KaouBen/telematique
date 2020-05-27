public class Server {

	public static void main(String[] args) {
		Thread serverThread = new Thread( new  Connection(6780));
		serverThread.run();
	}
}