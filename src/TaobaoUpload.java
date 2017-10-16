import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class TaobaoUpload {
	WebDriver driver = null;
	WebDriverUtils driverUtils = null;
	PhotoLib photoLib = null;
	TaobaoDownload download = null;

	String categoryKey = "";
	String selectItemXpath = "";
	
	/**
	 * new String[属性名称，Xpath/CssSelector，元素类型, ByType]
	 * 元素类型：
	 * 1 >　INPUT
	 * 2 > SELECT	
	 * 3 >　CHECKBOX	
	 * 
	 * ByType:
	 * 1 > Xpath
	 * 2 > CssSelector
	 */
	List<String[]> propertiesAryList = new ArrayList<String[]>();
	
	public TaobaoUpload(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().window().maximize();
		driverUtils = new WebDriverUtils(driver);
		photoLib = new PhotoLib(this.driver);
		download = new TaobaoDownload(driver);
	}

	public void navigateToBuyerCenter() throws Exception {
//		driverUtils.clickOnElement(By.linkText("卖家中心"));
		driver.get(Constants.SELLER_HOMEPAGE);
		// clickOnElement(By.linkText("免费开店"));
		// clickOnElement(By.id("J_SiteNavLogin"));
		// clickOnElement(By.xpath("//*[@id=\"J_SiteNavLoginPanel\"]/div/div[2]/p[1]/a[1]"));
		// clickOnElement(By.xpath("//*[@id=\"J_SiteNavBdR\"]/li[6]/div[1]/a"));
		// clickOnElement(By.xpath("//*[@id=\"J_SiteNavBdR\"]/li[6]/div[2]/div/a[1]"));

		// *[@id="J_SiteNavSeller"]/div[1]/a/span

	}


	/**
	 * 根据给定文件，提取产品详细信息
	 * 
	 * @param notesPath
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> generateProInfoMapFromFile(String notesPath) throws Exception {
		// 读取链接文件，开始下载产品信息
		Map<String, Object> map = new HashMap<String, Object>();
		String thisLine = null;
		BufferedReader br = null;
		String currentKey = null;
		// open input stream test.txt for reading purpose.
		br = new BufferedReader(new FileReader(notesPath));
		while ((thisLine = br.readLine()) != null) {
			if (thisLine.trim().equals("")) {
				continue;
			}

			if (thisLine.startsWith(Constants.MAPKEY_TITLE)) {
				currentKey = Constants.MAPKEY_TITLE;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_URL)) {
				currentKey = Constants.MAPKEY_URL;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_MENU)) {
				currentKey =Constants.MAPKEY_MENU;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_FOLDER)) {
				currentKey = Constants.MAPKEY_FOLDER;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_PARAM)) {
				currentKey = Constants.MAPKEY_PARAM;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_MAIN)) {
				currentKey = Constants.MAPKEY_MAIN;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_DETAILS)) {
				currentKey = Constants.MAPKEY_DETAILS;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_PRICE)) {
				currentKey = Constants.MAPKEY_PRICE;
				continue;
			} else if (thisLine.startsWith(Constants.MAPKEY_PROPERTIES)) {
				currentKey = Constants.MAPKEY_PROPERTIES;
				continue;
			}

			if (Constants.MAPKEY_URL.equals(currentKey)) {
				map.put(Constants.MAPKEY_URL, thisLine);
			} else if (Constants.MAPKEY_FOLDER.equals(currentKey)) {
				map.put(Constants.MAPKEY_FOLDER, thisLine);
			} else if (Constants.MAPKEY_MENU.equals(currentKey)) {
				map.put(Constants.MAPKEY_MENU, thisLine);
			} else if (Constants.MAPKEY_TITLE.equals(currentKey)) {
				String newTitle = thisLine;
				
				if (map.containsKey(Constants.MAPKEY_TITLE)) {
					newTitle = map.get(Constants.MAPKEY_TITLE) + newTitle;
				}
				map.put(Constants.MAPKEY_TITLE, newTitle);
				
			} else if (Constants.MAPKEY_PARAM.equals(currentKey)) {
				map.put(Constants.MAPKEY_PARAM, thisLine);
			} else if (Constants.MAPKEY_MAIN.equals(currentKey)) {
				List<String> list;
				if (map.containsKey(Constants.MAPKEY_MAIN)) {
					list = (List<String>) map.get(Constants.MAPKEY_MAIN);
				} else {
					list = new ArrayList();
				}
				list.add(thisLine.split(Constants.TM_SEPERATOR)[1]);
				map.put(Constants.MAPKEY_MAIN, list);
			} else if (Constants.MAPKEY_PRICE.equals(currentKey)) {
				List<String> list;
				if (map.containsKey(Constants.MAPKEY_PRICE)) {
					list = (List<String>) map.get(Constants.MAPKEY_PRICE);
				} else {
					list = new ArrayList();
				}
				list.add(thisLine);
				map.put(Constants.MAPKEY_PRICE, list);
			} else if (Constants.MAPKEY_DETAILS.equals(currentKey)) {
				List<String> list;
				if (map.containsKey(Constants.MAPKEY_DETAILS)) {
					list = (List<String>) map.get(Constants.MAPKEY_DETAILS);
				} else {
					list = new ArrayList();
				}
				list.add(thisLine);
				map.put(Constants.MAPKEY_DETAILS, list);
			}else if (Constants.MAPKEY_PROPERTIES.equals(currentKey)) {
				List<String> list;
				if (map.containsKey(Constants.MAPKEY_PROPERTIES)) {
					list = (List<String>) map.get(Constants.MAPKEY_PROPERTIES);
				} else {
					list = new ArrayList();
				}
				list.add(thisLine);
				map.put(Constants.MAPKEY_PROPERTIES, list);
			} 
		}
		if (br != null) {
			br.close();
		}
		
		return map;
	}
	
	public void startUpdateProduct(int start, int end) throws Exception {
		System.out.println("Start updating....");
//		this.navigateToBuyerCenter();
		
		String updateErrorFile = Constants.SHARE_FOLDER_PATH + "/" + Constants.MODIFIED_ERROR_FILENAME;
		String updateSuccessFile = Constants.SHARE_FOLDER_PATH + "/" + Constants.MODIFIED_SUCCESS_FILENAME;
		
		Set<String> updatedProducts = this.getUpdatedProductsFromLogs(updateSuccessFile);
		
		// 读取链接文件，开始下载产品信息
		String thisLine = null;
		BufferedReader br = null;
		int errorCount = 0;
		int successCount = 0;
		int dupCount = 0;
		int total = (end - start + 1);
		if (total == 1){
			total = 9999;
		}
		int seq = 0;
		int processCount = 0;
		
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(Constants.SHARE_FOLDER_PATH + "/"
					+ Constants.UPLOAD_SUCCESS_FILENAME));
			while ((thisLine = br.readLine()) != null) {
				//COS服装/儿童款/N0750002###COS服装/儿童款###7073412270###孩派天津 冰雪奇缘面具披风 米奇米妮面具斗篷 小马宝莉眼罩披###559655658369-50025860
				//PATH/MENUS(多个用;分隔)/ID/NEW_TITLE/NEWID-CATEID(多个用;分隔)
				thisLine = thisLine.replace("\uFEFF", "");
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}
				
				System.out.println("Progress [ " + (++processCount) + " / " + total + " ][Row: " + seq + "] " + thisLine);

				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				
				if (updatedProducts.contains(itemAry[2])){
					System.out.println("[Duplicate Update][Row：" + seq + "]	" + thisLine);
					dupCount++;
					continue;
				}

				String newTitle = itemAry[3];
				String[] newProIds = itemAry[4].split(";");
				
				boolean isUpdated = false;

				try {
					for (String newProId : newProIds){
						String[] idAndCatId = newProId.split("-");
						String id = idAndCatId[0];
						String cateId = idAndCatId[1];
						if (!this.isRecommended(newTitle, id)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put(Constants.MAPKEY_TITLE, newTitle);
							map.put(Constants.MAPKEY_MENU, itemAry[1]);
							map.put(Constants.MAPKEY_FOLDER, itemAry[0]);
							
							if (this.updateProductInfo(map, id, cateId)){
								isUpdated = true;
							}
						}else{
							isUpdated = false;
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				//已经推荐商品不做修改
				if (isUpdated){
					successCount++;
					System.out.println("[Updated][row：" + seq + "]	" + thisLine);
					Utils.addToLog(updateSuccessFile, thisLine);
				}else{
					errorCount++;
					System.err.println("[Update Failed][row：" + seq + "]	" + thisLine);
					Utils.addToLog(updateErrorFile, thisLine);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("==========================");
		System.out.println("Scope	: " + start + " - " + end);
		System.out.println("Total	: " + (end - start + 1));
		System.out.println("Error	: " + errorCount);
		System.out.println("Dup		: " + dupCount);
		System.out.println("Success	: " + successCount);
		System.out.println("==========================");
	}
	

	public void startDetectUncompleteProduct(int start, int end) throws Exception {
		System.out.println("Start detecting....");
//		this.navigateToBuyerCenter();
		
		String uncompleteFile = Constants.SHARE_FOLDER_PATH + "/uncomplete_products.txt";
		String completeFile = Constants.SHARE_FOLDER_PATH + "/complete_products.txt";
		
		Set<String> analyzedProducts = this.getUpdatedProductsFromLogs(completeFile);
		
		// 读取链接文件，开始下载产品信息
		String thisLine = null;
		BufferedReader br = null;
		int successCount = 0;
		int total = (end - start + 1);
		if (total == 1){
			total = 9999;
		}
		int seq = 0;
		int processCount = 0;
		
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(Constants.SHARE_FOLDER_PATH + "/"
					+ Constants.UPLOAD_SUCCESS_FILENAME));
			while ((thisLine = br.readLine()) != null) {
				//COS服装/儿童款/N0750002###COS服装/儿童款###7073412270###孩派天津 冰雪奇缘面具披风 米奇米妮面具斗篷 小马宝莉眼罩披###559655658369-50025860
				//PATH/MENUS(多个用;分隔)/ID/NEW_TITLE/NEWID-CATEID(多个用;分隔)
				thisLine = thisLine.replace("\uFEFF", "");
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}
				
				System.out.println("Progress [ " + (++processCount) + " / " + total + " ][Row: " + seq + "] " + thisLine);

				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				
				if (analyzedProducts.contains(itemAry[2])){
					System.out.println("[Already Analyzed][Row：" + seq + "]	" + thisLine);
					continue;
				}
				
				String[] newProIds = itemAry[4].split(";");
				
				boolean found = false;
				boolean isAnalyzed = false;
				try {
					for (String newProId : newProIds){
						String[] idAndCatId = newProId.split("-");
						String id = idAndCatId[0];
						String cateId = idAndCatId[1];
						found = this.detectUncompleteProducts(id, cateId);
						isAnalyzed = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				//已经推荐商品不做修改
				if (found){
					successCount++;
					System.out.println("[UNCOMPLETE][row：" + seq + "]	" + thisLine);
					Utils.addToLog(uncompleteFile, thisLine);
				}

				if (isAnalyzed){
					System.out.println("[ANALYZED][row：" + seq + "]	" + thisLine);
					Utils.addToLog(completeFile, thisLine);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("==========================");
		System.out.println("Scope	: " + start + " - " + end);
		System.out.println("Total	: " + (end - start + 1));
		System.out.println("Uncomplete	: " + successCount);
		System.out.println("==========================");
	}

	public void startUploadProduct(int start, int end) throws Exception {
		startUploadProduct(start, end, false);
	}
	
	public void startRemoveDups(int start, int end) throws Exception{
		System.out.println("Removing duplicates...");
		
		driver.get("https://sell.taobao.com/auction/merchandise/auction_list.htm?type=11");
		
		File removeList = new File("D:\\Taobao\\tools\\upload_success_all_sorted.txt");
		String lostProductsFilePath = Constants.SHARE_FOLDER_PATH +"/upload_success_lost.txt";
		String validProductsFilePath = Constants.SHARE_FOLDER_PATH +"/upload_success_valid.txt";
		String unIdentifiedProductsFilePath = Constants.SHARE_FOLDER_PATH +"/upload_success_unidentified.txt";
		BufferedReader br = null;
		
		//获取所有已经上传的新产品ID
		Set<String> allUploadNewProIdSet = new HashSet<String>();
		try {
			br = new BufferedReader(new FileReader(removeList));
			String line;
			while((line = br.readLine()) != null){
				String[] dupProductIdAry = line.split(Constants.TM_SEPERATOR)[2].split(";");
				allUploadNewProIdSet.addAll(Arrays.asList(dupProductIdAry));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if (br != null){
					br.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int total = (end - start + 1);
		if (total == 1){
			total = 9999;
		}
		int seq = 0;
		int processCount = 0;
		try {
			br = new BufferedReader(new FileReader(removeList));
			String line;
			while((line = br.readLine()) != null){
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}
				
				//Old product id###Search Title###new product id(|)
				String[] ary = line.split(Constants.TM_SEPERATOR);//length:3
				String title = ary[1];
				String[] dupProductIdAry = ary[2].split(";");
				Set<String> scopedIds = new HashSet<String>(Arrays.asList(dupProductIdAry));
								
//				System.out.println("-----------------------------------------");
				System.out.println("Progress [ " + (++processCount) + " / " + total + " ][Row: " + seq + "] " + line);

				List<Object[]> searchResult = this.getNewProductStatus(title);
				
				List<Object[]> keptList = this.getKeptProductInfo(searchResult, scopedIds);
				
				List<Object[]> unIdentifiedList = getUnIdentifiedObjects(searchResult, allUploadNewProIdSet);
				
				//保存不可识别记录
				for (Object[] obj : unIdentifiedList){
					//Object[0] = actual title
					//Object[1] = productId
					//Object[2] = categoryid
					//Object[3] = 0>Normal; 1>SELL OUT; 2>RECOMMENDED;
					//Object[4] = Checkbox WebElement
					String uIdentifiedLine = obj[1] +Constants.TM_SEPERATOR +obj[2] +Constants.TM_SEPERATOR +obj[3] +Constants.TM_SEPERATOR + obj[0];
					System.out.println("UNKNOWN: " + uIdentifiedLine);
					Utils.addToLog(unIdentifiedProductsFilePath, uIdentifiedLine);
				}
				
				boolean removeFailed = false;
				if (keptList.isEmpty()){
					//已上传产品未找到，需要重新上传
					System.out.println("LOST: " +  line);
					Utils.addToLog(lostProductsFilePath, line);
				}else{
					//删除未销售或未被推荐的重复产品
					List<Object[]> removedList = this.getElementsObjToDelete(searchResult, scopedIds, this.getKeptIds(keptList));
					
					if (!removedList.isEmpty()){
						try{
							//选择需要删除的行
							for (Object[] obj : removedList){
								WebElement ckx = (WebElement) obj[4];
								if (!Boolean.valueOf(ckx.getAttribute("checked"))) {
									System.out.println("REMOVED: id[" + obj[1] + "] cateId[" + obj[2] + "] title[" + obj[0] + "]");
									driverUtils.clickOnElement(ckx);
								}
							}
							
							//删除
							driverUtils.clickOnElement(By.xpath("//*[@id=\"J_DataTable\"]/div[2]/table/thead/tr[2]/td/div/button[1]"));
							driverUtils.acceptAlertMsg();
							
						}catch(Exception ex){
							removeFailed = true;
						}
					}
										
					//删除后，保存有效记录
					if (removeFailed){
						System.err.println("REMOVE FAILED: " + line);
						//删除异常，则保存原始记录，以备再次删除 
						Utils.addToLog(validProductsFilePath, line);	
					}else{
						//保留产品并记录实际title和category id
						StringBuffer proInfobuf = new StringBuffer();
						int bufIndex = 0;
						for (Object[] keptObj : keptList){
							//Object[0] = actual title
							//Object[1] = productId
							//Object[2] = categoryid
							//Object[3] = 0>Normal; 1>SELL OUT; 2>RECOMMENDED;
							//Object[4] = Checkbox WebElement
							if (bufIndex++ != 0){
								proInfobuf.append(";");
							}
							proInfobuf.append(keptObj[1] + "-" + keptObj[2]);
						}
						
						String keptLine = ary[0] +Constants.TM_SEPERATOR + ary[1] +Constants.TM_SEPERATOR + proInfobuf.toString();
						System.out.println("KEPT: " + keptLine);
						Utils.addToLog(validProductsFilePath, keptLine);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if (br != null){
					br.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 搜索结果是否包含目标新产品ID
	 * @param searchResult
	 * @param targetId
	 * @return
	 */
	private boolean hasNewProductId(List<Object[]> searchResult, String targetId){
		for (Object[] result : searchResult){
			if (targetId.equals(result[1])){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 获取不可识别记录
	 * @param searchResult
	 * @param scopedIds
	 * @param keptIds
	 * @return
	 */
	private List<Object[]> getUnIdentifiedObjects(List<Object[]> searchResult, Set<String> uploadedProIds){
		List<Object[]> elements = new ArrayList<Object[]>();
		for (Object[] result : searchResult){
			String id = (String) result[1];
			if (!uploadedProIds.contains(id)){
				elements.add(result);
			}
		}			
		
		return elements;
	}
	
	/**
	 * 获取将要删除的元素
	 * @param searchResult
	 * @param scopedIds
	 * @param keptIds
	 * @return
	 */
	private List<Object[]> getElementsObjToDelete(List<Object[]> searchResult, Set<String> scopedIds, Set<String> keptIds){
		List<Object[]> elements = new ArrayList<Object[]>();
		if (!keptIds.isEmpty()){
			for (Object[] result : searchResult){
				String id = (String) result[1];
				if (scopedIds.contains(id) && !keptIds.contains(id)){
					elements.add(result);
				}
			}			
		}
		
		return elements;
	}

	/**
	 * 获取保留对象列表
	 * @param searchResult
	 * @param scopedIds
	 *  @return List<Object[]>
	 * 			Object[0] = actual title
	 * 			Object[1] = productId
	 *  		Object[2] = categoryid
	 *  		Object[3] = 0>Normal; 1>SELL OUT; 2>RECOMMENDED;
	 *  		Object[4] = Checkbox WebElement
	 */
	private List<Object[]> getKeptProductInfo(List<Object[]> searchResult, Set<String> scopedIds){
		List<Object[]> list = new ArrayList<Object[]>();

		for (Object[] result : searchResult){
			String id = (String) result[1];
			int status = Integer.valueOf((String) result[3]);
			if (scopedIds.contains(id) && status != 0){
				//已销售或已经推荐的，则保留 
				list.add(result);
			}
		}
		
		//若产品都未销售且都未被推荐，则选择第一个保留 
		if (list.isEmpty()){
			for (Object[] result : searchResult){
				if (scopedIds.contains(result[1])){
					//已销售或已经推荐的，则保留 
					list.add(result);
					break;
				}
			}	
		}
		
		return list;
	}
	
	/**
	 * 获取保存对象的所有ID
	 * @param keptObjs
	 * @return
	 */
	private Set<String> getKeptIds(List<Object[]> keptList){
		Set<String> set = new HashSet<String>();
		for (Object[] result : keptList){
			set.add((String) result[1]);
		}
		
		return set;
	}
	
	
	/**
	 * 
	 * @param title
	 * @return List<Object[]>
	 * 			Object[0] = actual title
	 * 			Object[1] = productId
	 *  		Object[2] = categoryid
	 *  		Object[3] = 0>Normal; 1>SELL OUT; 2>RECOMMENDED;
	 *  		Object[4] = Checkbox WebElement
	 * @throws Exception 
	 */
	private List<Object[]> getNewProductStatus(String title) throws Exception{
		driverUtils.clickOnElement(By.className("aslink"));
		driverUtils.sendKeys(By.id("search-keyword"), title);
		driverUtils.clickOnElement(By.className("search-btn"));
		
		List<Object[]> list = new ArrayList<Object[]>();
		if (driverUtils.isElementExisted(By.id("J_DataTable"))){
			WebElement dataSet = driver.findElement(By.id("J_DataTable"));
			WebElement tbody = dataSet.findElements(By.tagName("tbody")).get(0);
			List<WebElement> rows = driverUtils.getChildren(tbody);
			
			int rowIdx = 0;
			for (int i = 0; i < rows.size(); i++){
				WebElement tr = rows.get(i);
				if (tr.getAttribute("class").contains("with-sid")){
					Object[] curProAry = new Object[5];
					
					rowIdx++;
					//检查是否为目标ID,若不是，则忽略
					WebElement link = tr.findElement(By.className("J_QRCode"));
					String outerHTML = link.getAttribute("outerHTML");
					String dataParam = outerHTML.substring(outerHTML.indexOf("data-param=") + 12, outerHTML.indexOf("src=")-2);
					String prodId = dataParam.substring(dataParam.indexOf("itemId=") + 7, dataParam.indexOf("&")); //product id
					String cateId = dataParam.substring(dataParam.indexOf("cid=") + 4, dataParam.indexOf("title=") - 5); //category id;
					String actTitle = dataParam.substring(dataParam.indexOf("title=") + 6); //actual title
					
					curProAry[0] = actTitle;
					curProAry[1] = prodId;
					curProAry[2] = cateId;
					curProAry[3] = "0";// Status
					
					
					//若销量 > 0，则不能删除
					//*[@id="J_DataTable"]/table/tbody[1]/tr[4]/td[6]
					WebElement sellCount = tr.findElement(By.xpath("//*[@id=\"J_DataTable\"]/table/tbody[1]/tr[" + 2*rowIdx + "]/td[6]"));
//					System.out.println("Sell count:" + sellCount.getAttribute("innerText"));
					if (!sellCount.getAttribute("innerText").equals("0")){
//						System.out.println("Sell out!");
						curProAry[3] = "1";
					}
					
					//获取复选框
					WebElement preTr = rows.get(i - 1);
					WebElement ckx = preTr.findElement(By.cssSelector("input[type=checkbox]"));
					
					//检查是否已推荐, 若已经推荐，则不能删除
					WebElement parent = driverUtils.getParent(ckx);
					if (parent.getAttribute("class").contains("recommend-td")){
//						System.out.println("Recommend!");
						curProAry[3] = "2";
					}
					
					//Checkbox WebElement
					curProAry[4] = ckx;
//					System.out.println(outerHTML);
//					System.out.println("title:	" + curProAry[0]);
//					System.out.println("id:	" + curProAry[1]);
//					System.out.println("cateId:	" + curProAry[2]);
//					System.out.println("status:	" + curProAry[3]);
//					System.out.println();
					
					list.add(curProAry);
					
				}
			}	
		}
		
		
		return list;
	}
	
	/**
	 * 判断是否已经被推荐
	 * @param title
	 * @param newProductId
	 * @return
	 * @throws Exception
	 */
	private boolean isRecommended(String title, String newProductId) throws Exception{
		driver.get("https://sell.taobao.com/auction/merchandise/auction_list.htm?type=11");
		//若上一次结果未成功保存，则弹出您还有未保存的数据，直接忽略
		driverUtils.acceptAlertMsg();
		
		driverUtils.clickOnElement(By.className("aslink"));
		driverUtils.sendKeys(By.id("search-keyword"), title);
		driverUtils.clickOnElement(By.className("search-btn"));
		
		if (driverUtils.isElementExisted(By.id("J_DataTable"))){
			WebElement dataSet = driver.findElement(By.id("J_DataTable"));
			WebElement tbody = dataSet.findElements(By.tagName("tbody")).get(0);
			List<WebElement> rows = driverUtils.getChildren(tbody);
			
			for (int i = 0; i < rows.size(); i++){
				WebElement tr = rows.get(i);
				if (tr.getAttribute("class").contains("with-sid")){
					
					//检查是否为目标ID,若不是，则忽略
					WebElement link = tr.findElement(By.className("J_QRCode"));
					String outerHTML = link.getAttribute("outerHTML");
					String dataParam = outerHTML.substring(outerHTML.indexOf("data-param=") + 12, outerHTML.indexOf("src=")-2);
					String prodId = dataParam.substring(dataParam.indexOf("itemId=") + 7, dataParam.indexOf("&")); //product id
					String cateId = dataParam.substring(dataParam.indexOf("cid=") + 4, dataParam.indexOf("title=") - 5); //category id;
					String actTitle = dataParam.substring(dataParam.indexOf("title=") + 6); //actual title
					
					if ( prodId.equals(newProductId) && tr.getAttribute("class").contains("recommend")){
						return true;
					}
				}
			}	
		}
		
		
		return false;
	}
	
	public void startUploadProduct(int start, int end, boolean downloadAgain) throws Exception {
		System.out.println("Start uploading....");
		this.navigateToBuyerCenter();
		
		Set<String> uploadedProducts = this.getUploadedProductsFromLogs(Constants.SHARE_FOLDER_PATH + "/" + Constants.UPLOAD_SUCCESS_FILENAME);

		// 读取链接文件，开始下载产品信息
		String thisLine = null;
		BufferedReader br = null;
		int dupCount = 0;
		int errorCount = 0;
		int successCount = 0;
		int total = (end - start + 1);
		if (total == 1){
			total = 9999;
		}
		int seq = 0;
		int processCount = 0;
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(Constants.SHARE_FOLDER_PATH + "/"
					+ Constants.LINKS_FILENAME));
			while ((thisLine = br.readLine()) != null) {
				//万圣节专区/J0700001###万圣节专区|派对元素/乳胶气球###7627811288
				//PATH/MENUS/ID
				thisLine = thisLine.replace("\uFEFF", ""); //ZERO WIDTH NO-BREAK SPACE
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}
				
				System.out.println("Progress [ " + (++processCount) + " / " + total + " ][Row: " + seq + "] " + thisLine);
								
				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				
				if (uploadedProducts.contains(itemAry[2])){
					System.out.println("[Duplicate Upload][Row：" + seq + "]	" + thisLine);
					dupCount++;
					continue;
				}

				boolean isUpload = false;
				String newProductTitleAndID = null;
				try {
					String p = Constants.SHARE_FOLDER_PATH + "/" + itemAry[0] + "/"
							+ Constants.EACH_PRODUCT_NOTE_FILENAME;
					
					//若需要下载，则重新下载
					if (downloadAgain) {
						System.out.println("Download again : " + Constants.SHARE_FOLDER_PATH + "/"
								+ itemAry[0]);
						download.downloadProductDetailsWithURL(Constants.SHARE_FOLDER_PATH + "/"
								+ itemAry[0],itemAry[1], Constants.SOURCE_DETAIL_VIEW_PAGE + itemAry[2], Constants.DOWNLOAD_TRY_TIMES);
						System.out.println("Download complete: " + Constants.SHARE_FOLDER_PATH + "/"
								+ itemAry[0]);
						
						//Go back to seller center again
						this.navigateToBuyerCenter();
					}
					
					Map<String, Object> map = this
							.generateProInfoMapFromFile(p);
					newProductTitleAndID = this.publishToWebPage(map, Constants.PUBLISH_TRY_TIMES);

//					String logFileName = Constants.UPLOAD_SUCCESS_FILENAME;
//					logFileName = logFileName.substring(0,
//							logFileName.lastIndexOf("."))
//							+ "_row"
//							+ start
//							+ "-"
//							+ end
//							+ logFileName.substring(logFileName
//									.lastIndexOf("."));
					if (newProductTitleAndID != null){
						isUpload = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
//					String logFileName = Constants.UPLOAD_ERROR_FILENAME;
//					logFileName = logFileName.substring(0,
//							logFileName.lastIndexOf("."))
//							+ "_row"
//							+ start
//							+ "-"
//							+ end
//							+ logFileName.substring(logFileName
//									.lastIndexOf("."));
//					Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + logFileName,
//							thisLine);
				}
				
				if (isUpload){
					successCount++;
					System.out.println("[Uploaded][row：" + seq + "]	" + thisLine);
					Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.UPLOAD_SUCCESS_FILENAME,
							thisLine + Constants.TM_SEPERATOR + newProductTitleAndID);
				}else{
					errorCount++;
					System.err.println("[Upload Failed][row：" + seq + "]	" + thisLine);
					Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.UPLOAD_ERROR_FILENAME,
							thisLine + Constants.TM_SEPERATOR + thisLine);
				}
			}

			// 保存异常记录
			// if (errorList.size() > 0){
			// this.createFile(shareFolder, this.errorLinksFileName, errorList);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("==========================");
		System.out.println("Scope	: " + start + " - " + end);
		System.out.println("Total	: " + (end - start + 1));
		System.out.println("Dup		: " + dupCount);
		System.out.println("Error	: " + errorCount);
		System.out.println("Success	: " + successCount);
		System.out.println("==========================");
	}

	/**
	 * 从上传成功LOG取得到所有已经上传成功的产品，避免重复上传
	 * @param logFile
	 * @return
	 */
	private Set<String> getUploadedProductsFromLogs(String logFile){
		//蜡烛/生日-卡通/K0150031###蜡烛/节日-婚礼|蜡烛/生日-卡通###45529687101###孩派天津纯手工彩绘蜡烛###559324037542
		//PATH/MENUS/ID/NEW_TITLE/NEW_ID
		Set<String> set = new HashSet<String>();
		
		String thisLine = null;
		BufferedReader br = null;
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(logFile));
			while ((thisLine = br.readLine()) != null) {
				thisLine = thisLine.replace("\uFEFF", "");
				
				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				set.add(itemAry[2]);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return set;
	}
	
	/**
	 * 从 更新成功LOG取得到所有已经更新的产品，避免重复更新
	 * @param logFile
	 * @return
	 */
	private Set<String> getUpdatedProductsFromLogs(String logFile){
		//蜡烛/生日-卡通/K0150031###蜡烛/节日-婚礼|蜡烛/生日-卡通###45529687101###孩派天津纯手工彩绘蜡烛###559324037542
		//PATH/MENUS/ID/NEW_TITLE/NEW_ID
		Set<String> set = new HashSet<String>();
		
		String thisLine = null;
		BufferedReader br = null;
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(logFile));
			while ((thisLine = br.readLine()) != null) {
				thisLine = thisLine.replace("\uFEFF", "");
				
				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				set.add(itemAry[2]);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return set;
	}
	
	private void tryToCloseCurrentPage() {
		driverUtils.acceptAlertMsg();

		if (!driver.getCurrentUrl().contains("auction_list.htm")){
			// System.out.println(driver.getWindowHandles().size());
			driver.close();
			// try {
			// Alert alert = driver.switchTo().alert();
			// if (alert != null) {
			// alert.dismiss();
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
	
			if (driver != null) {
				// System.out.println(driver.getWindowHandles().size());
				Set<String> wins = driver.getWindowHandles();
				driver.switchTo().window((String) wins.toArray()[wins.size() - 1]);
			}
		}
	}
	
	private void triggerPicSpace(WebElement elem){
		driverUtils.clickOnElement(elem);
		
		try{
			//验证是图片空间是否已经弹出, 若未弹出，重新点击
			WebElement picSpace = driver.findElement(By.linkText("图片空间"));
			while(picSpace == null || !picSpace.isDisplayed()){
				System.out.println("Waiting for picture presence...");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				triggerPicSpace(elem);
			}	
		}catch (Exception e){
//			e.printStackTrace();
			triggerPicSpace(elem);
		}
	}
	
	
	private void populateTitle(Map<String, Object> map) throws Exception{
		String newTitle = Utils.formatTitleDesc(map.get(Constants.MAPKEY_TITLE)
				.toString());
		driverUtils.sendKeys(By.id("title"), newTitle);
	}
	
	private void populateMainPhoto(Map<String, Object> map) throws Exception{
//		driverUtils.scrollDown();
		System.out.println("Upload thumbnail...");
		List<String> mainPhotos = (List<String>) map.get(Constants.MAPKEY_MAIN);
		for (int i = 1; i <= mainPhotos.size(); i++) {
			String xpath = "//*[@id=\"multiMedia\"]/div[1]/ul/li[" + i
					+ "]/div[2]/a";
			// 上传图标按扭
			triggerPicSpace(driver.findElement(By.xpath(xpath)));

			System.out.println("Current Upload..." + mainPhotos.get(i - 1));
			// 开始上传
			switchAndStartFileUpload(Utils
					.formatFilePath(mainPhotos.get(i - 1)), false);
		}
		//若主图数量不足5个，需要手动关闭图片空间窗口
		if (mainPhotos.size() < 5){
			driverUtils.clickOnElement(By.cssSelector("body > div.hint-tip.hint-white.over-upload > a"));	
		}
	}
	
	private void populatePriceInfo(Map<String, Object> map) throws Exception{
		// 宝贝价格
		System.out.println("\n Price Category...");
		List<String> priceList = (List<String>) map.get(Constants.MAPKEY_PRICE);
		//一品价
		if (priceList.size() == 1 && "一口价".equals(priceList.get(0).split(Constants.TM_SEPERATOR)[1])){
			String[] priceAry = priceList.get(0).split(Constants.TM_SEPERATOR);
			driverUtils.sendKeys(driver.findElement(By.id("price")), priceAry[2]);
			driverUtils.sendKeys(driver.findElement(By.id("quantity")), priceAry[3].replace("库存", "").replace("件", ""));
		}else{
			//多种价格分类
			List<WebElement> colorTexts = driver.findElements(By.name("text[]"));
			
			for (int i = 0; i < priceList.size(); i++) {
				// 1. 6人套餐 71.99 库存966件 D:/products/生日派对/男孩套餐/2/category/1.jpg
				System.out.println("Current Upload..." + priceList.get(i));
				String[] priceAry = priceList.get(i).split(Constants.TM_SEPERATOR);

				// 输入描述
				WebElement inputTmp = colorTexts.get(i);
				driverUtils.sendKeys(inputTmp, priceAry[1]);
//				inputTmp.sendKeys(priceAry[1]);
				Thread.sleep(1000);
				// 上传图标按扭
//						driverUtils.clickOnElement(driverUtils.getParent(inputTmp).findElement(
//								By.tagName("A")));

				// 开始上传, 若为无效图片，视为无对应图片，忽略上传价格图片
				if (new File(priceAry[4]).exists()){
					triggerPicSpace(driverUtils.getParent(inputTmp).findElement(
						By.tagName("A")));
					switchAndStartFileUpload(Utils.formatFilePath(priceAry[4]), false);	
				}
				
				// 填充价格
				String priceXpath = "//*[@id=\"sku\"]/div[2]/table/tbody/tr["
						+ (2 + i) + "]/td[2]/input";
				String storeXpath = "//*[@id=\"sku\"]/div[2]/table/tbody/tr["
						+ (2 + i) + "]/td[3]/input";
				driver.findElement(By.xpath(priceXpath)).sendKeys(priceAry[2]);
				driver.findElement(By.xpath(storeXpath)).sendKeys(
						priceAry[3].replace("库存", "").replace("件", ""));
				// 重新获取
				colorTexts = driver.findElements(By.name("text[]"));
			}			
		}
	}
	
	private boolean populatePCDescriptionForUpdate(Map<String, Object> map) throws Exception{
		boolean isUpdated = false;
		StringBuffer buf = new StringBuffer();
		//切换至富文本框，更新数据
		List<WebElement> frames = driver.findElements(By.tagName("IFRAME"));
		for (WebElement tmp : frames) {
			if (tmp.getAttribute("title").equalsIgnoreCase("kissy-editor")) {
				driver.switchTo().frame(tmp);

				WebElement editor = driver.findElement(By.tagName("body"));
				//若没有使用模板，则添加模板，并添加商品目录属性
				if (!editor.getAttribute("innerHTML").contains("详情页模板一")){
					String oldInnerHTML = editor.getAttribute("innerHTML"); 
					oldInnerHTML = oldInnerHTML.replace("我们的图片均为实物拍摄，所见即为所得", "");
					//插入模板
					buf.append("<div class=\"dm_module\" data-id=\"9487228\" data-title=\"详情页模板一\" id=\"ids-module-9487228\">");
					buf.append("	<div>");
					buf.append("		<p style=\"text-align:center;\"><a href=\"https://item.taobao.com/item.htm?spm=686.1000925.0.0.71a97caexCjfac&amp;id=558685030852\" target=\"_blank\"><img src=\"https://img.alicdn.com/imgextra/i3/2304330775/TB2HP4GdamgSKJjSsplXXaICpXa_!!2304330775.jpg\" alt=\"\" /></a></p>");
					buf.append("	</div>");
					buf.append("</div>");

					//商品自身描述信息 
					buf.append(this.wrapWords(Constants.DESC_TYPE_TOP_NOTES, "我们的图片均为实物拍摄，所见即为所得"));

					StringBuffer bottomBuf = new StringBuffer();
					String bottomNotes = "商品目录：" + map.get(Constants.MAPKEY_FOLDER).toString().replace(Constants.SHARE_FOLDER_PATH + "/", "");
					bottomBuf.append(this.wrapWords(Constants.DESC_TYPE_BOTTOM_NOTES, bottomNotes));
					driverUtils.getJs().executeScript("arguments[0].innerHTML = arguments[1] + arguments[3] + arguments[2]", editor,
							buf.toString(), bottomBuf.toString(), oldInnerHTML);
					isUpdated = true;
				}
				
				driver.switchTo().defaultContent();
				
				break;
			}
		}
		
		return isUpdated;
	}
	
	private void populatePCDescription(Map<String, Object> map) throws Exception{
		StringBuffer buf = new StringBuffer();
		
		List<String> detailList = (List<String>) map.get(Constants.MAPKEY_DETAILS);
		
		if (detailList == null){
			detailList = Collections.emptyList();
		}
		
		//上传图片
		for (String s : detailList) {
			String[] tmp = s.split(Constants.TM_SEPERATOR);
			if (tmp.length == 2 && (new File(tmp[1])).exists()){
				System.out.println("Current Upload..." + Utils
						.formatFilePath(tmp[1]));
				this.triggerPicSpace(driver.findElement(By.id("ks-component69")));
				switchAndStartFileUpload(Utils
						.formatFilePath(tmp[1]), true);
			}
		}
		
		//选择模板
//		driverUtils.clickOnElement(By.id("ks-component73"));
//		driverUtils.clickOnElement(By.className("dm-title"));
		
		//插入模板
		buf.append("<div class=\"dm_module\" data-id=\"9487228\" data-title=\"详情页模板一\" id=\"ids-module-9487228\">");
		buf.append("	<div>");
		buf.append("		<p style=\"text-align:center;\"><a href=\"https://item.taobao.com/item.htm?spm=686.1000925.0.0.71a97caexCjfac&amp;id=558685030852\" target=\"_blank\"><img src=\"https://img.alicdn.com/imgextra/i3/2304330775/TB2HP4GdamgSKJjSsplXXaICpXa_!!2304330775.jpg\" alt=\"\" /></a></p>");
		buf.append("	</div>");
		buf.append("</div>");

		//商品自身描述信息 
		buf.append(this.wrapWords(Constants.DESC_TYPE_TOP_NOTES, "我们的图片均为实物拍摄，所见即为所得"));
		
		String descType = Constants.DESC_TYPE_NORMAL;
		for (String s : detailList) {
			if (s.contains("实物拍摄")){
				continue;
			}
			
			String[] tmp = s.split(Constants.TM_SEPERATOR);
			if (tmp.length != 2 || tmp.length == 2 && !new File(tmp[1]).exists()){
				String trimLine = s.trim().replace(Constants.TM_SEPERATOR, "");
				if (trimLine.contains("PS:")){
					descType = Constants.DESC_TYPE_PS;
				}else if (trimLine.contains("注意事项")){
					descType = Constants.DESC_TYPE_ATTENTION;
				}else{
					buf.append(this.wrapWords(descType, s));
				}
			}
		}
		
		
		StringBuffer bottomBuf = new StringBuffer();
		
		String bottomNotes = "商品目录：" + map.get(Constants.MAPKEY_FOLDER).toString().replace(Constants.SHARE_FOLDER_PATH + "/", "");
		bottomBuf.append(this.wrapWords(Constants.DESC_TYPE_BOTTOM_NOTES, bottomNotes));
		
//		buf.append("<p style=\"text-align: center;\">");
//		buf.append("<strong>" + map.get(Constants.MAPKEY_TITLE).toString() + "</strong></p>");		
//		buf.append("<br>");
//		buf.append("<p style=\"text-align: center;\">！！！其他信息！！！</p>");
//		List<String> propList = (List<String>) map.get(Constants.MAPKEY_PROPERTIES);
//		for (String s : propList) {
//			buf.append("<p style=\"text-align: center;\">" + s + "</p>");
//		}
		
		//切换至富文本框，更新数据
		List<WebElement> frames = driver.findElements(By.tagName("IFRAME"));
		for (WebElement tmp : frames) {
			if (tmp.getAttribute("title").equalsIgnoreCase("kissy-editor")) {
				driver.switchTo().frame(tmp);

				WebElement editor = driver.findElement(By.tagName("body"));
				driverUtils.getJs().executeScript("arguments[0].innerHTML = arguments[1] + arguments[0].innerHTML + arguments[2]", editor,
						buf.toString(), bottomBuf.toString());
				driver.switchTo().defaultContent();
				
				break;
			}
		}
	}
	
	private String wrapWords(String type, String value){
		StringBuffer buf = new StringBuffer();
		if (Constants.DESC_TYPE_PS.equals(type)){
			buf.append("<p style=\"text-align: center;\"><strong style=\"color: #ff0000;font-size: 18.0px;line-height: 22.9091px;text-align: center;\">");
			buf.append(value);
			buf.append("</strong></p>");
		}else if (Constants.DESC_TYPE_ATTENTION.equals(type)){
			buf.append("<p style=\"margin-top: 1.12em;margin-bottom: 1.12em;line-height: 1.4;color: #404040;text-align: center;\"><span style=\"margin: 0.0px;padding: 0.0px;font-size: 18.0px;\">");
			buf.append(value);
			buf.append("</span></p>");
		}else if (Constants.DESC_TYPE_BOTTOM_NOTES.equals(type)){
			buf.append("<p style=\"text-align: center;\"><span style=\"color: rgb(153, 153, 153);\"><span style=\"background-color: rgb(217, 234, 211);\">");
			buf.append(value);
			buf.append("</span></span></p>");
		}else if (Constants.DESC_TYPE_TOP_NOTES.equals(type)){
			buf.append("<p align=\"center\" style=\"margin-top:1.12em;margin-bottom:1.12em;color:#000000;\"><font color=\"#ffff00\" size=\"6\" style=\"background-color:#ff0000;\"><strong>");
			buf.append(value);
			buf.append("</strong></font></p>");
		}else{
			buf.append("<p style=\"margin-top: 1.12em;margin-bottom: 1.12em;line-height: 1.4;color: #404040;text-align: center;\"><strong><span style=\"margin: 0.0px;padding: 0.0px;color: #e06666;\">");
			buf.append(value);
			buf.append("</span></strong></p>");
		}
		
		return buf.toString();
	}
	
	public void publishToMobileForUpdate(String productId, String categoryId) throws Exception{
		driver.get("https://upload.taobao.com/auction/container/publish.htm?catId=" + categoryId + "&itemId=" + productId);
	
		this.generateDescAndSaveForMobile();
		
		this.waitPageSavingForUpdate(productId);
	}
	

	public String publishToMobile() throws Exception{
		System.out.println("Publish to mobile...");
		
		driverUtils.clickOnElement(By.linkText("立即编辑»"));
		
		// 关闭验证窗口
		this.tryToCloseConfirmationPopup();

		//从URL中获取CATEGORY ID
		String url = driver.getCurrentUrl();
		String catId = url.substring(url.indexOf("?catId=") + 7, url.indexOf("&itemId="));
		
		try{
			this.generateDescAndSaveForMobile();
		}catch(Exception e){
			//发布到移动端失败，可忽略
			System.out.println("Publish to mobile failed!");
		}
		
	
		return catId;
	}
	
	private void generateDescAndSaveForMobile()  throws Exception{
//		driverUtils.scrollDown();
		List<WebElement> objList = driver.findElements(By.xpath("//label[contains(text(),'使用神笔模板编辑')]"));
		if (objList.size() == 2){
			driverUtils.clickOnElement(objList.get(1));
		}
//		WebElement mobileObj = driver.findElement(By.xpath("//label[contains(text(),'手机端描述')]"));
//		WebElement parentObj = driverUtils.getParent(driverUtils.getParent(mobileObj));
//		WebElement targetObj = parentObj.findElement(By.xpath("//label[contains(text(),'使用神笔模板编辑')]"));
//		driverUtils.clickOnElement(targetObj);
		
		//修改按扭
		driverUtils.clickOnElement(By.id("J_shenbiUpdatewl_1"));
		Thread.sleep(3000);
		
		//Switch to Frame
		driver.switchTo().frame("J_wlFrame_pob_1");
		
		//导入详情
		driver.findElement(By.xpath("//span[contains(text(),'导入详情')]")).click();
		
		//电脑端描述
		driverUtils.clickOnElement(By.xpath("//span[contains(text(),'电脑端描述')]"));
		
		//全图生成
		driverUtils.clickOnElement(By.xpath("//span[contains(text(),'全图生成')]"));
		
		//确定
		driverUtils.clickOnElement(By.xpath("/html/body/div[2]/div/div[2]/div[1]/div[3]/button[2]"));
		
		//再确定
		driverUtils.clickOnElement(By.xpath("/html/body/div[3]/div/div[2]/div[1]/div[3]/button[2]"));
		
		//等待完成
		
		int maxWaitTime = Constants.PUBLISH_TO_MOBIEL_MAX_WAIT;
		while(driverUtils.isElementExisted(By.className("importPCDetail__processing"))){
			System.out.println("Importing pictures from Web Page(PC)...");
			Thread.sleep(1000);
			maxWaitTime = maxWaitTime - 1000;
			
			if (maxWaitTime < 0){
				throw new Exception("Importing timeout!");
			}
		}
		System.out.println("Imported！");
		
		//完成编辑
		driverUtils.clickOnElement(By.xpath("//*[@id=\"where-magic-happens\"]/div/div[1]/div[3]"));
		Thread.sleep(3000);
		
		driver.switchTo().defaultContent();
		
		//保存
		System.out.println("Remove recommendation checkbox!");
		WebElement ccTj = driver
				.findElement(By
						.xpath("//*[@id=\"otherInfomation\"]/table/tbody/tr[4]/td[2]/div/span[1]/label/input"));
		if (Boolean.valueOf(ccTj.getAttribute("checked"))) {
			driverUtils.clickOnElement(ccTj);
		}
		
		Thread.sleep(1000);
		System.out.println("Save for Mobile Updates...");
		driverUtils.clickOnElement(By.xpath("//*[@id=\"commit\"]/button"));
	}
	
	private void populateMenuItem(Map<String, Object> map) throws Exception{
//		driverUtils.scrollDown();
		String menuPath = (String) map.get(Constants.MAPKEY_MENU);
		System.out.println("Match Menu Item..." + menuPath);

		// Correction
//		menuPath = menuPath.replaceAll("正品", "").replaceAll("蓝球", "篮球");
		
		String[] multiMenu = menuPath.split(";");
		Set<String> selectedMenuSet = new HashSet<String>();
		
		for (String menuStr : multiMenu){

			String[] menus = menuStr.split("/");

			WebElement menuDiv = driverUtils.getVisibleElement(By
					.xpath("//*[@id=\"shopCats\"]/ul"));
			List<WebElement> menuLevel1 = driverUtils.getChildren(menuDiv);
			if (menus.length == 1) {
				String menu = menus[0].replace("-", "/");
				for (WebElement m : menuLevel1) {
					String lbl1 = driverUtils.getChildren(m).get(0).getText();
					List<WebElement> level2Ins = m
							.findElements(By.tagName("INPUT"));
					for (WebElement tmp : level2Ins) {
						String lbl2 = driverUtils.getParent(tmp).getText();

						// 去除其他选项
						if (lbl1.equals(menu) && lbl2.equals(menu)) {
							if (!Boolean.valueOf(tmp.getAttribute("checked"))) {
								driverUtils.clickOnElement(tmp);
							}
							
							selectedMenuSet.add(lbl1 + "/" + lbl2);
						} else if (Boolean.valueOf(tmp.getAttribute("checked")) && !selectedMenuSet.contains(lbl1 + "/" + lbl2)) {
							driverUtils.clickOnElement(tmp);
						}
					}
				}
			} else if (menus.length == 2) {
				String menu = menus[0].replace("-", "/");
				String menuItem = menus[1].replace("-", "/");

				for (WebElement m : menuLevel1) {
					String lbl1 = driverUtils.getChildren(m).get(0).getText();
					List<WebElement> level2Ins = m
							.findElements(By.tagName("INPUT"));
					for (WebElement tmp : level2Ins) {
						String lbl2 = driverUtils.getParent(tmp).getText();
						if (lbl1.equals(menu) && lbl2.equals(menuItem)) {
							if (!Boolean.valueOf(tmp.getAttribute("checked"))) {
								driverUtils.clickOnElement(tmp);
							}
							
							selectedMenuSet.add(lbl1 + "/" + lbl2);
						} else {
							// 去除其他选项
							if (Boolean.valueOf(tmp.getAttribute("checked")) && !selectedMenuSet.contains(lbl1 + "/" + lbl2)) {
								driverUtils.clickOnElement(tmp);
							}
						}
					}
				}
			}	
		}
	}
	

	private boolean saveAndVerifyForUpdate(String productId) throws Exception{
		System.out.println("Remove recommendation checkbox...");
		WebElement ccTj = driver
				.findElement(By
						.xpath("//*[@id=\"otherInfomation\"]/table/tbody/tr[4]/td[2]/div/span[1]/label/input"));
		if (Boolean.valueOf(ccTj.getAttribute("checked"))) {
			driverUtils.clickOnElement(ccTj);
		}
		
		System.out.println("Saving...");
		driverUtils.clickOnElement(By.xpath("//*[@id=\"commit\"]/button"));
		
		Thread.sleep(3000);
		if (driverUtils.isElementExisted(By.linkText("一旦清零，无法恢复，查看规则"))){
			System.out.println("一旦清零，无法恢复，查看规则");
			//取消，并跳转
			driverUtils.clickOnElement(By.className("J_cancle"));
			driver.get("https://sell.taobao.com/auction/merchandise/auction_list.htm?type=11");
			driverUtils.acceptAlertMsg();
			throw new Exception("一旦清零，无法恢复，查看规则");
		}

		this.waitPageSavingForUpdate(productId);	
		

		return true;
	}
	
	private String saveAndVerify(Map<String, Object> map) throws Exception{
		System.out.println("Remove recommendation checkbox...");
		WebElement ccTj = driver
				.findElement(By
						.xpath("//*[@id=\"otherInfomation\"]/table/tbody/tr[4]/td[2]/div/span[1]/label/input"));
		if (Boolean.valueOf(ccTj.getAttribute("checked"))) {
			driverUtils.clickOnElement(ccTj);
		}
		
		System.out.println("Saving...");
		driverUtils.clickOnElement(By.xpath("//*[@id=\"commit\"]/button"));

		// System.out.println("保存为草稿...");
		// clickOnElement(By.xpath("//*[@id=\"commit\"]/div/button[1]"));
		// clickOnElement(By.xpath("//*[@id=\"commit\"]/div/div[1]/div/div[1]/button"));

		System.out.println("Verify publish status...");
		if (driverUtils.isElementExisted(By.xpath("//*[@id=\"Content\"]/div/h2[1]"))) {
			System.out.println(Constants.TM_SEPERATOR
					+ driver.findElement(
							By.xpath("//*[@id=\"Content\"]/div/h2[1]"))
							.getText());

			// 记录新宝贝地址
			if (driverUtils.isElementExisted(By.linkText("查看该宝贝"))) {
				String newUrl = driver.findElement(By.linkText("查看该宝贝"))
						.getAttribute("href");
				String prodId = newUrl.substring(newUrl.indexOf("?id=") + 4);
				System.out.println("New Add：" + newUrl);

				return Utils.formatTitleDesc(map.get(Constants.MAPKEY_TITLE)
						.toString()) + Constants.TM_SEPERATOR + prodId;
			}
		} else {
			throw new Exception("发布失败");
		}
		
		return null;
	}

	private String enterProductInfo(Map<String, Object> map) throws Exception {
		// *********************选择分类************************//
		this.chooseCategory(map);
		
		// *********************标题************************//
		this.populateTitle(map);
		
		// *********************填充属性信息************************//
		this.populateProperties(map, 3);
		
		// *********************上传主图************************//
		this.populateMainPhoto(map);

		// *********************宝贝价格************************//
		this.populatePriceInfo(map);

		// *********************电脑端描述************************//
		this.populatePCDescription(map);
		
		// *********************店铺中分类************************//
		this.populateMenuItem(map);

		// *********************保存************************//
		String result = this.saveAndVerify(map);

		// *********************手机端描述************************//
		String catId = this.publishToMobile();
		
		return result + "-" + catId;
	}


	public void switchAndStartFileUpload(String filePath, boolean hasInsertBtn) throws Exception{
		boolean error = false;
		try{
			// 上传图片
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			List<WebElement> frmList = driver.findElements(By.cssSelector("div[id^='J_imagespace']"));
			WebElement frame = null;
			for (WebElement frm : frmList){
				if (frm.isDisplayed()){
					frame = driverUtils.getChildren(frm).get(0);
					break;
				}
			}
			if (frame != null){
				driver.switchTo().frame(frame);
				
				uploadNewFile(filePath);
				
				if (hasInsertBtn){
					//若存在插入按扭，则点击
					WebElement inserBtn = driver.findElement(By.linkText("插入")); 
					while (inserBtn.getAttribute("class").contains("disabled")){
						System.out.println("Inserting...");
						Thread.sleep(1000);
						inserBtn = driver.findElement(By.linkText("插入"));
					}
					inserBtn.click();
				}
				
				driver.switchTo().defaultContent();	
			}
		}catch (Exception e){
			System.err.println("Upload failed!" + e.getMessage());
			throw e;
		}
		
//		if (error){
//			System.err.println("上传出错，重试。。。");
//			switchAndStartFileUpload(filePath, hasInsertBtn);
//		}
	}
	
	
	/**
	 * 负责直接上传新文件
	 * D:\Taobao\迪士尼\迪士尼公主\W0900002\main\W0900002_M_5.jpg
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void uploadNewFile(String filePath)  throws Exception{
		try{
			WebElement uploadNewBtn = driver.findElement(By.linkText("上传新图片"));
			driverUtils.clickOnElement(uploadNewBtn);
//			uploadNewBtn.click();
			//检查是否点击成功
//			WebElement uploadBtnParent = driverUtils.getParent(uploadNewBtn);
			while (!driverUtils.getParent(uploadNewBtn).getAttribute("class").contains("active")){
//				uploadNewBtn.click();
				driverUtils.clickOnElement(uploadNewBtn);
			}
			
//			driver.findElement(By.linkText("上传新图片")).click();
			
			
			Thread.sleep(500);
			//修改上传位置
			String[] folder = filePath.split("\\\\");
			String targetFolder = folder[folder.length - 3];
			
			//验证修改图片位置显示中
			WebElement floatWin = driver.findElement(By.className("tree-wrap"));
			if (floatWin.getAttribute("class").contains("none")){
//				driverUtils.chooseOkOnAcceptAlert();
				driverUtils.clickOnElement(By.className("mod-modify-folder"));
			}
			//亲，您已经上传5张了！ 
//			driverUtils.acceptAlertMsg();
//			this.tryToCloseConfirmationPopup();
			
			driverUtils.sendKeys(By.className("search"), targetFolder);
			//自动查询的结果, 若无结果，则无此目录
			WebElement li = driver.findElement(By.cssSelector("div.auto-complete ul li"));
			while (!li.isDisplayed()){
				Thread.sleep(300);
				driverUtils.sendKeys(By.className("search"), targetFolder);
				li = driver.findElement(By.cssSelector("div.auto-complete ul li"));
			}
			li.click();
			Thread.sleep(300);	
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Picture Upload Failed[" + filePath + "], try again!!");
		
			//若无子目录，则上传至根目录
//			System.out.println("未找到图片位置，切换至图片空间");
//			driver.findElement(By.cssSelector("a[title='图片空间']")).click();
//			System.out.println("修改上传图片位置结束！");
			throw e;
		}
		 

		//设置水印
		WebElement wm = driver.findElement(By.id("watermark"));
		boolean isChecked = Boolean.valueOf(wm.getAttribute("checked"));
		if (Constants.ENABLE_WATERMARK){
			if (!isChecked) {
				driverUtils.clickOnElement(wm);
			}
		}else{
			if (isChecked) {
				driverUtils.clickOnElement(wm);
			}
		}
		
		//选择上传按扭
		driverUtils.clickOnElement(By.id("pickfiles"));

		//上传图片
		Utils.uploadFile(filePath);
		Thread.sleep(1000);
	}
	
	private void chooseCategory(String title) throws Exception{
		System.out.println("Choose Category:" + title);
		propertiesAryList.clear();
//		propertiesAryList.add(new String[]{"","","1"});
		this.selectCategoryProperties(title);
		
		driverUtils.sendKeys(By.id("J_SearchKeyWord"), categoryKey);
		driverUtils.clickOnElement(By.id("J_SearchButton"));
		driverUtils.clickOnElement(By.xpath(selectItemXpath));
		driverUtils.clickOnElement(By.id("J_CatePubBtn"));

		// 关闭验证窗口
		this.tryToCloseConfirmationPopup();
		//若存在试用新版，则切换至新版
		if (driverUtils.isElementExisted(By.linkText("试用新版"))){
			driverUtils.clickOnElement(By.linkText("试用新版"));
		}
		
		System.out.println("Selected Category：" + categoryKey);
	}

	private void chooseCategory(Map<String, Object> map) throws Exception{
		String title = map.get(Constants.MAPKEY_TITLE).toString();
		this.chooseCategory(title);
	}
	
	private void selectCategoryProperties(String title){
		if (titleContains(title, "气球")){
			categoryKey = "气球";
			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
			
			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[1]/td[2]/div/div[1]/div[1]/div/div/div[1]/input","1","1"});
			propertiesAryList.add(new String[]{"型号","#props > table:nth-child(1) > tbody > tr:nth-child(1) > td:nth-child(2) > div > div.clearfix > div.clearfix.fl.child-prop > div.combo-props > div > div.combobox-sel.clearfix > input","1","2"});
			propertiesAryList.add(new String[]{"货号","//*[@id=\"prop_13021751\"]","1","1"});
			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
			propertiesAryList.add(new String[]{"气球及配件类型","//*[@id=\"props\"]/table[1]/tbody/tr[5]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"流行元素","//*[@id=\"props\"]/table[1]/tbody/tr[6]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"适用空间","//*[@id=\"props\"]/table[2]/tbody/tr[1]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"形状","//*[@id=\"props\"]/table[2]/tbody/tr[2]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"具体规格","//*[@id=\"props\"]/table[2]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"图案","//*[@id=\"props\"]/table[2]/tbody/tr[4]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"气球色泽","//*[@id=\"props\"]/table[2]/tbody/tr[5]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"主图来源","//*[@id=\"props\"]/table[2]/tbody/tr[6]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
		}else if (titleContains(title, "蜡烛")){
			categoryKey = "蜡烛";
			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
			
			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[1]/td[2]/div/div[1]/div[1]/div/div/div[1]/input","1","1"});
			propertiesAryList.add(new String[]{"型号","//*[@id=\"subProp_20000_0\"]","1","1"});
			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
			propertiesAryList.add(new String[]{"蜡烛配件类型","//*[@id=\"props\"]/table[1]/tbody/tr[4]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"适用场景","//*[@id=\"props\"]/table[2]/tbody/tr[1]/td[2]/div","3","1"});
			propertiesAryList.add(new String[]{"功能","//*[@id=\"props\"]/table[2]/tbody/tr[2]/td[2]/div","3","1"});
			propertiesAryList.add(new String[]{"主图来源","//*[@id=\"props\"]/table[2]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
		}else if (titleContains(title, "拉花")){
			categoryKey = "拉花";
			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
			
			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[2]/td[2]/div/div[1]/div[1]/div/div/div[1]/input","1","1"});
			propertiesAryList.add(new String[]{"型号","//*[@id=\"subProp_20000_0\"]","1","1"});
			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
			propertiesAryList.add(new String[]{"适用空间","//*[@id=\"props\"]/table[1]/tbody/tr[4]/td[2]/div","3","1"});
			propertiesAryList.add(new String[]{"材质","//*[@id=\"props\"]/table[2]/tbody/tr[1]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"长度","//*[@id=\"props\"]/table[2]/tbody/tr[2]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"主图来源","//*[@id=\"props\"]/table[2]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			
//		}else if (titleContains(title, "墙贴/贴纸/墙纸/贴画")){
//			categoryKey = "喜字/剪纸/贴纸";
//			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
//			
//			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[1]/td[2]/div/div[1]/div/div/div/div[1]/input","1","1"});
//			propertiesAryList.add(new String[]{"货号","//*[@id=\"prop_13021751\"]","1","1"});
//			propertiesAryList.add(new String[]{"窗花剪纸类型","//*[@id=\"props\"]/table[1]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
//			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
//			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
//			propertiesAryList.add(new String[]{"主图来源","//*[@id=\"props\"]/table[2]/tbody/tr[2]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
//			propertiesAryList.add(new String[]{"材质","//*[@id=\"props\"]/table[2]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			
		}else if (titleContains(title, "插牌/蛋糕架/刀叉勺/礼物袋/礼品袋/包装袋/回礼袋/服装/游戏用品/派对用品/聚会用品/墙贴/贴纸/墙纸/贴画")){
			categoryKey = "其他礼品/节庆用品";
			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
			
			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[1]/td[2]/div/div[1]/div/div/div/div[1]/input","1","1"});
			propertiesAryList.add(new String[]{"货号","//*[@id=\"prop_13021751\"]","1","1"});
			propertiesAryList.add(new String[]{"产品类别","//*[@id=\"props\"]/table[1]/tbody/tr[3]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
		}else{
			//面具/装扮用品/布置用品/装扮道具/会场装饰/装饰布置/饰扮布置/人物模型/人偶玩具/
			categoryKey = "装扮用品";
			selectItemXpath = "//*[@id=\"J_SearchResult\"]/div[2]/ol/li[1]";
			
			propertiesAryList.add(new String[]{"品牌","//*[@id=\"props\"]/table[1]/tbody/tr[1]/td[2]/div/div[1]/div[1]/div/div/div[1]/input","1","1"});
			propertiesAryList.add(new String[]{"型号","//*[@id=\"subProp_20000_0\"]","1","1"});
			propertiesAryList.add(new String[]{"包装体积","//*[@id=\"prop_122640606\"]","1","1"});
			propertiesAryList.add(new String[]{"毛重","//*[@id=\"prop_2020542\"]","1","1"});
			propertiesAryList.add(new String[]{"主图来源","//*[@id=\"props\"]/table[2]/tbody/tr[2]/td[2]/div/div[1]/div/div/div/div[1]/input","2","1"});
		}
		
		System.out.println("Selected Category：" + categoryKey);
	}
	
	private void populatePropertiesForUpdateCase(Map<String, Object> map) throws Exception{
		this.selectCategoryProperties(map.get(Constants.MAPKEY_TITLE).toString());
		
		String[] folder = map.get(Constants.MAPKEY_FOLDER).toString().split("/"); 
		String code = folder[folder.length-1];
		
		Map<String, String> propValMap = new HashMap<String, String>();
		propValMap.put("型号", code);
		propValMap.put("货号", code);
		
		//若必选属性没有值，则添加默认值
		if (!propValMap.containsKey("产品类别")){
			propValMap.put("产品类别", "节庆用品");
		}
		
		/**
		 * new String[属性名称，XPATH，元素类型, ByType]
		 * 元素类型：
		 * 1 >　INPUT
		 * 2 > SELECT	
		 * 3 >　CHECKBOX
		 */
		for (String[] ary : propertiesAryList){
			By by = null;
			if ("2".equals(ary[3])){
				by = By.cssSelector(ary[1]);
			}else{
				by = By.xpath(ary[1]);
			}
					
			if (propValMap.containsKey(ary[0])){
				if ("1".equals(ary[2])){
					driverUtils.sendKeys(by, propValMap.get(ary[0]));
					
					if ("品牌".equals(ary[0])){
						System.out.println("Waiting for brand population.");
						Thread.sleep(4000);	
					}
				}else if ("3".equals(ary[2])){
					//Checkbox
					String[] valAry = propValMap.get(ary[0]).split(" ");
					List<String> ckxVals = Arrays.asList(valAry);

					List<WebElement> items = driverUtils.getChildren(driver.findElement(by));
					for (WebElement e : items){
						if (ckxVals.contains(e.getText())){
							driverUtils.clickOnElement(e);
						}
					}
				}else if ("2".equals(ary[2])){
					// combobox/select
					this.setValueForCombobox(by, propValMap.get(ary[0]));
				}
			}
		}	
	}
	
	/**
	 * 判断标题是否包括特定关键字，以进行匹配分类
	 * @param source
	 * @param target
	 * @return
	 */
	private boolean titleContains(String source, String target){
		String[] ary = target.split("/");
		for (String t : ary){
			if (source.contains(t)){
				return true;
			}
		}
		return false;
	}
	
	private void populateProperties(Map<String, Object> map, int tryTimes) throws Exception{
		System.out.println("Populating Properties:" );
		try{
			// 品牌

			/**
			 *  品牌名称：Highparty/孩派
			 *  品牌: Highparty/孩派
			 *  货号: 35cm数字铝箔气球
			 *  尺寸: 35cm数字0 35cm数字1 35cm数字2 35cm数字3 35cm数字4 35cm数字5 35cm数字6 35cm数字7 35cm数字8 35cm数字9
			 *  颜色分类: 蓝色 粉色 金色 银色
			 *  窗花剪纸类型: 剪纸
			 */
			//根据不同种类，填充不同属性
			List<String> propList = (List<String>) map.get(Constants.MAPKEY_PROPERTIES);
			Map<String, String> propValMap = new HashMap<String, String>();
			for (String s : propList) {
				String[] tmp = s.split(":");
				if (tmp.length == 2){
					propValMap.put(tmp[0].trim(), tmp[1].trim());
				}
			}
			
			//若必选属性没有值，则添加默认值
			if (!propValMap.containsKey("产品类别")){
				propValMap.put("产品类别", "节庆用品");
			}
			String[] folder = map.get(Constants.MAPKEY_FOLDER).toString().split("/"); 
			String code = folder[folder.length-1];
			if (!propValMap.containsKey("型号")){
				propValMap.put("型号", code);
			}
			if (!propValMap.containsKey("货号")){
				propValMap.put("货号", code);
			}
			
			/**
			 * new String[属性名称，XPATH，元素类型, ByType]
			 * 元素类型：
			 * 1 >　INPUT
			 * 2 > SELECT	
			 * 3 >　CHECKBOX
			 */
			for (String[] ary : propertiesAryList){
				By by = null;
				if ("2".equals(ary[3])){
					by = By.cssSelector(ary[1]);
				}else{
					by = By.xpath(ary[1]);
				}
						
				if (propValMap.containsKey(ary[0])){
					if ("1".equals(ary[2])){
						driverUtils.sendKeys(by, propValMap.get(ary[0]));
						
						if ("品牌".equals(ary[0])){
							System.out.println("Waiting for brand population.");
							Thread.sleep(4000);	
						}
					}else if ("3".equals(ary[2])){
						//Checkbox
						String[] valAry = propValMap.get(ary[0]).split(" ");
						List<String> ckxVals = Arrays.asList(valAry);

						List<WebElement> items = driverUtils.getChildren(driver.findElement(by));
						for (WebElement e : items){
							if (ckxVals.contains(e.getText())){
								driverUtils.clickOnElement(e);
							}
						}
					}else if ("2".equals(ary[2])){
						// combobox/select
						this.setValueForCombobox(by, propValMap.get(ary[0]));
					}
				}
			}	
		}catch (Exception e){
			e.printStackTrace();
			throw e;
//			System.err.println("属性填充失败，重试");
//			if (tryTimes > 0){
//				populateProperties(map, --tryTimes);
//			}else{
//				throw e;				
//			}
			
		}
	}
	
	private void setValueForCombobox(By by, String value) throws Exception{
		WebElement element = driver.findElement(by);
		driverUtils.clickOnElement(element);
		
		WebElement grandParent = driverUtils.getParent(driverUtils.getParent(element));
		List<WebElement> children = driverUtils.getChildren(grandParent);
		
		// 0 Input
		// 1 Dropdown popup
		if (children.size() >= 2 ){
			WebElement secondElem = children.get(1);
			WebElement inputElem = secondElem.findElement(By.tagName("input"));
//			inputElem.sendKeys(value);
			driverUtils.sendKeys(inputElem, value);
//			Thread.sleep(500);
			
			//从弹出提示中选择第一个
			WebElement ulElem = secondElem.findElement(By.tagName("ul"));
			
			children = driverUtils.getChildren(ulElem);
			if (children.size() >= 1 ){
//				children.get(0).click();
				driverUtils.clickOnElement(children.get(0));
			}			
		}
	}
 
	private void tryToCloseConfirmationPopup() {
		if (driverUtils.isElementExisted(By.className("aq_durex_overlay_x"))) {
			try {
				driverUtils.clickOnElement(By.className("aq_durex_overlay_x"));
				driverUtils.dismissAlertMsg();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public boolean updateProductInfo(Map<String, Object> map, String productId, String categoryId) throws Exception {
		//跳转至编辑页面
		driver.navigate().to("https://upload.taobao.com/auction/container/publish.htm?catId=" + categoryId + "&itemId=" + productId);
		
		boolean isUpdated = this.populatePCDescriptionForUpdate(map);
		this.populateMenuItem(map);

		// *********************保存************************//
		this.saveAndVerifyForUpdate(productId);
		
		// *********************手机端描述************************//
		if (isUpdated){
			//描述 信息若有修改，才修改移动端内容
			try{
				this.publishToMobileForUpdate(productId, categoryId);
			}catch(Exception e){
				System.err.println("Publish to Mobile failed...");
			}	
		}
		
		return true;
	}
	
	public boolean detectUncompleteProducts(String productId, String categoryId) throws Exception {
		//跳转至编辑页面
		driver.get("https://upload.taobao.com/auction/container/publish.htm?catId=" + categoryId + "&itemId=" + productId);
		driverUtils.acceptAlertMsg();
		
		boolean found = false;
		//切换至富文本框，更新数据
		List<WebElement> frames = driver.findElements(By.tagName("IFRAME"));
		for (WebElement tmp : frames) {
			if (tmp.getAttribute("title").equalsIgnoreCase("kissy-editor")) {
				driver.switchTo().frame(tmp);

				WebElement editor = driver.findElement(By.tagName("body"));
				//若没有使用模板，则添加模板，并添加商品目录属性
				if (editor.getAttribute("innerHTML").contains("D:/products/")){
					found = true;
				}
				
				driver.switchTo().defaultContent();
				
				break;
			}
		}
		
		return found;
	}
	
	private void waitPageSavingForUpdate(String productId) throws Exception {
		int maxWaitTime = Constants.PUBLISH_TO_MOBIEL_MAX_WAIT;
		while(!driver.getCurrentUrl().equals("https://item.taobao.com/item.htm?id=" + productId)){
			System.out.println("Importing pictures from Web Page(PC)...");
			Thread.sleep(1000);
			maxWaitTime = maxWaitTime - 1000;
			
			if (maxWaitTime < 0){
				throw new Exception("Importing timeout!");
			}
		}
	}
	
	public String publishToWebPage(Map<String, Object> map, int tryTimes) throws Exception {
		// driver.navigate().to("https://myseller.taobao.com/seller_admin.htm");
		driverUtils.clickOnElement(By.linkText("发布宝贝"));
		Set<String> wins = driver.getWindowHandles();
		driver.switchTo().window((String) wins.toArray()[wins.size() - 1]);

		// 填充产品信息
		String result = null;
		try{
			result = enterProductInfo(map);
		}catch (Exception e){
			throw e;
		}finally{
			// 关闭当前发布 页面，继续上传
			try{
//				this.tryToCloseCurrentPage();	
			}catch (Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		return result;
	}
}
