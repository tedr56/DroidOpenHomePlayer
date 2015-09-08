package rocher.adrien.droidopenhomeplayer.Providers;

import rocher.adrien.droidopenhomeplayer.Utils.Config;
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgProduct1;

public class PrvProduct extends DvProviderAvOpenhomeOrgProduct1{

    private String friendly_name = Config.getInstance().getProductRoom();
    // private String iSourceXml =
    // "<SourceList><Source><Name>Playlist</Name><Type>Playlist</Type><Visible>1</Visible></Source><Source><Name>Receiver</Name><Type>Receiver</Type><Visible>1</Visible></Source><Source><Name>Radio</Name><Type>Radio</Type><Visible>1</Visible></Source></SourceList>";
    private String iSourceXml = "";
    // private boolean standby = true;
    private String attributes = "Info Time Volume Receiver Sender Credentials";
    // private String attributes = "";
    private String man_name = Config.getInstance().getManufacturerName();
    private String man_info = Config.getInstance().getManufacturerInfo();
    private String man_url = Config.getInstance().getManufacturerUrl();
    private String man_image = Config.getInstance().getManufacturerImageUri();
    private String model_name = Config.getInstance().getModelName();
    private String model_info = Config.getInstance().getModelInfo();
    private String model_url = Config.getInstance().getModelUrl();
    // "http://" + Config.ip + ":" + Config.port + "/" + Config.getInstance().getUdn() + Config.getInstance().getResourceURIPrefix()+"org/rpi/image/mediaplayer240.png";
//	private String model_image = Config.getInstance().getResourceURIPrefix()+"org/rpi/image/mediaplayer240.png";
    private String model_image = Config.getInstance().getModelImageUri();
    private String prod_room = Config.getInstance().getProductRoom();
    private String prod_name = Config.getInstance().getProductName();
    private String prod_info = Config.getInstance().getProductInfo();
    private String prod_url = Config.getInstance().getProductUrl();
    private String prod_image = Config.getInstance().getProductImageUri();

    //private PlayManager iPlayer = null;

    private long iSourceXMLChangeCount = 0;

    public PrvProduct(DvDevice dvDevice) {
        super(dvDevice);

        enablePropertyStandby();
        enablePropertyAttributes();
        enablePropertyManufacturerName();
        enablePropertyManufacturerInfo();
        enablePropertyManufacturerUrl();
        enablePropertyManufacturerImageUri();
        enablePropertyModelName();
        enablePropertyModelInfo();
        enablePropertyModelUrl();
        enablePropertyModelImageUri();
        enablePropertyProductRoom();
        enablePropertyProductName();
        enablePropertyProductInfo();
        enablePropertyProductUrl();
        enablePropertyProductImageUri();
        //enablePropertySourceIndex();
        //enablePropertySourceCount();
        //enablePropertySourceXml();

        setPropertyStandby(false);
        setPropertyAttributes(attributes);
        //
        setPropertyManufacturerName(man_name);
        setPropertyManufacturerInfo(man_info);
        setPropertyManufacturerUrl(man_url);
        setPropertyManufacturerImageUri(man_image);
        setPropertyModelName(model_name);
        setPropertyModelInfo(model_info);
        setPropertyModelUrl(model_url);
        setPropertyModelImageUri(model_image);
        setPropertyProductRoom(prod_room);
        setPropertyProductName(prod_name);
        setPropertyProductInfo(prod_info);
        setPropertyProductUrl(prod_url);
        setPropertyProductImageUri(prod_image);
        //
        ////setPropertySourceIndex(4);
        ////setPropertySourceCount(sources.size());
        //setPropertySourceXml(iSourceXml);
        //
        //
        enableActionManufacturer();
        enableActionModel();
        enableActionProduct();
        enableActionStandby();
        enableActionSetStandby();
        //enableActionSourceCount();
        //enableActionSourceXml();
        //enableActionSourceIndex();
        //enableActionSetSourceIndex();
        //enableActionSetSourceIndexByName();
        //enableActionSource();
        enableActionAttributes();
        //enableActionSourceXmlChangeCount();
        ////PlayManager.getInstance().observeProductEvents(this);
        ////PluginGateWay.getInstance().addObserver(this);
        //// initSources();
        ////setPropertySourceIndex(0);
    }
}
