import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MissileDeathMsg implements Msg {
	
	int msgType = Msg.MISSILE_DEATH_MSG;
	int id;
	int tankId;
	TankClient tc;
	
	public MissileDeathMsg(int  id, int tankId){
		this.id = id;
		this.tankId = tankId;
	}
	
	public MissileDeathMsg(TankClient tc){
		this.tc = tc;
	}
	

	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(tankId);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
			try {
				ds.send(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			int tankId = dis.readInt();

			
			for(int i = 0; i < tc.missiles.size(); i++){
				Missile m1 = tc.missiles.get(i);
				if(m1.tankId == tankId && id == m1.id){
					m1.setLive(false);
					tc.explodes.add(new Explode(m1.x, m1.y, tc));
				}
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
