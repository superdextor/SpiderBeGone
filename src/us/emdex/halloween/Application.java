package us.emdex.halloween;

import static java.awt.event.WindowEvent.WINDOW_CLOSING;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static us.emdex.halloween.Constants.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Application extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener 
{
	private static final long serialVersionUID = 3116109317588336633L;

	public static void main(String[] args)
	{
		System.out.println("Launching with arguments: " + Arrays.toString(args));
		new Application(args);
	}
	
	public static final int WIDTH = 256;
	public static final int HEIGHT = 192;
	
	private final JFrame frame;
	private final BufferedImage image;
	private final Graphics2D graphics;
	private final TextureHandler textureHandler;
	private final AudioHandler audioHandler;
	private final SaveHandler saveHandler;
	private final World world;
	private final Thread thread;
	
	private int mouseX, mouseY;
	private boolean running = false;
	private int ticks;
	
	private Application(String[] args)
	{
		{
			int scale = 3;
			this.setPreferredSize(new Dimension(WIDTH*scale,HEIGHT*scale));
		}
		this.frame = new JFrame(Constants.getAppName());
		this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.frame.setContentPane(this);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
		System.out.println("Created JFrame");
		this.image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		this.graphics = this.image.createGraphics();
		this.textureHandler = new TextureHandler(this,this.graphics);
		this.audioHandler = new AudioHandler(this);
		this.status("Initializing");
		this.saveHandler = new SaveHandler();
		this.frame.addWindowListener(this);
		this.world = new World(this,this.audioHandler,this.saveHandler);
		this.thread = new Thread(this);
		this.frame.addKeyListener(this);
		this.frame.addMouseListener(this);
		this.frame.addMouseMotionListener(this);
		this.frame.addMouseWheelListener(this);
		this.running = true;
		this.status("Starting loop");
		this.thread.start();
	}
	
	public void setFullscreen(boolean value)
	{
		if(this.frame.isUndecorated()!=value)
		{
			this.frame.dispose();
			this.frame.setUndecorated(value);
			if(value)
			{
				this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				this.frame.setLocation(0,0);
				this.frame.pack();
			}
			else
			{
				this.frame.setExtendedState(JFrame.NORMAL);
				this.frame.pack();
				this.frame.setLocationRelativeTo(null);
			}
			
			this.frame.setVisible(true);
		}
	}
	
	public boolean isFullscreen()
	{
		return this.frame.isUndecorated();
	}
	
	void status(String status)
	{
		System.out.println(status);
		if(!this.running)
		{
			this.graphics.setColor(COLOR_B);
			this.graphics.fillRect(0, 0, WIDTH, HEIGHT);
			this.graphics.setColor(COLOR_W);
			this.graphics.setFont(FONT_LOADING);
			this.graphics.drawString("Loading",(WIDTH-this.graphics.getFontMetrics().stringWidth("Loading"))/2, HEIGHT/2-16);
			this.graphics.setFont(FONT_STATUS);
			this.graphics.drawString(status, (WIDTH-this.graphics.getFontMetrics().stringWidth(status))/2, HEIGHT/2+16);
			Graphics g = this.getGraphics();
			g.drawImage(this.image, 0,0,this.getWidth(),this.getHeight(),null);
			g.dispose();
		}
	}
	
	void setIcon(BufferedImage image)
	{
		this.frame.setIconImage(image);
	}
	
	public int getTicks()
	{
		return this.ticks;
	}
	
	@Override
	public void run()
	{
		this.saveHandler.initialize(this, this.world);
		this.world.initialize();
		long start, wait;
		Color background = new Color(100,100,100,255);
		Graphics g;
		boolean pause = false;
		while(this.running)
		{
			start = System.nanoTime();
			pause = this.world.hud.isLoopPaused();
			this.graphics.setColor(background);
			this.graphics.fillRect(0,0,WIDTH,HEIGHT);
			this.loop(pause);
			g = this.getGraphics();
			if(g!=null)
			{
				g.drawImage(this.image, 0,0,this.getWidth(),this.getHeight(),null);
				g.dispose();
			}
			if(!pause) this.ticks++;
			wait = (System.nanoTime() - start) / 1000000;
			if(wait<TPS) wait = TPS;
			
			try
			{
				Thread.sleep(wait);
			}
			catch(InterruptedException e) {}
		}
		
		switch(this.ticks%4)
		{
			default:
			{
				System.out.println("Goodbye");
				break;
			}
			case 1:
			{
				System.out.println("Cye ya'");
				break;
			}
			case 2:
			{
				System.out.println("kthxbai");
				break;
			}
			case 3:
			{
				System.out.println("Bye bye");
				break;
			}
		}
	}
	
	private void loop(boolean pause)
	{
		if(!pause) this.world.tick(this.ticks);
		this.textureHandler.refresh();
		this.world.draw(this.textureHandler, this.graphics, this.ticks);
		this.audioHandler.refresh();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		this.world.keyAction(e,true);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		this.world.keyAction(e,false);
		if(e.getKeyCode()==KeyEvent.VK_F11)
		{
			this.setFullscreen(!this.isFullscreen());
		}
	}
	
	public World getWorld()
	{
		return this.world;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {}

	@Override
	public void mousePressed(MouseEvent e)
	{
		this.world.mouseInteraction(this.mouseX, this.mouseY);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		Point p = this.getMousePosition();
		if(p==null) return;
		this.mouseX = (int)(p.x/(float)(this.getWidth()/(float)WIDTH));
		this.mouseY = (int)(p.y/(float)(this.getHeight()/(float)HEIGHT));
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		this.mouseMoved(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public void exit()
	{
		this.frame.dispatchEvent(new WindowEvent(this.frame, WINDOW_CLOSING));
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e)
	{
		this.saveHandler.saveSettings(this, this.world);
		this.running = false;
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		this.getWorld().hud.windowDeactivated();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
