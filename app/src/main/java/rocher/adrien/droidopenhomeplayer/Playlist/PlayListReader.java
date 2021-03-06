package rocher.adrien.droidopenhomeplayer.Playlist;

import org.apache.log4j.Logger;

import java.io.File;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import rocher.adrien.droidopenhomeplayer.Channel.ChannelPlayList;
import rocher.adrien.droidopenhomeplayer.Providers.PrvPlayList;
import rocher.adrien.droidopenhomeplayer.Utils.Config;
import rocher.adrien.droidopenhomeplayer.Utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PlayListReader {

	private CopyOnWriteArrayList<ChannelPlayList> tracks = new CopyOnWriteArrayList<ChannelPlayList>();

	private static Logger log = Logger.getLogger(PlayListReader.class);
	private PrvPlayList iPlayList = null;
	//private String playlistPath = Config.getInstance().MediaplayerSaveLocalPlaylistPath();

	private int max_id = 0;

	public PlayListReader(PrvPlayList iPlayList) {
		this.iPlayList = iPlayList;
	}

	public String getXML() {
		tracks.clear();
		try {
			long startTime = System.nanoTime();
			String playlistPath = Config.getInstance().MediaplayerSaveLocalPlaylistPath();
			File file = new File(playlistPath);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			NodeList listOfChannels = doc.getElementsByTagName("Entry");
			for (int s = 0; s < listOfChannels.getLength(); s++) {
				Node channel = listOfChannels.item(s);
				Element element = (Element) channel;
				String id = XMLUtils.getStringFromElement(element, "Id");
				int iId = Integer.parseInt(id);
				if(iId > max_id)
				{
					max_id = iId;
				}
				String url = XMLUtils.getStringFromElement(element, "Uri");
				String metadata = XMLUtils.getStringFromElement(element, "Metadata");
				ChannelPlayList t = new ChannelPlayList(url, metadata, Integer.parseInt(id));
				tracks.add(t);
				log.debug("Adding Track Id: " + id + " URL: " + url +  " " + t.getFullDetails());
			}
			iPlayList.setNextId(max_id);
			iPlayList.setTracks(tracks);
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			//log.warn("Time to Add CustomTracks: " + duration);
			//log.debug("HoldHere");
		} catch (Exception e) {
			log.warn("Unable to load playlist XML file");
		}
		return "";
	}

}
