package us.emdex.halloween.entity;

import us.emdex.halloween.TextureHandler;
import us.emdex.halloween.World;
import static us.emdex.halloween.Application.WIDTH;
import static us.emdex.halloween.Application.HEIGHT;

import us.emdex.halloween.Sprites;

public class EntityBat extends Entity
{
	private Entity target = null;
	
	public EntityBat(World world)
	{
		super(world);
		this.width = 16;
		this.height = 16;
	}
	
	@Override
	public void initialize()
	{
		this.y = HEIGHT-this.height;
		this.x = this.world.random.nextInt(Math.max(WIDTH/8-3,1))*8+8;
	}
	
	@Override
	public void tick(int ticks)
	{
		super.tick(ticks);
		
		if(ticks%2==0||this.target!=null)
		{
			this.y--;
			if(this.y<-this.height) this.state = EnumState.DEAD;
		}
		
		ai:
		if(this.target!=null)
		{
			if(this.target.y>this.y||this.target.state.isDead())
			{
				this.target = null;
			}
			else
			{
				if(this.x/2==this.target.x/2);
				else if(this.x<this.target.x) this.x+=2;
				else this.x-=2;
				
				if(this.target.isColliding(this))
				{
					this.target.processInteraction(false);
					this.target = null;
					break ai;
				}
			}
		}
		else if(ticks%3==0)
		{
			double d, d2 = 100;
			for(Entity e : this.world.getEntitiesIn(0, 0, WIDTH, this.y))
			{
				if(e.state.isDead()||e.y+10>this.y) continue;
				d = Math.sqrt(Math.pow(e.x-this.x, 2)+Math.pow(e.y-this.y, 2));
				if(d<d2)
				{
					this.target = e;
					d2 = d;
				}
			}
		}
	}
	
	@Override
	public void draw(TextureHandler t, int ticks)
	{
		super.draw(t,ticks);
		t.drawSprite(Sprites.BAT_FLYING,this.x,this.y,this.target==null?ticks:ticks*2);
	}
}
