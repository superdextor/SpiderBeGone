package us.emdex.halloween;

import java.util.ArrayList;
import java.util.Random;

public enum Tiles
{
	DEFAULT(0,0, 1.0F),
	ALT(16,0,1.0F),
	KNOT(32,0,0.15F),
	TOP(48,0,0.0F),
	/* ---- GUI ---- */
	BOX_NE(0,16),
	BOX_N(16,16),
	BOX_NW(32,16),
	BOX_SE(0,32),
	BOX_S(16,32),
	BOX_SW(32,32),
	BOX_NE_S(48,16),
	BOX_N_S(64,16),
	BOX_NW_S(80,16),
	BOX_SE_S(48,32),
	BOX_S_S(64,32),
	BOX_SW_S(80,32);
	
	private final short x, y;
	private final byte w;
	private static final ArrayList<Tiles> TILES = new ArrayList<Tiles>();
	
	private Tiles(int x, int y)
	{
		this(x, y, 0.0F);
	}
	
	private Tiles(int x, int y, float w)
	{
		this.x = (short)x;
		this.y = (short)y;
		this.w = (byte)(Math.max(Math.min(1.0F, w), 0.0F)*100);
	}
	
	public static void register()
	{
		for(Tiles t : values())
		{
			for(byte i = t.w; i > 0; i--)
			{
				TILES.add(t);
			}
		}
	}
	
	public void draw(TextureHandler t, int x, int y)
	{
		t.drawTile(x, y, this.x, this.y);
	}
	
	public short getId()
	{
		return (short)this.ordinal();
	}
	
	public static Tiles byId(short id)
	{
		return values()[id%values().length];
	}
	
	public static Tiles chooseTile(int x, int y, Random random)
	{
		if(y==0) return TOP;
		return TILES.get(random.nextInt(TILES.size()));
	}
}
