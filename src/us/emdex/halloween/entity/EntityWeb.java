package us.emdex.halloween.entity;

import us.emdex.halloween.Sprites;
import us.emdex.halloween.TextureHandler;
import us.emdex.halloween.World;
import static us.emdex.halloween.Constants.PTS_PER_WEB;

public class EntityWeb extends Entity
{
	public EntityWeb(World world)
	{
		super(world);
		this.width = 8;
		this.height = 8;
	}
	
	@Override
	public boolean processInteraction(boolean mouse)
	{
		super.processInteraction(mouse);
		this.state = mouse?EnumState.SQUISHED:EnumState.POISONED;
		this.world.hud.addScore(PTS_PER_WEB);
		return false;
	}
	
	@Override
	public void draw(TextureHandler t, int ticks)
	{
		super.draw(t, ticks);
		t.drawSprite(Sprites.WEB, this.x, this.y);
	}
}
