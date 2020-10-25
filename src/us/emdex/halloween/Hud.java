package us.emdex.halloween;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import us.emdex.halloween.AudioHandler.Sounds;
import us.emdex.halloween.SaveHandler.ScorePosition;
import us.emdex.halloween.World.Difficulty;
import us.emdex.halloween.entity.EntityBat;
import us.emdex.halloween.entity.EntitySpray;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static us.emdex.halloween.Application.HEIGHT;
import static us.emdex.halloween.Application.WIDTH;
import static us.emdex.halloween.Tiles.*;
import static us.emdex.halloween.Hud.PauseState.*;
import static us.emdex.halloween.Constants.*;

public class Hud 
{
	private final World world;
	private final SaveHandler saveHandler;
	
	private PauseState pauseState = RUNNING;
	Difficulty newDifficulty = null;
	private ScorePosition position;
	
	private int score;
	private byte life;
	private short kills;
	private byte sprayIndex = 0;
	private short charge;
	private byte stage;
	
	private boolean sprayLeft, sprayRight, sprayAttack, dead, selectHeld;
	
	public Hud(World world, SaveHandler saveHandler)
	{
		this.world = world;
		this.saveHandler = saveHandler;
	}
	
	public void initialize(boolean flag)
	{
		this.pauseState = RUNNING;

		this.score = 0;
		this.life = (byte)(flag?Byte.MAX_VALUE:3);
		this.kills = 0;
		this.charge = 80;
		this.stage = 0;
		this.newDifficulty = this.world.getDifficulty();
		this.dead = false;
		this.position = null;
	}
	
	public void tick(int ticks)
	{
		if(this.sprayLeft) this.world.player.move(-4);
		
		if(this.sprayRight) this.world.player.move(4);
		
		if(this.sprayAttack)
		{
			boolean flag = false;
			int j = 0;
			if(this.sprayIndex>125||this.sprayIndex<0) this.sprayIndex = 0;
			for(int k = 0; k < 3 && this.useCharge(1); k++, this.sprayIndex++)
			{
				flag = true;
				if((this.sprayIndex%6)<3)
				{
					switch(this.sprayIndex%3)
					{
						case 0:
						{
							j = -22;
							break;
						}
						case 1:
						{
							j = 0;
							break;
						}
						case 2:
						{
							j = 22;
							break;
						}
					}
				}
				else
				{
					switch(this.sprayIndex%3)
					{
						case 0:
						{
							j = -20;
							break;
						}
						case 1:
						{
							j = 20;
							break;
						}
						case 2:
						{
							j = Integer.MAX_VALUE;
							break;
						}
					}
				}
				if(j!=Integer.MAX_VALUE) this.world.spawnEntity(new EntitySpray(j,this.world));
			}
			if(flag&&(this.sprayIndex/3)%4==1) this.world.playSound(Sounds.SPRAY);
		}
		else
		{
			if(this.sprayIndex>1) this.sprayIndex = 0;
			if(this.charge<80) this.charge+=1;
		}
		
		if(this.dead) this.trySave(RUNNING);
	}
	
