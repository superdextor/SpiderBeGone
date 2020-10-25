package us.emdex.halloween.entity;

import us.emdex.halloween.TextureHandler;
import us.emdex.halloween.World;

public abstract class Entity
{
	public final World world;
	
	public int x, y;
	public int width = -1;
	public int height = -1;
	public EnumState state = EnumState.ALIVE;
	public byte life = 0;
	
	public Entity(World world)
	{
		this.world = world;
	}
	
	public Entity setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	public boolean shouldSpawn()
	{
		return true;
	}
	
	public void initialize()
	{
		
	}
	
	public void tick(int ticks)
	{
		
	}
	
	public boolean processInteraction(boolean mouse)
	{
		return false;
	}
	
	public void draw(TextureHandler t, int ticks)
	{
		
	}
	
	public void onDeath()
	{
		this.life = -1;
	}
	
	public boolean isEnemy()
	{
		return false;
	}
	
	public boolean isColliding(Entity e)
	{
		return this.isColliding(e.x,e.y,e.width,e.height);
	}
	
	public boolean isColliding(int x, int y, int w, int h)
	{
		if(this.state.isDead()||this.width==0||this.height==0) return false;
		return this.x<x+w&&x<this.x+this.width&&this.y<y+h&&y<this.y+this.height;
	}
	
	public static enum EnumState
	{
		ALIVE,
		SQUISHED,
		POISONED,
		DEAD;
		
		public boolean isDead()
		{
			return this!=ALIVE;
		}
		
		public boolean wasKilled()
		{
			return this==SQUISHED||this==POISONED;
		}
	}
}
