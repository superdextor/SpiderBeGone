package us.emdex.halloween;

import java.awt.Color;
import java.awt.Font;

import static java.awt.Font.MONOSPACED;

public class Constants
{
	public static final String NAME = "Spider be Gone!";
	public static final String VERSION = "1.0";
	public static final int TPS = 1000 / 20;
	
	public static final String DIRECTORY;
	public static final String FILE_SETTINGS;
	public static final String FILE_SAVE;
	
	public static final String KEY_DIFFICULTY = "difficulty";
	public static final String KEY_VERSION = "version";
	public static final String KEY_FULLSCREEN = "fullscreen";
	public static final String KEY_SECRET = "dont_change_this_setting_or_else_bad_things_will_happen_dot_dot_dot_i_mean_it";
	
	public static final byte PTS_PER_SPIDER = 15;
	public static final byte PTS_PER_JUMP_SPIDER = 20;
	public static final byte PTS_PER_WEB = 1;
	
	public static final short PHASE_1_KILLS = 20;
	public static final short PHASE_2_KILLS = 50;
	public static final short PHASE_3_KILLS = 100;
	public static final short PHASE_4_KILLS = 200;
	public static final short PHASE_5_KILLS = 500;
	
	public static final Font FONT_LOADING = new Font(MONOSPACED, Font.BOLD, 24);
	public static final Font FONT_STATUS = new Font(MONOSPACED, Font.PLAIN, 16);
	public static final Font FONT_PARTICLE = new Font(MONOSPACED, Font.BOLD, 8);
	public static final Font FONT_HUD = new Font(MONOSPACED, Font.PLAIN, 12);
	
	public static final Color COLOR_BG = new Color(0,0,0,140);
	public static final Color COLOR_W = new Color(224,221,255,255);
	public static final Color COLOR_R = new Color(255,0,0,255);
	public static final Color COLOR_Y = new Color(255,216,0,255);
	public static final Color COLOR_G = new Color(76,255,0,255);
	public static final Color COLOR_B = new Color(0,0,0,255);
	public static final Color COLOR_BL = new Color(0,140,255,255);
	public static final Color COLOR_P = new Color(178,0,255,255);
	
	public static String getAppName()
	{
		return NAME + " " + VERSION;
	}
	
	static
	{
		DIRECTORY = System.getProperty("user.home")+"/Documents/SpiderBeGone";
		FILE_SETTINGS = "settings.txt";
		FILE_SAVE = "scores.dat";
	}
}
