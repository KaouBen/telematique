import java.io.*;
import java.net.DatagramPacket;

public class Serializer {

	public static Object deserialize(DatagramPacket packet) {

		ByteArrayInputStream bytes = new ByteArrayInputStream(packet.getData());
		ObjectInput in;
		try {
			in = new ObjectInputStream(bytes);
			UDPPacket ojb =  (UDPPacket) in.readObject();
			return ojb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static public byte[] serialize(Serializable Obj) {
		try
		{
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bytes);
			out.writeObject(Obj);
			out.close();
			byte[] byteObj= bytes.toByteArray();
			return byteObj;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}