	public void keyAction(KeyEvent e, boolean press)
	{
		if(this.position!=null&&press)
		{
			String s = this.position.getPlayer();
			switch(e.getKeyCode())
			{
				case VK_DELETE:
				case VK_BACK_SPACE:
				{
					if(!s.isEmpty()) s = s.substring(0, s.length()-1);
					break;
				}
				default:
				{
					char c = e.getKeyChar();
					if(Character.isWhitespace(c)||!Character.isDefined(c)) break;
					s+=c;
					if(s.length()>10) s = s.substring(0, 10);
					break;
				}
			}
			this.position.setPlayer(s);
		}
		
		switch(e.getKeyCode())
		{
			default: break;
			case VK_A:
			case VK_LEFT:
			{
				this.sprayLeft = press;
				if(!press)
				{
					switch(this.pauseState)
					{
						default: break;
						case DIFF_CANCEL:
						case DIFF_APPLY:
						{
							Difficulty difficulty = this.newDifficulty;
							this.newDifficulty = this.newDifficulty.decrease();
							if(this.newDifficulty!=difficulty) this.world.playSound(Sounds.CLICK);
							break;
						}
					}
				}
				break;
			}
			case VK_D:
			case VK_RIGHT:
			{
				this.sprayRight = press;
				if(!press)
				{
					switch(this.pauseState)
					{
						default: break;
						case DIFF_CANCEL:
						case DIFF_APPLY:
						{
							Difficulty difficulty = this.newDifficulty;
							this.newDifficulty = this.newDifficulty.increase();
							if(this.newDifficulty!=difficulty) this.world.playSound(Sounds.CLICK);
							break;
						}
					}
				}
				break;
			}
			case VK_ENTER:
			case VK_SPACE:
			{
				this.sprayAttack = press;
				if(press&&this.isPaused()) this.selectHeld = true;
				if(!press==this.selectHeld)
				{
					switch(this.pauseState)
					{
						default: break;
						case DIFFICULTY:
						{
							this.world.playSound(Sounds.CHOOSE);
							this.pauseState = DIFF_CANCEL;
							break;
						}
						case SCOREBOARD:
						{
							this.world.playSound(Sounds.CHOOSE);
							this.pauseState = SCORE_RETURN;
							break;
						}
						case SAVE_SCORE:
						{
							this.saveHandler.saveScores();
							if(this.newDifficulty==null) this.world.exit();
							else this.world.initialize();
							break;
						}
						case CONTINUE:
						{
							this.setPaused(false);
							break;
						}
						case DIFF_APPLY:
						{
							this.trySave(RUNNING);
							this.world.playSound(Sounds.RESUME);
							break;
						}
						case RESTART:
						{
							this.world.playSound(Sounds.CHOOSE);
							this.pauseState = PROMPT_G;
							break;
						}
						case GIVEUP:
						{
							this.world.playSound(Sounds.CHOOSE);
							this.newDifficulty = null;
							this.pauseState = PROMPT_G;
							break;
						}
						case DIFF_CANCEL:
						{
							this.world.playSound(Sounds.RETURN);
							this.newDifficulty = this.world.getDifficulty();
							this.pauseState = DIFFICULTY;
							break;
						}
						case SCORE_RETURN:
						{
							this.world.playSound(Sounds.RETURN);
							this.pauseState = SCOREBOARD;
							break;
						}
						case PROMPT_A:
						{
							if(this.newDifficulty==null) this.trySave(null);
							else this.trySave(RUNNING);
							this.world.playSound(Sounds.RESUME);
							break;
						}
						case PROMPT_G:
						{
							if(this.newDifficulty==null)
							{
								this.newDifficulty = this.world.getDifficulty();
								this.trySave(GIVEUP);
							}
							else this.pauseState = RESTART;
							this.world.playSound(Sounds.RETURN);
							break;
						}
					}
					this.selectHeld = false;
				}
				break;
			}
			case VK_ESCAPE:
			{
				if(!press&&this.pauseState!=SAVE_SCORE) this.setPaused(!this.pauseState.isPaused());
				break;
			}
			case VK_W:
			case VK_UP:
			{
				if(!press && this.isPaused())
				{
					PauseState current = this.pauseState;
					this.pauseState = this.pauseState.up();
					if(current!=this.pauseState) this.world.playSound(Sounds.CLICK);
				}
				break;
			}
			case VK_S:
			case VK_DOWN:
			{
				if(!press && this.isPaused())
				{
					PauseState current = this.pauseState;
					this.pauseState = this.pauseState.down();
					if(this.pauseState!=current) this.world.playSound(Sounds.CLICK);
				}
				break;
			}
		}
	}
	
