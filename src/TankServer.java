import java.io.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class TankServer {
	
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6666;
	List<Client> clients = new ArrayList<Client>();
	private int ID = 100;
	
	
	public static void main(String args[]){
		
		new TankServer().start();
		
	}
	
	public void start(){
		
		new Thread(new UDPThread()).start();
		
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true){
			Socket s = null;
			try {
				s = ss.accept();			
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String ip = s.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(ip, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);
				
				System.out.println("address: IP: " + s.getInetAddress() + ":" + s.getPort() + " port: " + udpPort);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if(s != null){
					try {
						s.close();
						s = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				
			}
			
		}
		
		
	}
	
	
	private class Client{
		String IP;
		int udpPort;
		public Client(String iP, int udpPort) {
			this.IP = iP;
			this.udpPort = udpPort;
		}
		
		
	}
	
	//listen whether new client joins in 
	private class UDPThread implements Runnable{
		
		byte[] buf = new byte[1024];
		
		public void run(){	
			DatagramSocket ds = null;
			try{
				ds = new DatagramSocket(UDP_PORT);
			} catch(SocketException e){
				e.printStackTrace();
			}
System.out.println("UDP thread starts at port: " + UDP_PORT);
			while(ds != null){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try{
					ds.receive(dp);
System.out.println("a packet has been received!");
					for(int i = 0; i < clients.size(); i++){
						Client c = clients.get(i);

						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
						ds.send(dp);
System.out.println("a packet has sent for client: " + i);						
					}

				} catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}
	}
	
	

}
