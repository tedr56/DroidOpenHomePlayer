package rocher.adrien.droidopenhomeplayer.Player.events;

public class EventPlayListStatusChanged implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTPLAYLISTSTATUSCHANGED;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String status = "";

}