	public void draw(TextureHandler t, Graphics2D g, int ticks)
	{
		boolean flag = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<4&&ticks%80<40;
		g.setFont(FONT_HUD);
		if(this.isPaused()||ticks%80==0&&flag)
		{
			if(this.isPaused())
			{
				g.setColor(COLOR_BG);
				g.fillRect(0,0,WIDTH,HEIGHT);
			}
			this.drawBox(t,0,HEIGHT-32,WIDTH,this.world.getDifficulty().getColor(this.stage));
			this.drawText(t.getGraphics(),this.pauseState.getMessage(flag),-1,HEIGHT-8,COLOR_W,false);
		}
		else
		{
			this.drawBox(t,0,HEIGHT-32,WIDTH,this.world.getDifficulty().getColor(this.stage));
			g.setColor(COLOR_W);
			
			g.drawString(this.getNumber(this.score, 8), 16, HEIGHT-8);
			
			String s = " ";
			for(int i = 0; i < this.life; i++)
			{
				s += "+ ";
			}
			
			g.drawString(" _ _ _ ",80,HEIGHT-20);
			g.setColor(COLOR_B);
			g.drawString(s,81,HEIGHT-7);
			g.setColor(COLOR_R);
			g.drawString(s,80,HEIGHT-8);
			g.setColor(COLOR_W);
			g.drawString("|_|_|_|",80,HEIGHT-8);
			g.drawString(this.getNumber(this.kills,5),136,HEIGHT-8);
			g.setColor(this.world.getDifficulty().useUberCharge()?COLOR_G:COLOR_Y);
			s = "";
			int c = this.charge/10;
			for(int i = 0; i < c; i++)
			{
				s+="»";
			}
			g.drawString(s,182,HEIGHT-8);
			g.setColor(COLOR_B);
			s = "";
			for(int i = 0; i < 8; i++)
			{
				s += i<c?" ":"»";
			}
			g.drawString(s,182,HEIGHT-8);
		}
		
		
		if(this.isPaused())
		{
			switch(this.pauseState)
			{
				default:
				{
					this.drawTextBox(t,0,"Difficulty", this.pauseState==DIFFICULTY, COLOR_P);
					this.drawTextBox(t, 32, "Scoreboard", this.pauseState==SCOREBOARD, COLOR_BL);
					this.drawTextBox(t,64,"Continue", this.pauseState==CONTINUE, COLOR_G);
					this.drawTextBox(t,96,"Restart", this.pauseState==RESTART, COLOR_Y);
					this.drawTextBox(t,128,"Give up", this.pauseState==GIVEUP, COLOR_R);
					break;
				}
				case DIFF_CANCEL:
				case DIFF_APPLY:
				{
					this.drawText(t.getGraphics(), "--- Difficulty ---", -1, 24, COLOR_W,true);
					this.drawTextBox(t, 32, "Cancel", this.pauseState==DIFF_CANCEL, COLOR_BL);
					this.drawTextBox(t, 64, this.newDifficulty.getName(), false, this.newDifficulty.getColor(0));
					this.drawTextBox(t, 96, "Apply", this.pauseState==DIFF_APPLY, COLOR_G);
					this.drawText(t.getGraphics(), "Changing the difficulty", -1,144,COLOR_W,false);
					this.drawText(t.getGraphics(), "requires a restart", -1,156,COLOR_W,false);
					break;
				}
				case SAVE_SCORE:
				case SCORE_RETURN:
				{
					ScorePosition pos;
					for(int i = 1, y = 4; i <= 5; i++,y+=30)
					{
						pos = this.saveHandler.get(i);
						this.drawScore(t,(i)+".",pos,y);
					}
					break;
				}
				case PROMPT_A:
				case PROMPT_G:
				{
					this.drawText(t.getGraphics(),this.newDifficulty==null?"Quit?":"Restart?",-1,48,COLOR_W,true);
					this.drawTextBox(t, 56, "Yes", this.pauseState==PROMPT_A, COLOR_BL);
					this.drawTextBox(t, 88, "No", this.pauseState==PROMPT_G, COLOR_Y);
					break;
				}
			}
		}
		
		if(this.life==Byte.MAX_VALUE)
		{
			t.graphicsTest();
		}
	}
	
	private String getNumber(int i, int l)
	{
		String s = String.valueOf(i);
		while(s.length()<l)
		{
			s = "0"+s;
		}
		return s;
	}
	
