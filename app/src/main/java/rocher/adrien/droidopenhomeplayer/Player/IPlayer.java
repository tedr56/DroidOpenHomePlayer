package rocher.adrien.droidopenhomeplayer.Player;

/**
 * A general implementation of a Player
 */

import java.util.Observer;

import rocher.adrien.droidopenhomeplayer.Channel.ChannelBase;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelPlayList;
import rocher.adrien.droidopenhomeplayer.Player.events.EventBase;

public interface IPlayer {
	
	public void preLoadTrack(ChannelBase track);
	public  void loaded();
	
	//Player control
	public boolean playTrack(ChannelBase track,long volume, boolean mute);
	public void openFile(ChannelBase track);
	public void pause(boolean bPause);	
	public void resume();
	public void stop();
	public void seekAbsolute(long seconds);
	public void startTrack();
	public boolean isPlaying();

	//Volume Control
	public void setMute(boolean mute);
	public void setVolume(long volume);
	

	//Event Handling
	public void addObserver(Observer obj);
	public  void fireEvent(EventBase ev);
	public void updateInfo(String artist, String title);
	public void setStatus(String status);
	
	//Probably not used..
	public String getUniqueId();
	
	//Tidy up
	public void destroy();

	public void show();
	public void hide();
}
