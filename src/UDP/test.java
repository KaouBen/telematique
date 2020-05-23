package UDP;

import java.util.Scanner;

public class test {

	public static void main(String[] args) throws Exception {
		Scanner sc= new Scanner(System.in);    
		System.out.print("Enter qlqch ");  
		String valeur = sc.next();  
		UDPServer client = new UDPServer(Integer.parseInt(valeur));
		client.start();
		sc.close();
	}

}
