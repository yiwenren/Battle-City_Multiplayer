import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Tank {
	
	public int ID;
	
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	public static Random r = new Random();
	
	private boolean live = true;
	

	private int step = r.nextInt(12) + 3;
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	TankClient tc;
	Direction ptDir = Direction.D;
	
	
	private boolean bL=false, bU=false, bR=false, bD=false; //stands for whether the four buttons have been pressed
	
	
	int x, y, oldX, oldY;
	
	private boolean good;
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	Direction dir = Direction.STOP;
	
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, Direction dir, TankClient tc){
		this(x, y, good);//call the other constructor
		this.tc = tc;
		this.dir = dir;
		
	}
	
	
	
	public void draw(Graphics g){
		if(!this.live) {
			tc.tanks.remove(this);
			return; //if the tank is not alive, then won't draw it.
		}
		
		g.drawString("ID:" + ID, x, y - 10);
		
		Color c = g.getColor();
		if(good) g.setColor(Color.red);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);

		//draw gun barrel
		switch(ptDir){
		case L: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x , y + Tank.WIDTH/2); 
			break;
		case LU: 
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x, y); 
			break;
		case U:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH/2, y); 
			break;
		case RU:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH, y); 
			break;
		case R:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH, y + Tank.WIDTH/2); 
			break;
		case RD:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH, y + Tank.WIDTH); 
			break;
		case D:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x + Tank.WIDTH/2, y + Tank.WIDTH); 
			break;
		case LD:
			g.drawLine(x + Tank.WIDTH/2, y + Tank.HEIGHT/2, x, y + Tank.WIDTH); 
			break;
		case STOP:
			break;
		}
		
		move();
	}
	
	public void move(){
		oldX = x;
		oldY = y;
		//control the moves of enemy tanks
		/*if(!good){
			if(step == 0){
				Direction[] dirs = Direction.values();
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
				step = r.nextInt(12)+3;
			}
			step--;
			
			if(r.nextInt(50)>46){
				this.fire();
			}
	
		}*/
		
		switch(dir){
		case L: 
			x -= XSPEED; 
			break;
		case LU: 
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		
		}
		
		if(x < 0) x = 0;
		if(y < 20) y = 20;
		if(x + Tank.WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.HEIGHT; 
		
		
		if(dir != Direction.STOP){
			ptDir = dir;
		}		
	}
	
	public void locateDirection(){
		
		Direction oldDir = this.dir;
		
		if(bL && !bU && !bR && !bD) dir = Direction.L;
		if(bL && bU && !bR && !bD) dir = Direction.LU;
		if(!bL && bU && !bR && !bD) dir = Direction.U;
		if(!bL && bU && bR && !bD) dir = Direction.RU;
		if(!bL && !bU && bR && !bD) dir = Direction.R;
		if(!bL && !bU && bR && bD) dir = Direction.RD;
		if(!bL && !bU && !bR && bD) dir = Direction.D;
		if(bL && !bU && !bR && bD) dir = Direction.LD;
		if(!bL && !bU && !bR && !bD) dir = Direction.STOP;
		
		if(oldDir != dir){
			TankMoveMsg msg = new TankMoveMsg(ID, x, y, dir, ptDir);
			tc.nc.send(msg);
		}
	}
	
	public void KeyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
		
		case KeyEvent.VK_LEFT: bL = true; break;
		case KeyEvent.VK_UP: bU = true; break;
		case KeyEvent.VK_RIGHT: bR = true; break;
		case KeyEvent.VK_DOWN: bD= true; break;
		
		}
		
		locateDirection(); //if press key button, the direction changes
	}
 

	public void KeyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_Z: {
			fire();
			break;
		}
		case KeyEvent.VK_LEFT: bL = false; break;
		case KeyEvent.VK_UP: bU = false; break;
		case KeyEvent.VK_RIGHT: bR = false; break;
		case KeyEvent.VK_DOWN: bD= false; break;
		
		}
		
		locateDirection(); //if release key button, the direction changes
	}

	public Missile fire(){
		if(!live) return null;
		int x = this.x + Tank.WIDTH/2 - Missile.WIDTH/2;
		int y = this.y + Tank.WIDTH/2 - Missile.HEIGHT/2;
		Missile m = new Missile(x, y, ptDir, good, true, this.ID, tc);
//System.out.println("missile tankId: " + m.tankId);
		tc.missiles.add(m);
		MissileNewMsg msg = new MissileNewMsg(m);
		tc.nc.send(msg);
		
		return m;
	}
	
	
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
	public void stays(){
		x = oldX;
		y = oldY;
	}
	
	
	public boolean hitsTank(java.util.List<Tank> tanks){
		for(int i = 0; i <tanks.size(); i++){
			Tank t = tanks.get(i);
			if(this.live && t.isLive() && this != t && this.getRect().intersects(t.getRect())){
				this.stays();
				t.stays(); 
				return true;
			}
		}
		return false;
	}

	
	
}
