import java.awt.*;
import java.util.List;

public class Missile {
	public static int num = 1;
	
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	
	public static final int WIDTH =10;
	public static final int HEIGHT = 10;
	
	public static int ID = 1;
	
	int x, y;
	Direction dir;
	int id;
	
	private boolean live = true; //show or not show this missile
	
	private boolean good;
	
	private TankClient tc;
	
	int tankId;
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}
	
	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
	
	

	public Missile(int x, int y, Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.id = this.ID++;
	}
	
	public Missile(int x, int y, Direction dir, boolean good, boolean live, int tankId, TankClient tc) {
		this.x = x;   
		this.y = y;
		this.dir = dir;
		this.good = good;
		this.live = live;
		this.tankId = tankId;
		this.tc = tc;
	}
	
	
	public Missile(int x, int y, boolean good, Direction dir, TankClient tc){
		this(x,y,dir);
		this.good = good;
		this.tc = tc;
	}
	
	public void draw(Graphics g){
		if(!this.live) tc.missiles.remove(this);
		
		
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
		
		
	}

	private void move() {
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
		}
		
		//judge whether to show the missile
		if(x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT){
			live = false;
			tc.missiles.remove(this); //not showing this missile if it is out of bound
		}
		
	}
	
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
	public boolean hitTank(Tank t){
		if(this.isLive() && this.getRect().intersects(t.getRect()) && t.isLive() && this.good != t.isGood()){
			t.setLive(false);
			this.setLive(false);
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
				
			return true;
		}
		return false;
	}
	
	public boolean hitTanks(List<Tank> tanks){
		for(int i = 0; i< tc.tanks.size(); i++){
			if(hitTank(tanks.get(i))){
				return true;
			}
		}
		return false;
	}
	

	

}
