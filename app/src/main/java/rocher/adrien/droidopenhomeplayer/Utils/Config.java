package rocher.adrien.droidopenhomeplayer.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.UUID;

import javax.json.JsonObject;

import org.apache.log4j.*;
import org.apache.log4j.net.SyslogAppender;

import de.bwaldvogel.log4j.*;

import rocher.adrien.droidopenhomeplayer.MainActivity;
import rocher.adrien.droidopenhomeplayer.Utils.NetworkUtils;
import rocher.adrien.droidopenhomeplayer.Utils.CustomPatternLayout;
import rocher.adrien.droidopenhomeplayer.Utils.MemoryAppender;
import rocher.adrien.droidopenhomeplayer.Utils.Utils;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.ContextWrapper;

enum Props {
	PRODUCT_ROOM("mediaplayer_room_name"),
	MEDIAPLAYER_PLAYER("mediaplayer_player"),
	MEDIAPLAYER_PLAYLIST_MAX("mediaplayer_playlist_max"),
	MEDIAPLAYER_ENABLE_AVTRANSPORT("mediaplayer_enable_avTransport"),
	MEDIAPLAYER_ENABLE_RECEIVER("mediaplayer_enable_receiver"),
	MEDIAPLAYER_STARTUP_VOLUME("mediaplayer_startup_volume"),
	MEDIAPLAYER_MAX_VOLUME("mediaplayer_max_volume"),
	MEDIAPLAYER_SAVE_LOCAL_PLAYLIST("mediaplayer_save_local_playlist"),
	MPLAYER_PLAY_DEFINITIONS("mplayer_play_definitions"),
	MPLAYER_PATH("mplayer_path"),
	MPLAYER_CACHE_SIZE("mplayer_cache_size"),
	MPLAYER_CACHE_MIN("mplayer_cache_min"),
	MPD_HOST("mpd_host"), MPD_PORT("mpd_port"),
	MPD_PRELOAD_TIMER("mpd_preload_timer"),
	LOG_FILE_NAME("log_file_name"),
	LOG_FILE_LEVEL("log_file_level"),
	LOG_CONSOLE_LEVEL("log_console_level"),
	LOG_SYSLOG_HOST("log_syslog_host"),
	LOG_SYSLOG_LEVEL("log_syslog_level"),
	LOG_JOURNAL_LEVEL("log_journal_level"),
	OPENHOME_PORT("openhome_port"),
	OPENHOME_LOG_LEVEL("openhome_log_level"),
	JAVA_SOUNDCARD_SUFFIX("java_soundcard_suffix"),
	JAVA_SOUND_SOFTWARE_MIXER_ENABLED("java_sound_software_mixer_enabled"),
	SONGCAST_LATENCY_ENABLED("songcast_latency_enabled"),
	RADIO_TUNEIN_USERNAME("radio_tunein_username"),
	RADIO_TUNEIN_PARTNERID("radio_tunein_partnerid"),
	WEB_SERVER_PORT("web_server_port"),
	WEB_SERVER_ENABLED("web_server_enabled"),
	AIRPLAY_ENABLED("airplay_enabled"),
	AIRPLAY_LATENCY_ENABLED("airplay_latency_enabled"),
	AIRPLAY_AUDIO_START_DELAY("airplay_audio_start_delay"),
	AIRPLAY_PORT("airplay_port");

	private final String stringValue;

	private Props(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}

enum CustomProductProps {
	PRODUCT_NAME("product_name"), 
	PRODUCT_INFO("product_info"), 
	PRODUCT_URL("product_url"),
	PRODUCT_IMAGE_URI("product_image_uri"),
	SERIAL_NUMBER("serial_number"),
	MANUFACTURER_NAME("manufacturer_name"),
	MANUFACTURER_INFO("manufacturer_info"),
	MANUFACTURER_URL("manufacturer_url"),
	MANUFACTURER_IMAGE_URI("manufacturer_image_uri"),
//	MODEL_NAME("model_name"), 
//	MODEL_INFO("model_info"), 
//	MODEL_URL("model_url"),
//	MODEL_IMAGE_URI("model_image_uri"),
    MEDIAPLAYER_SAVE_LOCAL_PLAYLIST_PATH("mediaplayer_save_local_playlist_path"),
	PACKAGED_IMAGE_FILENAME("packaged_image_filename");

	private final String stringValue;

	private CustomProductProps(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}
}

public class Config {

	private static Properties custom_product = null;
	
	private String version = "0.0.8.7";

	private static String udn = null;

	private Logger log = Logger.getLogger(this.getClass());

