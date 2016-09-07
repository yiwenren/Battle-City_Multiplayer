import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;



public class TankClient extends Frame{
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	Tank tk = new Tank(100, 150, true, Direction.STOP, this);
	List<Tank> tanks = new ArrayList<Tank>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	
	
	
	int x = 50;
	int y = 50;
	Image offScreenImage = null;
	
	NetClient nc = new NetClient(this);
	
	ConnDialog dialog = new ConnDialog();
	
	@Override
	public void paint(Graphics g) {
		
		g.drawString("missiles count: " + missiles.size(), 500, 60);
		g.drawString("explode count: " + explodes.size(), 500, 80);
		g.drawString("tanks count: " + tanks.size(), 500, 100);
		
		
		
		//missile
		for(int i = 0; i < missiles.size(); i++){
			Missile m = missiles.get(i);
			//m.hitTanks(tanks);
			if(m.hitTank(tk)){
				TankDeathMsg msg = new TankDeathMsg(tk);
				this.nc.send(msg);
				MissileDeathMsg msgMissileDie = new MissileDeathMsg(m.id, m.tankId);
				
				this.nc.send(msgMissileDie);
			};
			m.draw(g);
			//another kind of removing the missile out of bound
			//if(!m.isLive()) missiles.remove(m);
			//else m.draw(g);	
		}
		
		for(int i = 0; i < explodes.size(); i++){
			explodes.get(i).draw(g);
		}
		
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.draw(g);
		}
		
		//tank
		tk.draw(g);
	
	}
	

	@Override
	public void update(Graphics g) {
		if(offScreenImage == null){
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);	
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.green);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);//draw at offScreen image
		g.drawImage(offScreenImage, 0, 0, null);//draw at front image
		
		
		
	}





	public void launchFrame(){
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);
		setLocation(400, 300);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		this.setResizable(false);
		this.setBackground(Color.green);
		
		this.addKeyListener(new KeyMonitor());
		setVisible(true);
		
		new Thread(new PaintThread()).start();;
		
	}
	
	private class KeyMonitor extends KeyAdapter{

		@Override
		public void keyReleased(KeyEvent e) {
			
			tk.KeyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int k = e.getKeyCode();
			if(k == KeyEvent.VK_C){
				dialog.setVisible(true);
			} else{
				tk.KeyPressed(e);
			}
			tk.KeyPressed(e);
			
		}
		
	}
	
	private class PaintThread implements Runnable{
		
		public void run(){
			while(true){
				repaint();
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class ConnDialog extends Dialog{
		
		Button b = new Button("OK");
		TextField ipTf = new TextField("127.0.0.1", 12);
		TextField portTf = new TextField("" + TankServer.TCP_PORT, 4);
		TextField udpTf = new TextField("2223", 4);
		
		public ConnDialog(){
			super(TankClient.this, true); //true: model dialog
			this.setLayout(new FlowLayout());
			this.add(new Label("IP: "));
			this.add(ipTf);
			this.add(new Label("Port: "));
			this.add(portTf);
			this.add(new Label("My Udp Port: "));
			this.add(udpTf);
			this.add(b);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter(){

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
					
				}	
			});
			
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					String IP = ipTf.getText().trim();
					int port = Integer.parseInt(portTf.getText().trim());
					int udpPort = Integer.parseInt(udpTf.getText().trim());
					nc.setUdpPort(udpPort);
					nc.connect(IP, port);
					setVisible(false);
				}
			});
			
			
		}
	}
	
	public static void main(String args[]){
		TankClient tc = new TankClient();
		tc.launchFrame();
		
	}
}