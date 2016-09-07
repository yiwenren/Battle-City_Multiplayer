import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg{
	
	int id;
	int x, y;
	Direction dir;
	TankClient tc;
	Direction ptDir;
	int msgType = Msg.TANK_MOVE_MSG;
	
	public TankMoveMsg(int id, int x, int y, Direction dir, Direction ptDir) {
		this.id = id;
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.ptDir = ptDir;
	}

	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
			dos.writeInt(ptDir.ordinal());
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

	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(id == tc.tk.ID){
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			Direction ptDir = Direction.values()[dis.readInt()];
			boolean exist = false;
			for(int i = 0; i < tc.tanks.size(); i++){
				Tank t = tc.tanks.get(i);
				if(t.ID == id){
					t.x = x;
					t.y = y;
					t.dir = dir;
					exist = true;
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
}
