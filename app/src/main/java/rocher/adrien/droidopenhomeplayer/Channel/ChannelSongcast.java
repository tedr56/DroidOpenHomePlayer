package rocher.adrien.droidopenhomeplayer.Channel;

public class ChannelSongcast extends ChannelBase {
	
	public ChannelSongcast(String uri, String metadata, int id) {
		super(uri, metadata, id);
	}
	
	@Override
	public String getMetaText()
	{
		String text = super.getMetaText();
		
		if(text.equalsIgnoreCase(""))
		{
			return super.getMetadata();
		}
			return text;
	}
}