	private void drawTextBox(TextureHandler t, int y, String text, boolean selected, Color c2)
	{
		text = " "+text+" ";
		int w = (int)(Math.ceil(t.getGraphics().getFontMetrics().stringWidth(text)/16D))*16;
		int x = (WIDTH-w)/2;
		this.drawBox(t,x,y,w,selected?c2.darker():c2,selected);
		this.drawText(t.getGraphics(),text,-1,y+24,selected?COLOR_W:COLOR_B,false);
	}
	
	private void drawText(Graphics2D g, String text, int x, int y, Color c, boolean shadow)
	{
		if(x==-1) x = (WIDTH-g.getFontMetrics().stringWidth(text))/2;
		if(shadow)
		{
			g.setColor(COLOR_B);
			g.drawString(text,x+1,y+1);
		}
		g.setColor(c);
		g.drawString(text,x,y);
	}
	
	private void drawBox(TextureHandler t, int x, int y, int w, Color c)
	{
		this.drawBox(t,x,y,w,c,false);
	}
	
	private void drawBox(TextureHandler t, int x, int y, int w, Color c, boolean selected)
	{
		if(this.life==Byte.MAX_VALUE) c = COLOR_B;
		t.getGraphics().setColor(c);
		t.getGraphics().fillRect(x+3,y+11,w-6,18);
		if(selected)
		{
			BOX_NE_S.draw(t,x,y);
			BOX_NW_S.draw(t,x+w-16,y);
			for(int i = x+16; i < x+w-16; i+=16)
			{
				BOX_N_S.draw(t,i,y);
				BOX_S_S.draw(t,i,y+16);
			}
			BOX_SE_S.draw(t,x,y+16);
			BOX_SW_S.draw(t,x+w-16,y+16);
		}
		else
		{
			BOX_NE.draw(t,x,y);
			BOX_NW.draw(t,x+w-16,y);
			for(int i = x+16; i < x+w-16; i+=16)
			{
				BOX_N.draw(t,i,y);
				BOX_S.draw(t,i,y+16);
			}
			BOX_SE.draw(t,x,y+16);
			BOX_SW.draw(t,x+w-16,y+16);
		}
	}
	
	private void drawScore(TextureHandler t, String prefix, ScorePosition pos, int y)
	{
		this.drawBox(t, 16, y, 224, COLOR_B);
		y+=24;
		if(!prefix.isEmpty()) this.drawText(t.getGraphics(),prefix,24,y,COLOR_W,false);
		this.drawText(t.getGraphics(),pos.getPlayer()+(this.position==pos&&(t.getConstTicks()/10)%2==0?"_":""),40,y,pos.getDifficulty().getColor(0),false);
		this.drawText(t.getGraphics(),this.getNumber(pos.getScore(),8), 120, y,COLOR_W,false);
		this.drawText(t.getGraphics(), this.getNumber(pos.getKills(),5), 184, y, COLOR_W, false);

	}
	
	public void addScore(int v)
	{
		int i = this.score;
		this.score+=v;
		if(i/250!=this.score/250)
		{
			this.world.spawnEntity(new EntityBat(this.world));
		}
	}
	
	public void addKill()
	{
		this.kills++;
		boolean intense = this.isIntense();
		byte prevStage = this.stage;
		switch(this.stage)
		{
			default: break;
			case 0:
			{
				if(this.kills>=PHASE_1_KILLS) this.stage++;
				break;
			}
			case 1:
			{
				if(this.kills>=PHASE_2_KILLS) this.stage++;
				break;
			}
			case 2:
			{
				if(this.kills>=PHASE_3_KILLS) this.stage++;
				break;
			}
			case 3:
			{
				if(this.kills>=PHASE_4_KILLS) this.stage++;
				break;
			}
			case 4:
			{
				if(this.kills>=PHASE_5_KILLS) this.stage++;
				break;
			}
		}
		if(intense!=this.isIntense()&&this.getLives()>1) this.world.resetMusic();
		if(prevStage!=this.stage) this.world.playSound(Sounds.NEXT_PHASE);
	}
	
