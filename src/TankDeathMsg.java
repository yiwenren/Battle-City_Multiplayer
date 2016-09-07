import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankDeathMsg implements Msg {
	
	int msgType = TANK_DEATH_MSG;
	int tankId;
	Tank tk;
	TankClient tc;
	
	public TankDeathMsg(Tank tk){
		this.tk = tk;
	}
	
	public TankDeathMsg(TankClient tc){
		this.tc = tc;
	}
	

	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tk.ID);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			int tankId = dis.readInt();
			if(tankId == tc.tk.ID){
				return;
			}
			for(int i = 0; i < tc.tanks.size(); i++){
				Tank t = tc.tanks.get(i);
				if(t.ID == tankId){
					t.setLive(false);
					//tc.tanks.remove(t);
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
