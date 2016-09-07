import java.io.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetClient {
	
	private int udpPort;
	
	String IP;
	
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	private TankClient tc;
	
	

	DatagramSocket ds = null;
	
	public NetClient(TankClient tc){
		this.tc = tc;
		
	}
	
	public void connect(String IP, int port){
		
		this.IP = IP;
		
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		Socket s = null;
		try {
			s = new Socket(IP, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.tk.ID = id;
			if(id % 2 == 0) tc.tk.setGood(true);
			else tc.tk.setGood(false);
			
			
System.out.println("conected to server! and my ID is: " + tc.tk.ID);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		
		TankNewMsg msg = new TankNewMsg(tc.tk);
		send(msg);
		
		new Thread(new UDPRecvThread()).start();
	}
	
	public void send(Msg msg){
		msg.send(ds, IP, TankServer.UDP_PORT);
	}
	
	private class UDPRecvThread implements Runnable{
		
		byte[] buf = new byte[1024];
		
		public void run(){		
			while(ds != null){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
//System.out.println("a packet received from server!");
					parse(dp);

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("error");
				}
				
			}
		}
		
		private void parse(DatagramPacket dp) {
			Msg msg = null;
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(msgType){
			case Msg.TANK_NEW_MSG:
				msg = new TankNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg = new TankMoveMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_NEW_MSG:
				msg = new MissileNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_DEATH_MSG:
				msg = new TankDeathMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_DEATH_MSG:
				msg = new MissileDeathMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
				
			}
			
			
		}

		
	}
	
	
	

}
