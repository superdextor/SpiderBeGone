package us.emdex.halloween.entity;

import us.emdex.halloween.TextureHandler;
import us.emdex.halloween.World;
import us.emdex.halloween.AudioHandler.Sounds;
import us.emdex.halloween.Particle;

import static us.emdex.halloween.Application.WIDTH;
import static us.emdex.halloween.Application.HEIGHT;
import static us.emdex.halloween.Constants.PTS_PER_SPIDER;

import us.emdex.halloween.Sprites;

public class EntitySpider extends Entity
{
	private final byte deathTime = -6;
	private byte step, delay;
	private boolean hasWeb;
	
	public EntitySpider(World world)
	{
		super(world);
		this.width = 16;
		this.height = 16;
	}

	@Override
	public void initialize()
	{
		super.initialize();
		this.x = this.world.random.nextInt(Math.max(WIDTH/8-3,1))*8+8;
		this.y = -15;
		this.step = this.world.getDifficulty().getStep();
		this.delay = this.world.getDifficulty().getDelay(this.world);
		this.hasWeb = this.world.random.nextInt(5)==0;
	}
	
	protected int step(int ticks)
	{
		return this.y+=this.step;
	}
	
	@Override
	public void tick(int ticks)
	{
		super.tick(ticks);
		if(ticks%this.delay==0)
		{
			if(this.step(ticks)>HEIGHT-24)
			{
				this.state = EnumState.DEAD;
				this.world.hud.removeLife();
			}
			else if(this.hasWeb&&this.y>24&&this.y<HEIGHT-32&&this.world.random.nextInt(40)==0)
			{
				this.hasWeb = false;
				this.world.spawnEntity(new EntityWeb(this.world).setPosition(this.x+4, this.y+4));
			}
		}
	}
	protected byte getPoints()
	{
		return PTS_PER_SPIDER;
	}
	
	@Override
	public boolean processInteraction(boolean mouse)
	{
		super.processInteraction(mouse);
		this.state = mouse?EnumState.SQUISHED:EnumState.POISONED;
		this.world.playSound(mouse?Sounds.SQUISH:Sounds.POISON);
		this.world.hud.addScore(this.getPoints());
		this.world.spawnParticle(Particle.createText(this.x+(this.width/2), this.y+(this.height/2), String.valueOf(this.getPoints())));
		this.world.hud.addKill();
		return true;
	}
	
	protected Sprites getSprite(int ticks)
	{
		switch(this.state)
		{
			default: return Sprites.SPIDER_WALK;
			case SQUISHED: return Sprites.SPIDER_SQUISHED;
			case POISONED: return Sprites.SPIDER_POISONED;
		}
	}
	
	@Override
	public void draw(TextureHandler t, int ticks)
	{
		super.draw(t, ticks);
		t.drawSprite(this.getSprite(ticks), this.x, this.y, this.life<0?this.life-this.deathTime:(ticks/this.delay)*this.step);
	}
	
	@Override
	public void onDeath()
	{
		this.life = this.deathTime;
	}
	
	@Override
	public boolean isEnemy()
	{
		return true;
	}
}
