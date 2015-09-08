package rocher.adrien.droidopenhomeplayer.Player.events;

import rocher.adrien.droidopenhomeplayer.Player.TrackInfo;

public class EventUpdateTrackInfo implements EventBase {

private TrackInfo trackInfo;

//	public EventUpdateTrackInfo(Object source) {
//		super(source);
//	}
	
	public EnumPlayerEvents getType()
	{
		return EnumPlayerEvents.EVENTUPDATETRACKINFO;
	}

	public void setTrackInfo(TrackInfo trackInfo) {
		this.trackInfo = trackInfo;
		
	}
	
	public TrackInfo getTrackInfo()
	{
		return trackInfo;
	}
	
}
