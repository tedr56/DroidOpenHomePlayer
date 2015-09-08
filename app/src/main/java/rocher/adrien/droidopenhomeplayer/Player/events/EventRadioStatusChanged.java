package rocher.adrien.droidopenhomeplayer.Player.events;

public class EventRadioStatusChanged implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTRADIOSTATUSCHANGED;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String status = "";
	

}
