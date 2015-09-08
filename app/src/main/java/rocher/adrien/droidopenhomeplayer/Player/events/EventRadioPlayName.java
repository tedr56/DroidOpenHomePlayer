package rocher.adrien.droidopenhomeplayer.Player.events;

public class EventRadioPlayName implements EventBase {

	@Override
	public EnumPlayerEvents getType() {
		return EnumPlayerEvents.EVENTRADIOPLAYNAME;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name = "";
	

}
