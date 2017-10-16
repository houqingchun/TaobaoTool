import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;


public class Constants {


public static final String EACH_PRODUCT_NOTE_FILENAME = MyConfig.getText("product.note.name");

	public static final String UPLOAD_ERROR_FILENAME = MyConfig.getText("upload.error");

	public static final String UPLOAD_SUCCESS_FILENAME = MyConfig.getText("upload.success");
	
	public static final String DOWNLOAD_ERROR_FILENAME = MyConfig.getText("download.error");

	public static final String DOWNLOAD_SUCCESS_FILENAME = MyConfig.getText("download.success");
	
	public static final String MODIFIED_ERROR_FILENAME = MyConfig.getText("modified.error");
	public static final String MODIFIED_SUCCESS_FILENAME = MyConfig.getText("modified.success");
	public static final String TM_DISMISSED_PRODUCTS_FILENAME = MyConfig.getText("tm.dismissed.products");
	public static final String TM_CHANGED_PRODUCTS_FILENAME = MyConfig.getText("tm.changed.products");
	public static final String TM_INCREASE_PRODUCTS_FILENAME = MyConfig.getText("tm.increase.products");
	
	public static final String TM_MENU_LINKS_FILENAME = MyConfig.getText("tm.menu.links");
	
	public static final String TM_SEPERATOR = "###";

	public static final String SHARE_FOLDER_PATH = MyConfig.getText("root.folder");

	public static final String LINKS_FILENAME = MyConfig.getText("links");

	public static final int PIC_MAX_WIDTH = Integer.valueOf(MyConfig.getText("pic.max.width"));
	
	public static final int PIC_MAX_HEIGHT = Integer.valueOf(MyConfig.getText("pic.max.height"));
	
	public static final int PUBLISH_TRY_TIMES = Integer.valueOf(MyConfig.getText("publish.try.times"));
	
	public static final int DOWNLOAD_TRY_TIMES = Integer.valueOf(MyConfig.getText("download.try.times"));
	
	public static final int PUBLISH_TO_MOBIEL_MAX_WAIT = Integer.valueOf(MyConfig.getText("publish.mobile.max.wait")); //million seconds
	
	public static final boolean ENABLE_WATERMARK = Boolean.valueOf(MyConfig.getText("enable.watermark"));

	public static final String HOME_PAGE = MyConfig.getText("download.home");

	public static final String LOGIN_PAGE = MyConfig.getText("login.page");
	
	public static final String SOURCE_DETAIL_VIEW_PAGE = MyConfig.getText("source.detail.view.page");
	
	public static final String TARGET_DETAIL_VIEW_PAGE = MyConfig.getText("target.detail.view.page");
	
	public static final String SELLER_HOMEPAGE = MyConfig.getText("seller.page");
	
	public static final String PHOTO_PAGE = MyConfig.getText("photo.space");

	public static final String PROXY_HOST = MyConfig.getText("proxy.host");
 
	public static final int PROXY_PORT = Integer.valueOf(MyConfig.getText("proxy.port"));
	
	public static final boolean USE_PROXY = Boolean.valueOf(MyConfig.getText("proxy.use"));
	
	public static final String DESC_TYPE_PS = "PS";
	
	public static final String DESC_TYPE_ATTENTION = "ATTENTION";
	
	public static final String DESC_TYPE_NORMAL = "NORMAL";
	
	public static final String DESC_TYPE_BOTTOM_NOTES = "BOTTOM_NOTES";
	
	public static final String DESC_TYPE_TOP_NOTES = "TOP_NOTES";
	
	public static final String MAPKEY_URL = ">>#URL#";

	public static final String MAPKEY_MENU = ">>#MENU#";

	public static final String MAPKEY_FOLDER = ">>#FOLDER#";

	public static final String MAPKEY_TITLE = ">>#TITLE#";

	public static final String MAPKEY_PARAM = ">>#PARAM#";

	public static final String MAPKEY_MAIN = ">>#MAIN#";

	public static final String MAPKEY_PRICE = ">>#PRICE#";

	public static final String MAPKEY_DETAILS = ">>#DETAILS#";
	
	public static final String MAPKEY_PROPERTIES = ">>#PROPERTIES#";
	
	
	public static final Set<String> UPLOADED_LINE_SET = new HashSet<String>();
	static {
		//上传成功记录
		String[] spans = MyConfig.getText("product.ignore.lines").split(",");
		for (String span : spans){
			String[] tmp = span.split("-"); 
			if (tmp.length == 2){
				int start = Integer.valueOf(tmp[0]);
				int end = Integer.valueOf(tmp[1]);
				for (int i = start; i <= end; i++){
					UPLOADED_LINE_SET.add(String.valueOf(i));
				}
			}else{
				UPLOADED_LINE_SET.add(span);
			}
		}
	}
}


class MyConfig{
	private static ResourceBundle bundle = ResourceBundle.getBundle("config");
	public static String getText(String key){		
		return bundle.getString(key);
	}
}