	public void removeLife()
	{
		this.world.playSound(Sounds.HURT);
		if(this.life==Byte.MAX_VALUE) return;
		if(--this.life<1) this.dead = true;
		if(this.life<=1) this.world.resetMusic();
	}
	
	public boolean useCharge(int c)
	{
		if(this.world.getDifficulty().useUberCharge()) c = Math.max(1,c/4);
		boolean flag = this.charge>=c;
		this.charge = (short)Math.max(0,this.charge-c);
		return flag;
	}
	
	public boolean isLoopPaused()
	{
		return this.life!=Byte.MAX_VALUE&&this.isPaused();
	}
	
	public boolean isPaused()
	{
		return this.pauseState.isPaused();
	}
	
	public void setPaused(boolean value)
	{
		if(value!=this.isPaused())
		{
			this.pauseState = value?CONTINUE:RUNNING;
			if(value)
			{
				this.newDifficulty = this.world.getDifficulty();
				this.world.playSound(Sounds.PAUSE);
			}
			else this.world.playSound(Sounds.RESUME);
		}
	}
	
	public byte getStage()
	{
		return this.stage;
	}
	
	public int getLives()
	{
		return this.life;
	}
	
	public boolean isIntense()
	{
		return this.world.getDifficulty().isIntense(this);
	}
	
	private void trySave(PauseState state)
	{
		if(this.life==Byte.MAX_VALUE) this.world.exit();
		this.position = this.saveHandler.canSave(this.score);
		if(this.position!=null)
		{
			ScorePosition.shift(this.position);
			this.position.apply(this.world.getDifficulty(),this.score,this.kills);
			this.pauseState = SAVE_SCORE;
		}
		else
		{
			if(state==null)
			{
				this.world.exit();
				return;
			}
			if(state==RUNNING) this.world.initialize();
			this.pauseState = state;
		}
	}
	
	public void windowDeactivated()
	{
		this.setPaused(true);
		this.sprayRight = false;
		this.sprayLeft = false;
		this.sprayAttack = false;
	}
	
	static enum PauseState
	{
		RUNNING,
		/* Paused */
		DIFFICULTY,
		SCOREBOARD,
		CONTINUE,
		RESTART,
		GIVEUP,
		/* Sub menus */
		DIFF_CANCEL,
		DIFF_APPLY,
		SCORE_RETURN,
		SAVE_SCORE,
		PROMPT_A,
		PROMPT_G;
		
		public boolean isPaused()
		{
			return this!=RUNNING;
		}
		
		public PauseState up()
		{
			switch(this)
			{
				default: return this;
				case DIFFICULTY: return GIVEUP;
				case SCOREBOARD: return DIFFICULTY;
				case CONTINUE: return SCOREBOARD;
				case RESTART: return CONTINUE;
				case GIVEUP: return RESTART;
				
				case DIFF_CANCEL: return DIFF_APPLY;
				case DIFF_APPLY: return DIFF_CANCEL;
				
				case PROMPT_A: return PROMPT_G;
				case PROMPT_G: return PROMPT_A;
			}
		}
		
		public PauseState down()
		{
			switch(this)
			{
				default: return this;
				case DIFFICULTY: return SCOREBOARD;
				case SCOREBOARD: return CONTINUE;
				case CONTINUE: return RESTART;
				case RESTART: return GIVEUP;
				case GIVEUP: return DIFFICULTY;
				
				case DIFF_CANCEL: return DIFF_APPLY;
				case DIFF_APPLY: return DIFF_CANCEL;
				
				case PROMPT_A: return PROMPT_G;
				case PROMPT_G: return PROMPT_A;
			}
		}
		
		public String getMessage(boolean flag)
		{
			if(flag) return "Don't play at night";
			switch(this)
			{
				default: return "»»» »»» Paused ««« «««";
				case SCORE_RETURN: return "«« Top 5 Players »»";
			}
		}
	}
}
