package rocher.adrien.droidopenhomeplayer.Player.events;


public class EventLoaded implements EventBase {
	
//	public EventLoaded(Object source) {
//		super(source);
//
//	}
	
	public EnumPlayerEvents getType()
	{
		return EnumPlayerEvents.EVENTLOADED;
	}

}
