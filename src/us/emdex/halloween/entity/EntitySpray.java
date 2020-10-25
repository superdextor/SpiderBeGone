package us.emdex.halloween.entity;

import us.emdex.halloween.Sprites;
import us.emdex.halloween.TextureHandler;
import us.emdex.halloween.World;
import static us.emdex.halloween.Application.WIDTH;

public class EntitySpray extends Entity
{
	private final byte xFactor;
	private short life;
	
	public EntitySpray(World world)
	{
		this(-1,world);
	}
	
	public EntitySpray(int xf, World world)
	{
		super(world);
		this.x = world.player.x+4;
		this.y = world.player.y;
		this.xFactor = (byte)xf;
		this.life = (short)(world.getDifficulty().useUberCharge()?20:15);
	}
	
	@Override
	public void tick(int ticks)
	{
		super.tick(ticks);
		if(this.y<-8||this.x<-8||this.x>WIDTH||--this.life<0) this.state = EnumState.DEAD;
		
		this.y-=4;
		if(this.xFactor!=0&&(Math.abs(this.xFactor)>20||ticks%Math.abs(this.xFactor)!=0))
		{
			if(Math.abs(this.xFactor)>20) this.x+=(Math.abs(this.xFactor)-20)*(this.xFactor<0?-1:1);
			else this.x+=this.xFactor<0?-1:1;
		}
		
		for(Entity e : this.world.getEntitiesIn(this.x, this.y, 8, 8))
		{
			if(e.processInteraction(false))
				this.state = EnumState.DEAD;
		}
	}
	
	@Override
	public void draw(TextureHandler t, int ticks)
	{
		super.draw(t, ticks);
		if(this.life>5||this.life%2==0) t.drawSprite(Sprites.POISON, this.x, this.y);
	}
}
