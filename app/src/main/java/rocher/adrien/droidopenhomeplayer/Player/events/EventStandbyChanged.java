package rocher.adrien.droidopenhomeplayer.Player.events;


public class EventStandbyChanged implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTSTANDBYCHANGED;
	}
	
	public boolean isStandby() {
		return standby;
	}

	public void setStandby(boolean standby) {
		this.standby = standby;
	}

	private boolean standby = true;

}
