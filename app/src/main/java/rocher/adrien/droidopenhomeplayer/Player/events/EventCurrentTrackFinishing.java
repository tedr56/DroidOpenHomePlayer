package rocher.adrien.droidopenhomeplayer.Player.events;

public class EventCurrentTrackFinishing implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTCURRENTTRACKFINISHING;
	}

}
