package rocher.adrien.droidopenhomeplayer.Playlist;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelPlayList;
import rocher.adrien.droidopenhomeplayer.Utils.Config;

public class PlayListWriter extends Thread {

	private boolean save = false;
	private int counter = 0;
	private CopyOnWriteArrayList<ChannelPlayList> tracks = null;
	private Logger log = Logger.getLogger(PlayListWriter.class);

    //private String playlistPath = Config.getInstance().MediaplayerSaveLocalPlaylistPath();
	public PlayListWriter() {
		this.setName("PlayListWriter");
	}

	public void trigger(CopyOnWriteArrayList<ChannelPlayList> tracks) {
		this.tracks = tracks;
		save = true;
		counter = 0;
	}

	private String getList() {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("<TrackList>");
		for (ChannelPlayList t : tracks) {
			i++;
			sb.append(t.getFullText());
		}
		sb.append("</TrackList>");
		log.debug("PlayList Contains : " + i);
		return sb.toString();
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (save && Config.getInstance().isMediaplayerSaveLocalPlaylist()) {
					if (counter < 5) {
						counter++;
					} else {
						String xml = getList();
						log.debug("Saving PlayList: " );
						FileWriter out = null;
						try {
							String s = new String(xml.getBytes(), "UTF-8");
							String playlistPath = Config.getInstance().MediaplayerSaveLocalPlaylistPath();
							out = new FileWriter(playlistPath);
							try {
								out.write(s);
							} finally {
								out.close();
							}
							save = false;
							log.debug("Saved PlayList");
						} catch (Exception e) {
							log.error("Unable to write playlist XML");
						}
					}
				}
				Thread.sleep(1000);
			}

		} catch (Exception e) {
			log.error("Unable to write playlist XML");
		}
	}

}
