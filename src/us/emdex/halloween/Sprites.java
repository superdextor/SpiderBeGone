package us.emdex.halloween;

public enum Sprites
{
	SPRAY(-1,16,32, new int[][] {new int[] {48,0}}),
	POISON(1,8,8,new int[][] {new int[] {64,0}, new int[] {72,0}, new int[] {64,8}, new int[] {72,8}}),
	SPIDER(0,0),
	SPIDER_WALK(4,16,16,new int[][] {new int[] {16,0}, new int[] {32,0}}),
	SPIDER_SQUISHED(2,16,16,new int[][] {new int[] {0,16}, new int[] {16,16}, new int[] {32,16}}),
	SPIDER_POISONED(2,16,16,new int[][] {new int[] {0,32}, new int[] {16,32}, new int[] {32,32}}),
	JUMP_SPIDER(0,48),
	JUMP_SPIDER_LEAP(16,48),
	JUMP_SPIDER_SQUISHED(2,16,16, new int[][] {new int[] {0,64}, new int[] {16,64}, new int[] {32,64}}),
	JUMP_SPIDER_POISONED(2,16,16, new int[][] {new int[] {0,80}, new int[] {16,80}, new int[] {32,80}}),
	WEB(1,8,8,new int[][] {new int[] {48,32}}),
	BAT_FLYING(3,16,16, new int[][] {new int[] {0,96}, new int[] {16,96}, new int[] {32,96}, new int[] {16,96}});
	
	private final int[][] frames;
	private final byte speed;
	private final byte width, height;
	
	private Sprites(int x, int y)
	{
		this(-1,16,16,new int[][] {new int[] {x,y}});
	}
	
	private Sprites(int s, int w, int h, int[][] frames)
	{
		this.frames = frames;
		this.speed = (byte)s;
		this.width = (byte)w;
		this.height = (byte)h;
	}
	
	public void draw(TextureHandler t, int x, int y, int time)
	{
		this.draw(t,x,y,this.width,this.height,time);
	}
	
	public void draw(TextureHandler t, int x, int y, int w, int h, int time)
	{
		int i = 0;
		if(this.speed>=0) i = (time/this.speed)%this.frames.length;
		int[] frame = this.frames[i];
		t.drawEntity(x, y, w, h, frame[0], frame[1], this.width, this.height);
	}
}
