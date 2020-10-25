package us.emdex.halloween.entity;

import us.emdex.halloween.World;

import static us.emdex.halloween.Application.HEIGHT;
import static us.emdex.halloween.Application.WIDTH;

import us.emdex.halloween.TextureHandler;
import static us.emdex.halloween.Sprites.SPRAY;

public class EntitySprayCan extends Entity
{
	public EntitySprayCan(World world)
	{
		super(world);
		this.width = 16;
		this.height = 32;
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		this.x = WIDTH/2-8;
		this.y = HEIGHT-58;
	}
	
	public void move(int x)
	{
		x += this.x;
		if(x<-8) x = -8;
		else if(x>WIDTH-8) x = WIDTH-8;
		this.x = x;
	}
	
	@Override
	public void draw(TextureHandler t, int ticks)
	{
		super.draw(t, ticks);
		t.drawSprite(SPRAY, this.x, this.y);
	}
}
