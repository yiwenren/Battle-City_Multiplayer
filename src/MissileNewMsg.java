import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Msg{
	Missile m;
	int msgType = MISSILE_NEW_MSG;
	TankClient tc;
	
	
	public MissileNewMsg(Missile m) {
		this.m = m;
	}
	
	public MissileNewMsg(TankClient tc) {
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeInt(msgType);
			dos.writeInt(m.tankId);
			dos.writeInt(m.x);
			dos.writeInt(m.y);
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.isGood());
			dos.writeBoolean(m.isLive());
		} catch(IOException e){
			e.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort) );
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	

	public void parse(DataInputStream dis) {
		try {
			int tankId = dis.readInt();
			if(tc.tk.ID == tankId) {
				return;
			}
			
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			boolean live = dis.readBoolean();

			Missile missile = new Missile(x, y, dir, good, live, tankId, tc);
			tc.missiles.add(missile);
			//for the tankClient new later, add the tankClient generated before into this client's tanks

	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	
	

}
