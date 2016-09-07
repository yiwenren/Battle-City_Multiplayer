import java.io.*;
import java.net.*;

public class TankNewMsg implements Msg {
	
	Tank tank;
	TankClient tc;
	int msgType = Msg.TANK_NEW_MSG;
	
	public TankNewMsg(TankClient tc){
		this.tc = tc;
	}
	
	public TankNewMsg(Tank tank){
		this.tank = tank;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try{
			dos.writeInt(msgType);
			dos.writeInt(tank.ID);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.isGood());
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
			int id = dis.readInt();
			if(tc.tk.ID == id) {
				return;
			}
			
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
			boolean exist = false;
			for(int i = 0; i < tc.tanks.size(); i++){
				if(tc.tanks.get(i).ID == id){
					exist = true;
					break;
				}
			}
			//for the tankClient new later, add the tankClient generated before into this client's tanks
			if(!exist){
				TankNewMsg msg = new TankNewMsg(tc.tk);
				tc.nc.send(msg);
				Tank t = new Tank(x, y, good, dir, tc);
				t.ID = id;
				tc.tanks.add(t);
				System.out.println("id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-good:" + good);
			}

			
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