	private String songcast_nic_name = "";

	private String resourceURIPrefix = "";

	private String java_soundcard_name = "";

	private boolean java_sound_software_mixer_enabled = false;

	private static Properties pr = null;

	private static Calendar cal = null;

	private MemoryAppender memory_appender = null;

	private static Config instance = null;

    private static AppCompatActivity mActivity = null;

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
			instance.ConfigureLogging();
		}
		return instance;
	}
	
	private Config() {
		cal = Calendar.getInstance();
		getConfig();
	}

    public static void setActivity(AppCompatActivity MainActivity) {
        mActivity = MainActivity;
    }

	public static AppCompatActivity getActivity() {
		return mActivity;
	}

	public static int convertStringToInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {

		}
		return -99;
	}

	public static int convertStringToInt(String s, int iDefault) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {

		}
		return iDefault;
	}

	public static boolean convertStringToBoolean(String s, boolean bDefault) {
		if (s == null || s.equalsIgnoreCase(""))
			return bDefault;
		if (s.equalsIgnoreCase("TRUE"))
			return true;
		if (s.equalsIgnoreCase("YES"))
			return true;
		if (s.equalsIgnoreCase("1"))
			return true;
		return false;
	}

	public void setStartTime() {
		try {
			Date date = new Date();
			cal.setTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getStartTime() {
		return cal.getTime();
	}

	private static void getConfig() {
		try {
			pr = new Properties();
			pr.load(new FileInputStream("app.properties"));
		} catch (Exception e1) {
			try {
				pr = new Properties();
				pr.load(new FileInputStream("/etc/mediaplayer.conf"));
			} catch (Exception e2) {
				// Write error message to stderr because we don't have logging configured yet
				System.err.println("Error loading configuration file");
			}
		}
	}
	
	public void getCustomProductConfig() {
		try {
			custom_product = new Properties();
			custom_product.load(new FileInputStream("product.properties"));
			log.info("Custom product configuration loaded");
		} catch (Exception e) {
			log.debug("Unable to load custom product configuration file");
		}
	}

	/**
	 * Get the ProtocolInfo
	 * 
	 * @return
	 */
	public String getProtocolInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("http-get:*:audio/x-flac:*,");
		sb.append("http-get:*:audio/wav:*,");
		sb.append("http-get:*:audio/wave:*,");
		sb.append("http-get:*:audio/x-wav:*,");
		sb.append("http-get:*:audio/mpeg:*,");
		sb.append("http-get:*:audio/x-mpeg:*,");
		sb.append("http-get:*:audio/mp1:*,");
		sb.append("http-get:*:audio/aiff:*,");
		sb.append("http-get:*:audio/x-aiff:*,");
		sb.append("http-get:*:audio/x-m4a:*,");
		sb.append("http-get:*:audio/x-ms-wma:*,");
		sb.append("rtsp-rtp-udp:*:audio/x-ms-wma:*,");
		sb.append("http-get:*:audio/x-scpls:*,");
		sb.append("http-get:*:audio/x-mpegurl:*,");
		sb.append("http-get:*:audio/x-ms-asf:*,");
		sb.append("http-get:*:audio/x-ms-wax:*,");
		sb.append("http-get:*:audio/x-ms-wvx:*,");
		sb.append("http-get:*:text/xml:*,");
		sb.append("http-get:*:audio/aac:*,");
		sb.append("http-get:*:audio/aacp:*,");
		sb.append("http-get:*:audio/mp4:*,");
		sb.append("http-get:*:audio/ogg:*,");
		sb.append("http-get:*:audio/x-ogg:*,");
		sb.append("http-get:*:application/ogg:*,");
		sb.append("http-get:*:video/mpeg:*,");
		sb.append("http-get:*:video/mp4:*,");
		sb.append("http-get:*:video/quicktime:*,");
		sb.append("http-get:*:video/webm:*,");
		sb.append("http-get:*:video/x-ms-wmv:*,");
		sb.append("http-get:*:video/x-ms-asf:*,");
		sb.append("http-get:*:video/x-msvideo:*,");
		sb.append("http-get:*:video/x-ms-wax:*,");
		sb.append("http-get:*:video/x-ms-wvx:*,");
		sb.append("http-get:*:video/x-m4v:*,");
		sb.append("http-get:*:video/x-matroska:*,");
		sb.append("http-get:*:application/octet-stream:*");
		return sb.toString();
	}

	/**
	 * Get a String value from the properties map
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	private String getValue(Props key, String default_value) {
		if (pr == null)
			return default_value;
		try {
			return pr.getProperty(key.toString(), default_value);
		} catch (Exception e) {
			return default_value;
		}
	}

	/**
	 * Get a String value from the product properties map
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	private String getCustomProductValue(CustomProductProps key, String default_value) {
		if (custom_product == null)
			return default_value;
		try {
			return custom_product.getProperty(key.toString(), default_value);
		} catch (Exception e) {
		return default_value;
	}
	}
	

	/**
	 * Get an int value from the properties map
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	private int getValueInt(Props key, int default_value) {
		try {
			return convertStringToInt(getValue(key, "" + default_value));
		} catch (Exception e) {
		return default_value;
	}
	}

	/**
	 * Get a boolean value from the properties map
	 * 
	 * @param key
	 * @param default_value
	 * @return
	 */
	private boolean getValueBool(Props key, Boolean default_value) {
		try {
			return convertStringToBoolean(getValue(key, default_value.toString()), default_value);
		} catch (Exception e) {
			return default_value;
		}
	}

	/**
	 * @return the mediaplayer_room_name
	 */
	public String getProductRoom() {
		return getValue(Props.PRODUCT_ROOM, "Room");
		}

	/**
	 * @param mediaplayer_room_name
	 *            the mediaplayer_room_name to set
	 */
	public void setProductRoom(String mediaplayer_room_name) {
		pr.setProperty(Props.PRODUCT_ROOM.toString(), mediaplayer_room_name);
	}

	/**
	 * @return the mediaplayer_product_name
	 */
	public String getProductName() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.PRODUCT_NAME, Config.getInstance().getModelName());
	}

	/**
	 * @return the mediaplayer_product_info
	 */
	public String getProductInfo() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.PRODUCT_INFO, Config.getInstance().getModelInfo());
	}

	/**
	 * @return the mediaplayer_product_url
	 */
	public String getProductUrl() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.PRODUCT_URL, Config.getInstance().getModelUrl());
	}
	
	/**
	 * @return the product image URI
	 */
	public String getProductImageUri() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.PRODUCT_IMAGE_URI, Config.getInstance().getModelImageUri());
	}
	
	/**
	 * @return the serial number
	 */
	public String getSerialNumber() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.SERIAL_NUMBER, "00000000");
	}
	
	/**
	 * @return the manufacturer name
	 */
	public String getManufacturerName() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.MANUFACTURER_NAME, "Adrien Rocher");
	}

	/**
	 * @return the manufacturer info
	 */
	public String getManufacturerInfo() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.MANUFACTURER_INFO, "Pete Hoyle, with contributions from Markus M May, Marcello");
	}

	/**
	 * @return the manufacturer url
	 */
	public String getManufacturerUrl() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.MANUFACTURER_URL, "https://github.com/PeteManchester");
	}

	/**
	 * @return the manufacturer image URI
	 */
	public String getManufacturerImageUri() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.MANUFACTURER_IMAGE_URI, "http://www.openhome.org/mediawiki/skins/openhome/images/logo.png");
	}
	
	/**
	 * @return additional dir name to adjust packaged icons that are presented
	 */
	public String getPackagedImageFilename() {
		return Config.getInstance().getCustomProductValue(CustomProductProps.PACKAGED_IMAGE_FILENAME, "mediaplayer");
	}
	
	/**
	 * @return the model name
	 */
	public String getModelName() {
		return "OpenPlayer";
	}

	/**
	 * @return the model info
	 */
	public String getModelInfo() {
		return "OpenHome+UPnP/DLNA renderer/media player with Songcast & AirPlay receivers, version " + Config.getInstance().getVersion();
	}

	/**
	 * @return the model url
	 */
	public String getModelUrl() {
		return "https://github.com/tedr56/MediaPlayer";
	}
	
	/**
	 * @return the model image URI
	 */
	public String getModelImageUri() {
//		return "https://raw.githubusercontent.com/PeteManchester/MediaPlayer/master/com.upnp.mediaplayer/web/images/mediaplayer240.png";
		return Config.getInstance().getResourceURIPrefix()+"org/rpi/image/mediaplayer240.png";
	}

	/**
	 * @return the mediaplayer_player
	 */
	public String getMediaplayerPlayerType() {
		return getValue(Props.MEDIAPLAYER_PLAYER, "mplayer");
	}

	/**
	 * @param mediaplayer_player
	 *            the mediaplayer_player to set
	 */
	public void setMediaplayerPlayerType(String mediaplayer_player) {
		pr.setProperty(Props.MEDIAPLAYER_PLAYER.toString(), mediaplayer_player);
	}

	/**
	 * @return the mediaplayer_playlist_max
	 */
	public int getMediaplayerPlaylistMax() {
		return getValueInt(Props.MEDIAPLAYER_PLAYLIST_MAX, 1000);
	}

	/**
	 * @param mediaplayer_playlist_max
	 *            the mediaplayer_playlist_max to set
	 */
	public void setMediaplayerPlaylistMax(int mediaplayer_playlist_max) {
		// Config.mediaplayer_playlist_max = mediaplayer_playlist_max;
	}

	/**
	 * @return the mediaplayer_enable_avTransport
	 */
	public boolean isMediaplayerEnableAVTransport() {
		return getValueBool(Props.MEDIAPLAYER_ENABLE_AVTRANSPORT, true);
	}

	/**
	 * @param mediaplayer_enable_avTransport
	 *            the mediaplayer_enable_avTransport to set
	 */
	public void setgetMediaplayerPlaylistMax(boolean mediaplayer_enable_avTransport) {
		// Config.mediaplayer_enable_avTransport =
		// mediaplayer_enable_avTransport;
	}

	/**
	 * @return the mediaplayer_enable_receiver
	 */
	public boolean isMediaplayerEnableReceiver() {
		return getValueBool(Props.MEDIAPLAYER_ENABLE_RECEIVER, true);
	}

	/**
	 * @param mediaplayer_enable_receiver
	 *            the mediaplayer_enable_receiver to set
	 */
	public void setMediaplayerEnableReceiver(boolean mediaplayer_enable_receiver) {
		// Config.mediaplayer_enable_receiver = mediaplayer_enable_receiver;
	}

	/**
	 * @return the mediaplayer_startup_volume
	 */
	public long getMediaplayerStartupVolume() {
		return getValueInt(Props.MEDIAPLAYER_STARTUP_VOLUME, -1);
	}

	public long getMaxVolume() {
		return getValueInt(Props.MEDIAPLAYER_MAX_VOLUME, 100);
	}

	/**
	 * @param mediaplayer_startup_volume
	 *            the mediaplayer_startup_volume to set
	 */
	public void setMediaplayerStartupVolume(long mediaplayer_startup_volume) {
		// Config.mediaplayer_startup_volume = mediaplayer_startup_volume;
	}

	/**
	 * @return the mediaplayer_save_local_playlist
	 */
	public boolean isMediaplayerSaveLocalPlaylist() {
		return getValueBool(Props.MEDIAPLAYER_SAVE_LOCAL_PLAYLIST, true);
	}

	/**
	 * @param mediaplayer_save_local_playlist
	 *            the mediaplayer_save_local_playlist to set
	 */
	public void setMediaplayerSaveLocalPlaylist(boolean mediaplayer_save_local_playlist) {
		// Config.mediaplayer_save_local_playlist =
		// mediaplayer_save_local_playlist;
	}

	public void setMediaplayerSaveLocalPlaylistPath() {

	}
	public String MediaplayerSaveLocalPlaylistPath() {
        //return Config.getInstance().getCustomProductValue(CustomProductProps.MEDIAPLAYER_SAVE_LOCAL_PLAYLIST_PATH, "/data/rocher.adrien.droidopenhomeplayer/playlist.txt");
		if (mActivity == null) {
            return null;
        }
        else {
            String filepath = mActivity.getApplicationContext().getFilesDir()+ File.separator + "playlist.xml";
            return Config.getInstance().getCustomProductValue(CustomProductProps.MEDIAPLAYER_SAVE_LOCAL_PLAYLIST_PATH, mActivity.getApplicationContext().getFilesDir()+ File.separator + "playlist.xml");
        }
    }

	/**
	 * @return the mplayer_play_definitions
	 */
	public List<String> getMplayerPlayListDefinitions() {
		List<String> res = new ArrayList<String>();
		try {
			String lists = getValue(Props.MPLAYER_PLAY_DEFINITIONS, "asx,b4s,kpl,m3u,pls,ram,rm,smil,wax,wvx");
			String[] splits = lists.split(",");
			res = Arrays.asList(splits);
		} catch (Exception e) {

		}
		return res;
	}

	/**
	 * @param mplayer_play_definitions
	 *            the mplayer_play_definitions to set
	 */
	public void setMplayerPlayListDefinitions(List<String> mplayer_play_definitions) {
		// Config.mplayer_playlist_definitions = mplayer_play_definitions;
	}

	/**
	 * @return the mplayer_path
	 */
	public String getMPlayerPath() {
		return getValue(Props.MPLAYER_PATH, "mplayer");
	}

	/**
	 * @param mplayer_path
	 *            the mplayer_path to set
	 */
	public void setMPlayerPath(String mplayer_path) {
		// Config.mplayer_path = mplayer_path;
	}

	/**
	 * @return the mplayer_cache_size
	 */
	public int getMplayerCacheSize() {
		return getValueInt(Props.MPLAYER_CACHE_SIZE, 520);
	}

	/**
	 * @param mplayer_cache_size
	 *            the mplayer_cache_size to set
	 */
	public void setMPlayerCacheSize(int mplayer_cache_size) {
		// Config.mplayer_cache_size = mplayer_cache_size;
	}

	/**
	 * @return the mplayer_cache_min
	 */
	public int getMPlayerCacheMin() {
		return getValueInt(Props.MPLAYER_CACHE_MIN, 80);
	}

	/**
	 * @param mplayer_cache_min
	 *            the mplayer_cache_min to set
	 */
	public void setMPlayerCacheMin(int mplayer_cache_min) {
		// Config.mplayer_cache_min = mplayer_cache_min;
	}

	/**
	 * @return the mpd_host
	 */
	public String getMpdHost() {
		return getValue(Props.MPD_HOST, "localhost");
	}

	/**
	 * @param mpd_host
	 *            the mpd_host to set
	 */
	public void setMpdHost(String mpd_host) {
		// Config.mpd_host = mpd_host;
	}

	/**
	 * @return the mpd_port
	 */
	public int getMpdPort() {
		return getValueInt(Props.MPD_PORT, 6600);
	}

	/**
	 * @param mpd_port
	 *            the mpd_port to set
	 */
	public void setMpdPort(int mpd_port) {
		// Config.mpd_port = mpd_port;
	}

	/**
	 * @return the mpd_preload_timer
	 */
	public int getMpdPreloadTimer() {
		return getValueInt(Props.MPD_PRELOAD_TIMER, 2);
	}

	/**
	 * @param mpd_preload_timer
	 *            the mpd_preload_timer to set
	 */
	public void setMpdPreloadTimer(int mpd_preload_timer) {
		// Config.mpd_preload_timer = mpd_preload_timer;
	}

	/**
	 * @return the log_file_name
	 */
	public String getLogFileName() {
		return getValue(Props.LOG_FILE_NAME, "mediaplayer.log");
	}

	/**
	 * @param log_file_name
	 *            the log_file_name to set
	 */
	public void setLogFileName(String log_file_name) {
		// Config.log_file_name = log_file_name;
	}

	/**
	 * @return the log_file_level
	 */
	public String getLogFileLevel() {
		return getValue(Props.LOG_FILE_LEVEL, "off");
	}

	/**
	 * @param log_file_level
	 *            the log_file_level to set
	 */
	public void setLogFileLevel(String log_file_level) {
		// Config.log_file_level = log_file_level;
	}

	/**
	 * @return the log_console_level
	 */
	public String getLogConsoleLevel() {
		return getValue(Props.LOG_CONSOLE_LEVEL, "error");
	}

	/**
	 * @param log_console_level
	 *            the log_console_level to set
	 */
	public void setLogConsoleLevel(String log_console_level) {

	}

	/**
	 * @return the log_syslog_host
	 */
	public String getLogSyslogHost() {
		return getValue(Props.LOG_SYSLOG_HOST, "localhost");
	}

	/**
	 * @param log_syslog_host
	 *            the log_syslog_host to set
	 */
	public void setLogSyslogHost(String log_syslog_host) {
		// Config.log_syslog_host = log_syslog_host;
	}

	/**
	 * @return the log_syslog_level
	 */
	public String getLogSyslogLevel() {
		return getValue(Props.LOG_SYSLOG_LEVEL, "off");
	}

	/**
	 * @param log_syslog_level
	 *            the log_syslog_level to set
	 */
	public void setLogSyslogLevel(String log_syslog_level) {
		// Config.log_syslog_level = log_syslog_level;
	}

	/**
	 * @return the log_journal_level
	 */
	public String getLogJournalLevel() {
		return getValue(Props.LOG_JOURNAL_LEVEL, "off");
	}

	/**
	 * @param log_journal_level
	 *            the log_journal_level to set
	 */
	public void setLogJournalLevel(String log_journal_level) {
		// Config.log_journal_level = log_journal_level;
	}
	
	/**
	 * @return the openhome_port
	 */
	public int getOpenhomePort() {
		return getValueInt(Props.OPENHOME_PORT, 52821);
	}

	/**
	 * @param openhome_port
	 *            the openhome_port to set
	 */
	public void setOpenhomePort(int openhome_port) {
		// Config.openhome_port = openhome_port;
	}

	/**
	 * @return the openhome_log_level
	 */
	public String getOpenhomeLogLevel() {
		return getValue(Props.OPENHOME_LOG_LEVEL, "Error");
	}

	/**
	 * @param openhome_log_level
	 *            the openhome_log_level to set
	 */
	public void setOpenhomeLogLevel(String openhome_log_level) {
		// Config.openhome_log_level = openhome_log_level;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getJavaSoundcardSuffix() {
		List<String> res = new ArrayList<String>();
		try {
			String names = getValue(Props.JAVA_SOUNDCARD_SUFFIX, "[PLUGHW:0,0]--PRIMARY SOUND DRIVER");
			String[] list = names.split("--");
			res = Arrays.asList(list);
		} catch (Exception e) {
			log.error("Error parsing SoundCard Suffix", e);
		}

		return res;
	}

	/**
	 * 
	 * @return
	 */
	public String getJavaSoundcardName() {
		return java_soundcard_name;
	}

	/**
	 * @param java_soundcard_name
	 *            the songcast_soundcard_name to set
	 */
	public void setJavaSoundcardName(String java_soundcard_name) {
		this.java_soundcard_name = "";
		if (!Utils.isEmpty(java_soundcard_name)) {
			this.java_soundcard_name = "#" + java_soundcard_name;
		}
	}

	/**
	 * @return the radio_tunein_username
	 */
	public String getRadioTuneinUsername() {
		return getValue(Props.RADIO_TUNEIN_USERNAME, "");
	}

	/**
	 * @param radio_tunein_username
	 *            the radio_tunein_username to set
	 */
	public void setRadioTuneinUsername(String radio_tunein_username) {
		// this.radio_tunein_username = radio_tunein_username;
	}

	/**
	 * 
	 * @return
	 */
	public String getRadioTuneInPartnerId() {
		return getValue(Props.RADIO_TUNEIN_PARTNERID, "");
	}

	/**
	 * @return the web_server_enabled
	 */
	public boolean isWebWerverEnabled() {
		return getValueBool(Props.WEB_SERVER_ENABLED, true);
	}

	/**
	 * @param web_server_enabled
	 *            the web_server_enabled to set
	 */
	public void setWebServerEnabled(boolean web_server_enabled) {
		// Config.web_server_enabled = web_server_enabled;
	}

	/**
	 * @return the songcast_latency_enabled
	 */
	public boolean isSongcastLatencyEnabled() {
		return getValueBool(Props.SONGCAST_LATENCY_ENABLED, false);
	}

	/**
	 * @param songcast_latency_enabled
	 *            the songcast_latency_enabled to set
	 */
	public void setSongcastLatencyEnabled(boolean songcast_latency_enabled) {
		// Config.songcast_latency_enabled = songcast_latency_enabled;
	}

	/**
	 * @return the web_server_port
	 */
	public String getWebServerPort() {
		return getValue(Props.WEB_SERVER_PORT, "8088");
	}

	/**
	 * @param web_server_port
	 *            the web_server_port to set
	 */
	public void setWebServerPort(String web_server_port) {
		// Config.web_server_port = web_server_port;
	}

	public boolean isAirPlayEnabled() {

		return getValueBool(Props.AIRPLAY_ENABLED, true);
	}
	
	public boolean isAirPlayLatencyEnabled()
	{
		return getValueBool(Props.AIRPLAY_LATENCY_ENABLED,false);
	}
	
	public boolean isAirPlayStartAudioDelayEnabled()
	{
		return getValueBool(Props.AIRPLAY_AUDIO_START_DELAY,false);
	}

	/**
	 * 
	 * @return
	 */
	public int getAirPlayPort() {
		return getValueInt(Props.AIRPLAY_PORT, 5000);
	}

	/***
	 * Set up our logging
	 */
	private void ConfigureLogging() {
		// We only want to see important messages from third party packages
		Logger.getLogger("io.netty").setLevel(Level.ERROR);
		Logger.getLogger("org.glassfish").setLevel(Level.ERROR);
		Logger.getLogger("org.jvnet.hk2").setLevel(Level.ERROR);
		Logger.getLogger("net.xeoh.plugins.base.impl").setLevel(Level.ERROR);
		Logger.getLogger("javax.jmdns.impl").setLevel(Level.ERROR);
		Level l = null;
		try {
			if (!"off".equalsIgnoreCase(getLogFileLevel())) {
				CustomPatternLayout lpl = new CustomPatternLayout();
				lpl.setConversionPattern("%d %5p: %m%n");
				lpl.activateOptions();					
			RollingFileAppender fileAppender = new RollingFileAppender();
			fileAppender.setName("fileAppender");
			fileAppender.setAppend(true);
			fileAppender.setMaxFileSize("5mb");
			fileAppender.setMaxBackupIndex(5);
			fileAppender.setFile(getLogFileName());
				l = getLogLevel(getLogFileLevel());
				if (Logger.getRootLogger().getLevel().isGreaterOrEqual(l)) {
					Logger.getRootLogger().setLevel(l);
				}
				fileAppender.setThreshold(l);
				fileAppender.setLayout(lpl);
			fileAppender.activateOptions();
			Logger.getRootLogger().addAppender(fileAppender);
			}
			
			CustomPatternLayout cpl = new CustomPatternLayout();
			cpl.setConversionPattern("%d %-5p [%t] [%-10c]: %m%n");
			cpl.activateOptions();
			
			if (!"off".equalsIgnoreCase(getLogConsoleLevel())) {
			ConsoleAppender consoleAppender = new ConsoleAppender();
			consoleAppender.setName("consoleLayout");
				consoleAppender.setLayout(cpl);
			consoleAppender.activateOptions();
				l = getLogLevel(getLogConsoleLevel());
				if (Logger.getRootLogger().getLevel().isGreaterOrEqual(l)) {
					Logger.getRootLogger().setLevel(l);
				}
				consoleAppender.setThreshold(l);
			Logger.getRootLogger().addAppender(consoleAppender);
			}
			
			if (isWebWerverEnabled()) {
				memory_appender = new MemoryAppender();
				memory_appender.setLayout(cpl);
			memory_appender.activateOptions();
			memory_appender.setThreshold(Level.DEBUG);
			Logger.getRootLogger().addAppender(memory_appender);
			}
			
			PatternLayout spl = new PatternLayout();
			spl.setConversionPattern("mediaplayer: %-5p [%t]: %m%n");
			spl.activateOptions();
			if (!"off".equalsIgnoreCase(getLogSyslogLevel())) {
				SyslogAppender syslogAppender = new SyslogAppender();
				syslogAppender.setName("syslogger");
				syslogAppender.setSyslogHost(getLogSyslogHost());
				syslogAppender.setFacility("LOCAL7");
				l = getLogLevel(getLogSyslogLevel());
				if (Logger.getRootLogger().getLevel().isGreaterOrEqual(l)) {
					Logger.getRootLogger().setLevel(l);
				}			
				syslogAppender.setThreshold(l);
				syslogAppender.setLayout(spl);
				syslogAppender.setHeader(true);
				syslogAppender.activateOptions();
				Logger.getRootLogger().addAppender(syslogAppender);
			}

			if (!"off".equalsIgnoreCase(getLogJournalLevel())) {
				SystemdJournalAppender journalAppender = new SystemdJournalAppender();
				journalAppender.setName("journal");
				l = getLogLevel(getLogJournalLevel());
				if (Logger.getRootLogger().getLevel().isGreaterOrEqual(l)) {
					Logger.getRootLogger().setLevel(l);
				}
				journalAppender.setThreshold(l);
				journalAppender.setLogLoggerName(true);
				journalAppender.setLogThreadName(true);
				journalAppender.setLogStacktrace(true);
				journalAppender.activateOptions();
				Logger.getRootLogger().addAppender(journalAppender);
			}
					
		} catch (Exception e) {
			System.err.println("Error configuring logging");
		}
		}

	public void printLoggingConfig() {
		log.debug("Logging configured as:");
		log.debug("  File:    " + getLogFileName() + " (level: " + getLogFileLevel() + ")");
		log.debug("  Console: " + !(getLogConsoleLevel().equalsIgnoreCase("off")) + " (level: " + getLogConsoleLevel() + ")");
		log.debug("  Syslog:  " + getLogSyslogHost() + " (level: " + getLogSyslogLevel() + ")");
		log.debug("  Journal: " + !(getLogJournalLevel().equalsIgnoreCase("off")) + " (level: " + getLogJournalLevel() + ")");
	}

	private Level getLogLevel(String s) {
		return Level.toLevel(s, Level.DEBUG);
	}

	public String getVersion() {
		return version;
	}

	public String getUdn() {
		if (udn == null) {
			udn = generateUdn();
		}
		return udn;
	}

	public String getSongCastNICName() {
		return songcast_nic_name;
	}

	public void setSongCastNICName(String nic) {
		songcast_nic_name = nic;
	}

	/**
	 * Change the Console log level
	 * 
	 * @param level
	 */
	private void changeConsoleLogLevel(String level) {
		try {
			ConsoleAppender append = (ConsoleAppender) LogManager.getRootLogger().getAppender("consoleLayout");
			append.setThreshold(getLogLevel(level));
			log.warn("Console Log Level Changed to: " + level);
		} catch (Exception e) {
			log.error("Error setting Console Log Level", e);
		}
	}

	/**
	 * Change the LogFile log level
	 * 
	 * @param level
	 */
	private void changeFileLogLevel(String level) {
		try {
			RollingFileAppender append = (RollingFileAppender) LogManager.getRootLogger().getAppender("fileAppender");
			append.setThreshold(getLogLevel(level));
			log.warn("File Log Level Changed to: " + level);
		} catch (Exception e) {
			log.error("Error setting File Log Level", e);
		}
	}

	/**
	 * Change the syslog log level
	 * 
	 * @param level
	 */
	private void changeSyslogLogLevel(String level) {
		try {
			SyslogAppender append = (SyslogAppender) LogManager.getRootLogger().getAppender("syslogger");
			append.setThreshold(getLogLevel(level));
			log.info("Syslog log level changed to: " + level);
		} catch (Exception e) {
			log.warn("Error setting syslog log level", e);
		}
	}

	/**
	 * Change the systemd-journal log level
	 * 
	 * @param level
	 */
	private void changeJournalLogLevel(String level) {
		try {
			SystemdJournalAppender append = (SystemdJournalAppender) LogManager.getRootLogger().getAppender("journal");
			append.setThreshold(getLogLevel(level));
			log.info("Journal log level changed to: " + level);
		} catch (Exception e) {
			log.warn("Error setting journal log level", e);
		}
	}
	
	/**
	 * Update the app.properties file
	 * 
	 * @param configObject
	 */
	public void updateConfig(JsonObject configObject) {
		try {
			for (String key : configObject.keySet()) {
				String value = configObject.getString(key);
				log.debug("Key: " + key + " = " + value);
				if (key.equalsIgnoreCase(Props.LOG_CONSOLE_LEVEL.toString())) {
					if (!value.toString().equalsIgnoreCase(pr.getProperty(Props.LOG_CONSOLE_LEVEL.toString()))) {
						changeConsoleLogLevel(value);
					}
				} else if (key.equalsIgnoreCase(Props.LOG_FILE_LEVEL.toString())) {
					if (!value.toString().equalsIgnoreCase(pr.getProperty(Props.LOG_FILE_LEVEL.toString()))) {
						changeFileLogLevel(value);
					}
				}
				pr.put(key, value);
			}
		} catch (Exception e) {

		}
		OutputStream out = null;
		try {

			File f = new File("app.properties");
			out = new FileOutputStream(f);
			Properties tmp = new Properties() {
				@Override
				public synchronized Enumeration<Object> keys() {
					return Collections.enumeration(new TreeSet<Object>(super.keySet()));
				}
			};
			tmp.putAll(pr);
			tmp.store(new FileWriter(f), "Modified Using the Web Page");

		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void setResourceURIPrefix(String resourceURIPrefix) {
		this.resourceURIPrefix = resourceURIPrefix;
	}

	public String getResourceURIPrefix() {
		return resourceURIPrefix;
	}

	public String getLoggingEvents() {
		String text = "";
		try {
			text = URLEncoder.encode(memory_appender.getEventString(), "UTF-8");
		} catch (Exception e) {

		}
		return text;
	}

	/**
	 * Generate a stable UDN
	 * 
	 * @return string UDN (UUID)
	 */
	public String generateUdn() {
		String info = getManufacturerName() + getProductName() + getSerialNumber();
		byte[] salt = info.getBytes();
		byte[] mac = NetworkUtils.getMacAddress();
		byte[] input = new byte[mac.length + salt.length];
		for (int i = 0; i < input.length; ++i) {
		    input[i] = i < salt.length ? salt[i] : mac[i - salt.length];
		}
		return UUID.nameUUIDFromBytes(input).toString();
	}
	
	/**
	 * @return the java_sound_software_mixer_enabled
	 */
	public boolean isSoftwareMixerEnabled() {
		return getValueBool(Props.JAVA_SOUND_SOFTWARE_MIXER_ENABLED, false);
	}

}
