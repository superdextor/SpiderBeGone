package us.emdex.halloween;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import us.emdex.halloween.AudioHandler.Sounds;
import us.emdex.halloween.entity.Entity;
import us.emdex.halloween.entity.EntityJumpSpider;
import us.emdex.halloween.entity.EntitySpider;
import us.emdex.halloween.entity.EntitySprayCan;
import static us.emdex.halloween.Constants.KEY_DIFFICULTY;

public class World
{
	private static final ArrayList<Class<? extends Entity>> HOSTILES;
	
	private final Application application;
	private final AudioHandler audioHandler;
	private final SaveHandler saveHandler;
	private final ArrayList<Entity> queue;
	private final ArrayList<Entity> alive;
	private final Tiles[][] tiles;
	private final ArrayList<Particle> particles;
	public final Random random;
	public final EntitySprayCan player;
	public final Hud hud;
	
	private boolean secretMode = false;
	private int spawnTimer;
	private short[] mouseInteraction;
	private Difficulty difficulty;
	private int enemyCount;
	
	public World(Application application, AudioHandler audioHandler, SaveHandler saveHandler)
	{
		this.application = application;
		this.audioHandler = audioHandler;
		this.saveHandler = saveHandler;
		this.queue = new ArrayList<Entity>();
		this.alive = new ArrayList<Entity>();
		this.tiles = new Tiles[16][16];
		this.particles = new ArrayList<Particle>();
		this.random = new Random();
		this.player = new EntitySprayCan(this);
		this.hud = new Hud(this,saveHandler);
	}
	
	public void initialize()
	{
		this.spawnTimer = this.secretMode?-160:-12;
		this.audioHandler.resetMusic();
		this.queue.clear();
		this.alive.clear();
		this.enemyCount = 0;
		this.spawnEntity(this.player);
		{
			Random random = new Random();
			int x, y;
			Tiles[] row;
			for(y = 0; y < this.tiles.length; y++)
			{
				row = this.tiles[y];
				for(x = 0; x < row.length; x++)
				{
					row[x] = Tiles.chooseTile(x,y,random);
				}
			}
		}
		this.difficulty = this.hud.newDifficulty;
		if(this.difficulty==null)
		{
			this.difficulty = Difficulty.byId(this.saveHandler.get(KEY_DIFFICULTY, (byte)1));
		}
		this.hud.initialize(this.secretMode);
	}
	
	private Class<? extends Entity> getEnemy(int ticks)
	{
		if(ticks%3==0) return EntityJumpSpider.class;
		else return EntitySpider.class;
	}
	
	public void tick(int ticks)
	{
		if(this.mouseInteraction!=null)
		{
			if(this.mouseInteraction.length==2)
			{
				for(Entity e :this.getEntitiesIn(this.mouseInteraction[0], this.mouseInteraction[1], 1, 1))
				{
					e.processInteraction(true);
				}
			}
			this.mouseInteraction = null;
		}
		
		if(!this.queue.isEmpty())
		{
			for(Entity e : this.queue)
			{
				e.initialize();
				this.alive.add(e);
			}
			this.queue.clear();
		}
		
		int i = this.alive.size();
		Entity e;
		while(i-->0)
		{
			e = this.alive.get(i);
			if(e.life<0)
			{
				if(e.life++==-1)
				{
					this.alive.remove(i);
				}
			}
			else
			{
				e.tick(ticks);
				if(e.state.isDead())
				{
					e.onDeath();
				}
			}
		}
		
		if(this.spawnTimer++>0&&(this.secretMode||this.spawnTimer%this.getDifficulty().getSpawnRate(this.hud)==1))
		{
			i = 0;
			for(Entity entity : this.alive)
			{
				for(Class<? extends Entity> clazz : HOSTILES)
				{
					if(entity.getClass()==clazz) i++;
				}
			}
			this.enemyCount = i;
			
			if(this.secretMode||i<this.getDifficulty().getSpawnLimit(this.hud))
			{
				e = this.createEntity(this.getEnemy(ticks+this.enemyCount));
				if(e.shouldSpawn()) this.spawnEntity(e);
			}
		}
		this.hud.tick(ticks);
	}
	
	public void draw(final TextureHandler t, final Graphics2D g, final int ticks)
	{
		{
			int x, y;
			Tiles[] row;
			for(y = 0; y < this.tiles.length; y++)
			{
				row = this.tiles[y];
				for(x = 0; x < row.length; x++)
				{
					row[x].draw(t, x*16, y*16);
				}
			}
		}
		
		int i = this.alive.size();
		while(i-->0)
		{
			this.alive.get(i).draw(t, ticks);
		}
		
		if(!this.particles.isEmpty())
		{
			Predicate<Particle> handler = new Predicate<Particle>()
			{
				@Override
				public boolean test(Particle p)
				{
					return p.tickThendraw(t, g, ticks);
				}
			};
			this.particles.removeIf(handler);
		}
		
		this.hud.draw(t, g, ticks);
	}
	
