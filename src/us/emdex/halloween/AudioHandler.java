package us.emdex.halloween;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import static us.emdex.halloween.AudioHandler.Music.*;

public class AudioHandler
{
	private final Application application;
	private final Random random;
	
	private Music activeTrack = null;
	
	public AudioHandler(Application app)
	{
		this.application = app;
		this.random = new Random();	
		app.status("Loading sounds");
		for(Sounds sound : Sounds.values())
		{
			for(int i = sound.files.length-1; i>=0; i--)
			{
				sound.clips[i] = open(sound.getResource(i));
			}
		}
	}
	
	private static Clip open(String s)
	{
		try
		{
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(AudioHandler.class.getResource(s));
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        return clip;
		}
		catch(Throwable t)
		{
			System.out.println("Missing \"" + s+"\"");
			return null;
		}
	}
	
	public void refresh()
	{
		for(Sounds sound : Sounds.values())
		{
			if(sound.requested)
			{
				sound.requested=false;
				Clip clip = sound.getSound(this.random);
				if(clip==null) continue;
				clip.setFramePosition(0);
				clip.start();
			}
		}
		
		if(this.application.getTicks()%30==0&&this.application.getWorld().hud.getLives()>0)
		{
			if(this.activeTrack==null||!this.activeTrack.isPlaying())
			{
				if(this.application.getWorld().hud.getLives()==Byte.MAX_VALUE)
				{
					this.activeTrack = TEST;
				}
				else if(this.application.getWorld().hud.getLives()<=1)
				{
					this.activeTrack = PERIL;
				}
				else if(this.application.getWorld().hud.isIntense())
				{
					this.activeTrack = INTENSE;
				}
				else if(this.application.getWorld().hud.getStage()>0&&this.application.getWorld().getEnemyCount()<8)
				{
					this.activeTrack = HAPPY;
				}
				else
				{
					this.activeTrack = NORMAL;
				}
				
				this.activeTrack.play(this.random);
			}
		}
	}
	
	public void playSound(Sounds sound)
	{
		sound.requested = true;
	}
	
	public void resetMusic()
	{
		if(this.activeTrack!=null)
		{
			this.activeTrack.stop();
			this.activeTrack = null;
		}
	}
	
	public static enum Music
	{
		NORMAL("rising_tide","broken_reality"),
		HAPPY("tech_talk","aitech"),
		PERIL("i_feel_you", "pilot_error"),
		INTENSE("metalmania", "take_the_lead"),
		TEST("test");
		
		private final String[] files;
		
		private Clip track;
		private int currentTrack = -1;
		
		private Music(String... files)
		{
			this.files = files;
		}
		
		private String getResource(int index)
		{
			return "/music/"+this.files[index]+".wav";
		}
		
		public void play(Random random)
		{
			this.play(random.nextInt(this.files.length));
		}
		
		public void play(int index)
		{
			this.stop();
			if(index<0||index>=this.files.length) return;
			this.track = AudioHandler.open(this.getResource(index));
			if(this.track==null) return;
			this.track.start();
			this.currentTrack = index;
		}
		
		public boolean isPlaying()
		{
			return this.track!=null&&this.track.isActive();
		}
		
		public void stop()
		{
			if(this.currentTrack==-1) return;
			this.currentTrack = -1;
			if(this.track==null) return;
			this.track.setFramePosition(0);
			this.track.stop();
			this.track.close();
			this.track=null;
		}
	}
	
	public static enum Sounds
	{
		SQUISH("squish_0","squish_1"),
		POISON("poison_0", "poison_1"),
		HURT("hurt"),
		NEXT_PHASE("next_phase"),
		SPRAY("spray"),
		PAUSE("pause"),
		RESUME("resume"),
		CLICK("click"),
		CHOOSE("choose"),
		RETURN("return");
		
		private final String[] files;
		private final Clip[] clips;
		
		private boolean requested;
		
		private Sounds(String... files)
		{
			this.files = files;
			this.clips = new Clip[files.length];
		}
		
		public Clip getSound(Random random)
		{
			return this.clips[random.nextInt(this.clips.length)];
		}
		
		private String getResource(int index)
		{
			return "/sounds/"+this.files[index]+".wav";
		}
	}
}
