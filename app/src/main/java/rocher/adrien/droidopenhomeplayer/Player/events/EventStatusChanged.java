package rocher.adrien.droidopenhomeplayer.Player.events;

import rocher.adrien.droidopenhomeplayer.Channel.ChannelBase;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelPlayList;

public class EventStatusChanged implements EventBase {
	
	public EnumPlayerEvents getType()
	{
		return EnumPlayerEvents.EVENTSTATUSCHANGED;
	}
	
	private ChannelBase current_track = null;

//	public EventStatusChanged(Object source) {
//		super(source);
//	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	private String status = "";

	public void setTrack(ChannelBase current_track) {
		this.current_track = current_track;
	}
	
	public ChannelBase getTrack()
	{
		return current_track;
	}

}
