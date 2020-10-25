package us.emdex.halloween;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.Color;

import javax.imageio.ImageIO;
import static us.emdex.halloween.Application.WIDTH;
import static us.emdex.halloween.Application.HEIGHT;

public class TextureHandler
{
	private final Application application;
	private final Graphics2D graphics;
	
	private BufferedImage entities, tiles;
	
	private int constTicks;
	
	public TextureHandler(Application app, Graphics2D graphics)
	{
		this.application = app;
		this.graphics = graphics;
		app.status("Loading textures");
		app.setIcon(this.load("icon",128,128));
		this.entities = this.load("entities",256,256);
		this.tiles = this.load("tiles",256,256);
		Tiles.register();
	}
	
	private final BufferedImage load(String resource, int w, int h)
	{
		try
		{
			return ImageIO.read(TextureHandler.class.getResourceAsStream("/textures/"+resource+".png"));
		}
		catch(Throwable e)
		{
			BufferedImage fallback = new BufferedImage(w,h,TYPE_INT_RGB);
			Graphics2D g = fallback.createGraphics();
			Color c;
			String s;
			int i = 0;
			for(int y = 0; y < h; y++)
			{
				for(int x = 0; x < w; x++)
				{
					c = Color.getHSBColor((float)x/(float)w, 1F, 1F-((float)y/(float)h));
					g.setColor(c);
					g.fillRect(x,y,1,1);
					if(x%16==8&&y%16==13)
					{
						g.setColor(new Color(-c.getRGB()));
						s = String.valueOf(i++);
						System.out.println("I:"+i+" X:"+x+" Y:"+y);
						g.drawString(s,x-g.getFontMetrics().stringWidth(s)/2,y);
					}
				}
			}
			return fallback;
		}
	}
	
	public void drawSprite(Sprites sprite, int x, int y)
	{
		this.drawSprite(sprite,x,y,-1);
	}
	
	public void drawSprite(Sprites sprite, int x, int y, int time)
	{
		if(time<0) time = this.application.getTicks();
		sprite.draw(this, x, y, time);
	}
	
	public void drawSprite(Sprites sprite, int x, int y, int w, int h, int time)
	{
		sprite.draw(this, x, y, w, h, time);
	}
	
	public void drawEntity(int x, int y, int w, int h, int tx, int ty, int tw, int th)
	{
		this.graphics.drawImage(this.entities.getSubimage(tx, ty, tw, th),x,y,w,h,null);
	}
	
	public void drawTile(int x, int y, int tx, int ty)
	{
		this.graphics.drawImage(this.tiles.getSubimage(tx,ty,16,16),x,y,null);
	}
	
	public void graphicsTest()
	{
		Random random = new Random(this.constTicks);
		int i;
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				i = Math.abs(y-this.constTicks)%100;
				if(random.nextInt(4)==0)
				{
					this.graphics.setColor(new Color(i,i,i,255));
					this.graphics.fillRect(x,y,1,1);
				}
			}
		}
	}
	
	public Graphics2D getGraphics()
	{
		return this.graphics;
	}
	
	public int getTicks()
	{
		return this.application.getTicks();
	}
	
	public void refresh()
	{
		this.constTicks++;
	}
	
	public int getConstTicks()
	{
		return this.constTicks;
	}
}
