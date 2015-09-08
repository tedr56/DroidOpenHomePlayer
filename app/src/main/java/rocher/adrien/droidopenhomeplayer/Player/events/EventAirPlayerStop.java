package rocher.adrien.droidopenhomeplayer.Player.events;
/**
 * Used to indicate to the AirPlayer Server to stop playing
 * @author phoyle
 *
 */

public class EventAirPlayerStop implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTAIRPLAYERSTOP;
	}

}
