package rocher.adrien.droidopenhomeplayer.Player.events;

public class EventStopSongcast implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTSTOPSONGCAST;
	}

}