	public Entity createEntity(Class<? extends Entity> clazz)
	{
		Entity e = null;
		try
		{
			e = clazz.getConstructor(World.class).newInstance(this);
		}
		catch (IllegalAccessException|InstantiationException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException ex)
		{
			ex.printStackTrace();
		}
		return e;
	}
	
	public <T extends Entity> T spawnEntity(T e)
	{
		this.queue.add(e);
		return e;
	}
	
	public <T extends Particle> T spawnParticle(T p)
	{
		if(!this.secretMode||!p.hiddenInSecret()) this.particles.add(p);
		return p;
	}
	
	public void mouseInteraction(int x, int y)
	{
		if(!this.hud.isPaused())
			this.mouseInteraction = new short[] {(short)x,(short)y};
	}
	
	public void keyAction(KeyEvent e, boolean press)
	{
		this.hud.keyAction(e,press);
	}
	
	public Entity[] getEntitiesIn(int x, int y, int w, int h)
	{
		ArrayList<Entity> list = new ArrayList<Entity>();
		for(Entity e : this.alive)
		{
			if(e.isColliding(x, y, w, h))
			{
				list.add(e);
			}
		}
		return list.toArray(new Entity[0]);
	}
	
	public Difficulty getDifficulty()
	{
		return this.difficulty;
	}
	
	public void playSound(Sounds sound)
	{
		this.audioHandler.playSound(sound);
	}
	
	public void exit()
	{
		this.application.exit();
	}
	
	public int getEnemyCount()
	{
		return this.enemyCount;
	}
	
	public int getTicks()
	{
		return this.application.getTicks();
	}
	
	public void resetMusic()
	{
		this.audioHandler.resetMusic();
	}
	
	public void welpSomeoneMessedUp()
	{
		this.secretMode = true;
	}
	
	public static enum Difficulty
	{
		EASY(8,40,1,2,0.15F, "#156A99"),
		NORMAL(10,25,1,2,0.33F, "#0AAD25"),
		HARD(25,15,1,1,0.66F, "#CC4300"),
		EXTREME(Byte.MAX_VALUE,8,2,1,1.99F, "#FF0072");
		
		private final byte limit;
		private final byte spawnRate;
		private final byte step;
		private final byte delay;
		
		private final float multiplier;
		private final Color[] color;
		
		private Difficulty(int limit, int spawnRate, int step, int delay, float multiplier, String color)
		{
			this.limit = (byte)limit;
			this.spawnRate = (byte)spawnRate;
			this.step = (byte)step;
			this.delay = (byte)delay;
			this.multiplier = multiplier;
			this.color = new Color[6];
			for(int i = 0; i < 6; i++)
			{
				Color base = Color.decode(color);
				float[] hsv = new float[3];
				Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), hsv);
				float f = (i/6F)*(25/255F);
				this.color[i] = Color.getHSBColor(hsv[0]-f,hsv[1],hsv[2]-f*3);
			}
		}
		
		private short applyMultiplier(Hud hud, byte value)
		{
			float f = hud.getStage()/5.0F;
			f *= this.multiplier;
			f+=1;
			return (short)(value*f);
		}
		
		public short getSpawnLimit(Hud hud)
		{
			return this.applyMultiplier(hud,this.limit);
		}
		
		public int getSpawnRate(Hud hud)
		{
			return Math.max(this.spawnRate*2-this.applyMultiplier(hud, this.spawnRate),2);
		}
		
		public byte getStep()
		{
			return this.step;
		}
		
		public byte getDelay(World world)
		{
			return (byte)Math.max(1,this.delay-(world.random.nextFloat()<this.multiplier?1:0));
		}
		
		public boolean useUberCharge()
		{
			return this==EXTREME;
		}
		
		public Color getColor(int stage)
		{
			return this.color[Math.max(Math.min(stage,this.color.length-1),0)];
		}
		
		public String getName()
		{
			return this.name().substring(0, 1).toUpperCase()+this.name().substring(1).toLowerCase();
		}
		
		public Difficulty increase()
		{
			switch(this)
			{
				default: return this;
				case EASY: return NORMAL;
				case NORMAL: return HARD;
				case HARD: return EXTREME;
			}
		}
		
		public Difficulty decrease()
		{
			switch(this)
			{
				default: return this;
				case NORMAL: return EASY;
				case HARD: return NORMAL;
				case EXTREME: return HARD;
			}
		}
		
		public static Difficulty byId(byte id)
		{
			return Difficulty.values()[id%values().length];
		}
		
		public boolean isIntense(Hud hud)
		{
			switch(this)
			{
				default: return false;
				case HARD: return hud.getStage()>3;
				case EXTREME: return hud.getStage()>0;
			}
		}
	}
	
	static
	{
		HOSTILES = new ArrayList<Class<? extends Entity>>();
		HOSTILES.add(EntitySpider.class);
		HOSTILES.add(EntityJumpSpider.class);
	}
}
