package us.emdex.halloween;

import static java.awt.Color.PINK;
import java.awt.Graphics2D;
import static us.emdex.halloween.Constants.FONT_PARTICLE;
import static us.emdex.halloween.Constants.COLOR_W;
import static us.emdex.halloween.Particle.ParticleType.*;

public class Particle
{
	private final ParticleType type;
	private final byte[] data;
	
	private int x, y;
	private byte life;
	
	private Particle(ParticleType type, byte... data)
	{
		this.type = type;
		this.data = data;
	}
	
	private Particle define(int x, int y, int life)
	{
		this.x = x;
		this.y = y;
		this.life = (byte)life;
		return this;
	}
	
	public boolean tickThendraw(TextureHandler t, Graphics2D g, int ticks)
	{
		switch(this.type)
		{
			default:
			{
				g.setColor(PINK);
				g.fillRect(this.x,this.y,8,8);
				break;
			}
			case TEXT:
			{
				g.setFont(FONT_PARTICLE);
				g.setColor(COLOR_W);
				String s = new String(this.data);
				int x = this.x;
				x -= g.getFontMetrics().stringWidth(s)/2;
				g.drawString(s, x, this.y);
				if(ticks%3==0) this.y--;
				break;
			}
		}
		
		return --this.life<0;
	}
	
	public static Particle createText(int x, int y, String s)
	{
		return new Particle(TEXT,s.getBytes()).define(x,y,15);
	}
	
	public boolean hiddenInSecret()
	{
		return this.type==TEXT;
	}
	
	protected static enum ParticleType
	{
		TEXT;
	}
}
