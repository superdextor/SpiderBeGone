package us.emdex.halloween;

import us.emdex.halloween.World.Difficulty;
import static us.emdex.halloween.World.Difficulty.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TreeMap;

import static us.emdex.halloween.Constants.*;

public class SaveHandler
{
	private final TreeMap<String, String> settings;
	
	private boolean saveScore = true;
	private boolean saveSettings = true;
	
	public SaveHandler()
	{
		this.settings = new TreeMap<String,String>();
	}
	
	public void initialize(Application app, World world)
	{
		File file = new File(DIRECTORY, FILE_SETTINGS);
		if(file.exists())
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String s = "";
				while((s = reader.readLine())!=null)
				{
					if(s.isEmpty()||s.indexOf('=')<1&&s.indexOf('=')==s.length()) break;
					this.settings.put(s.substring(0, s.indexOf('=')), s.substring(s.indexOf('=')+1));
					
				}
				reader.close();
			}
			catch(FileNotFoundException e) {}
			catch(Throwable t)
			{
				System.out.println("Failed to read settings");
				this.saveSettings = false;
			}
			
			if(!this.saveSettings&&file.exists())
			{
				try
				{
					file.delete();
				}
				catch(Throwable t)
				{
					file.deleteOnExit();
				}
			}
		}
		file = new File(DIRECTORY, FILE_SAVE);
		if(file.exists())
		{
			byte index = 0;
			try
			{
				FileInputStream input = new FileInputStream(file);
				for(ScorePosition pos : ScorePosition.all())
				{
					pos.read(input);
					index++;
				}
			}
			catch(FileNotFoundException e) {}
			catch(Throwable e)
			{
				System.out.println("Failed to read I="+index+"(" + e.getStackTrace()[0]+")");
				this.saveScore = false;
			}
			
			if(!this.saveScore&&file.exists())
			{
				try
				{
					file.delete();
				}
				catch(Throwable t)
				{
					try
					{
						file.deleteOnExit();
					}
					catch(Throwable t2) {}
				}
			}
		}
		boolean fullscreen = this.get(KEY_FULLSCREEN, false);
		if(this.get(KEY_SECRET, false)||Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==3&&Calendar.getInstance().get(Calendar.SECOND)%4==0)
		{
			world.welpSomeoneMessedUp();
			fullscreen = true;
		}
		app.setFullscreen(fullscreen);
	}
	
	public byte get(String key, byte fallback)
	{
		return (byte)this.get(key, (int)fallback);
	}
	
	public int get(String key, int fallback)
	{
		if(!this.settings.containsKey(key)) return fallback;
		try
		{
			return Integer.parseInt(this.settings.get(key));
		}
		catch(NumberFormatException e)
		{
			return fallback;
		}
	}
	
	public String get(String key, String fallback)
	{
		if(!this.settings.containsKey(key)) return fallback;
		return this.settings.get(key);
	}
	
	public boolean get(String key, boolean fallback)
	{
		if(!this.settings.containsKey(key)) return fallback;
		switch(this.settings.get(key).toLowerCase())
		{
			default: return fallback;
			case "1":
			case "true": return true;
			case "0":
			case "false": return false;
		}
	}
	
	public void saveSettings(Application app, World world)
	{
		if(!this.saveSettings) return;
		this.settings.put(KEY_VERSION, VERSION);
		this.settings.put(KEY_DIFFICULTY, String.valueOf(world.getDifficulty().ordinal()));
		this.settings.put(KEY_FULLSCREEN, String.valueOf(app.isFullscreen()));
		this.settings.put(KEY_SECRET, "false");
		File file = new File(DIRECTORY);
		try
		{
			file.mkdir();
			file = new File(DIRECTORY, FILE_SETTINGS);
			if(!file.exists())
			{
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			int i = 0;
			for(String key : this.settings.keySet())
			{
				writer.write(key+"="+this.settings.get(key)+(++i==this.settings.size()?"":"\n"));
			}
			writer.close();
		}
		catch(Throwable e)
		{
			System.out.println("Cannot save settings");
			e.printStackTrace();
		}
	}
	
	public void saveScores()
	{
		if(!this.saveScore) return;
		File file = new File(DIRECTORY);
		try
		{
			file.mkdir();
			file = new File(DIRECTORY, FILE_SAVE);
			if(!file.exists())
			{
				file.createNewFile();
			}
			FileOutputStream output = new FileOutputStream(file);
			
			for(ScorePosition pos : ScorePosition.all())
			{
				pos.write(output);
			}
			output.close();
		}
		catch(Throwable e)
		{
			System.out.println("Cannot save score");
			e.printStackTrace();
			return;
		}
	}
	
	public ScorePosition canSave(int score)
	{
		for(ScorePosition pos : ScorePosition.all())
		{
			if(pos.score<score) return pos;
		}
		return null;
	}
	
	public void write(int place, String player, Difficulty difficulty, int score, short kills)
	{
		ScorePosition pos = this.get(place);
		pos.player = player;
		pos.difficulty = pos.difficulty;
		pos.score = score;
		pos.kills = kills;
	}
	
	public ScorePosition get(int place)
	{
		return ScorePosition.fromPlace(place);
	}
	
	public static enum ScorePosition
	{
		DUMMY("",EASY,0),
		FIRST("Potato",HARD,500),
		SECOND("Chip",NORMAL,200),
		THIRD("Fry", NORMAL, 100),
		FOURTH("Spud", EASY, 50),
		FIFTH("Dirt", EASY, 20);
		
		private String player;
		private Difficulty difficulty;
		private int score;
		private short kills;
		
		private ScorePosition(String player, Difficulty difficulty, int kills)
		{
			this.player = player;
			this.difficulty = difficulty;
			this.score = kills*PTS_PER_SPIDER;
			this.kills = (short)kills;
		}
		
		private static ScorePosition fromPlace(int place)
		{
			if(place<1||place>5) return DUMMY;
			return values()[place];
		}
		
		private static ScorePosition[] all()
		{
			return new ScorePosition[] {FIRST,SECOND,THIRD,FOURTH,FIFTH};
		}
		
		public String getPlayer()
		{
			return this.player;
		}
		
		public Difficulty getDifficulty()
		{
			return this.difficulty;
		}
		
		public int getScore()
		{
			return this.score;
		}
		
		public short getKills()
		{
			return this.kills;
		}
		
		public void apply(Difficulty difficulty, int score, short kills)
		{
			this.player = "";
			this.difficulty = difficulty;
			this.score = score;
			this.kills = kills;
		}
		
		private void set(ScorePosition pos)
		{
			this.player = pos.player;
			this.difficulty = pos.difficulty;
			this.score = pos.score;
			this.kills = pos.kills;
		}
		
		public void setPlayer(String player)
		{
			this.player = player;
		}
		
		public void read(FileInputStream input) throws IOException
		{
			byte[] data = new byte[4];
			input.read(data);
			data = new byte[ByteBuffer.wrap(data).getInt()];
			input.read(data);
			int i = 0;
			byte[] d = new byte[data[i++]];
			for(int k = 0; k<d.length; i++, k++)
			{
				d[k] = data[i];
			}
			this.player = new String(d);
			this.difficulty = Difficulty.byId(data[i++]);
			this.score = ByteBuffer.wrap(data, i, 4).getInt();
			this.kills = ByteBuffer.wrap(data,i+4,2).getShort();
		}
		
		public void write(FileOutputStream output) throws IOException
		{
			byte[] data = new byte[this.getPlayer().length()+8];
			{
				int i = 0;
				data[i++] = (byte)this.getPlayer().length();
				byte[] d = this.getPlayer().getBytes();
				for(int k = 0; k<d.length; i++, k++)
				{
					data[i] = d[k];
				}
				data[i++] = (byte)this.difficulty.ordinal();
				ByteBuffer.wrap(data, i, 4).putInt(this.score);
				i+=4;
				ByteBuffer.wrap(data, i, 2).putShort(this.kills);
			}
			byte[] length = new byte[4];
			ByteBuffer.wrap(length).putInt(data.length);
			output.write(length);
			output.write(data);
		}
		
		public static void shift(ScorePosition pos)
		{
			for(int i = values().length-1, p = pos.ordinal(); i-- > p;)
			{
				values()[i+1].set(values()[i]);
			}
		}
	}
}
