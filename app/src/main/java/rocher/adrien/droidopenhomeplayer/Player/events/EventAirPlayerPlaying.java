package rocher.adrien.droidopenhomeplayer.Player.events;



public class EventAirPlayerPlaying implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTAIRPLAYERPLAYING;
	}

}
