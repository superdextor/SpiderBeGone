package us.emdex.halloween.entity;

import us.emdex.halloween.Sprites;
import us.emdex.halloween.World;
import static us.emdex.halloween.Constants.PTS_PER_JUMP_SPIDER;

public class EntityJumpSpider extends EntitySpider
{
	public EntityJumpSpider(World world)
	{
		super(world);
	}
	
	@Override
	protected int step(int ticks)
	{
		if((ticks/15)%2==1)
		{
			super.step(ticks);
			if(ticks%15>5&&ticks%15<10) this.y++;
			return super.step(ticks);
		}
		return this.y;
	}
	
	@Override
	protected byte getPoints()
	{
		return PTS_PER_JUMP_SPIDER;
	}
	
	@Override
	protected Sprites getSprite(int ticks)
	{
		switch(this.state)
		{
			default: return (ticks/15)%2==1?Sprites.JUMP_SPIDER_LEAP:Sprites.JUMP_SPIDER;
			case SQUISHED: return Sprites.JUMP_SPIDER_SQUISHED;
			case POISONED: return Sprites.JUMP_SPIDER_POISONED;
		}
	}
}
