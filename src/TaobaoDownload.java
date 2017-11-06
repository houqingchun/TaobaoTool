import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class TaobaoDownload {

	WebDriver driver = null;
	WebDriverUtils driverUtils = null;
	
	public TaobaoDownload(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().window().maximize();
		driverUtils = new WebDriverUtils(driver);
	}
	

	public void startDownload(int start, int end) {
		System.out.println("Start downloading....");
		// 生成产品链接
//		generateLinksMap();

		// 错误异常记录保存
		// List<String> errorList = new ArrayList<String>();

		// 读取链接文件，开始下载产品信息
		String thisLine = null;
		BufferedReader br = null;
		int seq = 0;
		try {
			// open input stream test.txt for reading purpose.
			br = new BufferedReader(new FileReader(Constants.SHARE_FOLDER_PATH + "/"
					+ Constants.LINKS_FILENAME));
			while ((thisLine = br.readLine()) != null) {
				//万圣节专区/J0700001###万圣节专区|派对元素/乳胶气球###7627811288
				//PATH/MENUS/ID
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}

				System.out.println(thisLine);
				String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
				// 根据链接，下载各产品详细信息
				try {
					this.downloadProductDetailsWithURL(Constants.SHARE_FOLDER_PATH + "/"
							+ itemAry[0],itemAry[1], Constants.SOURCE_DETAIL_VIEW_PAGE + itemAry[2], Constants.DOWNLOAD_TRY_TIMES);

//					String logFileName = Constants.DOWNLOAD_SUCCESS_FILENAME;
//					logFileName = logFileName.substring(0,
//							logFileName.lastIndexOf("."))
//							+ "_row"
//							+ start
//							+ "-"
//							+ end
//							+ logFileName.substring(logFileName
//									.lastIndexOf("."));

					Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.DOWNLOAD_SUCCESS_FILENAME,
							thisLine);
				} catch (Exception e) {
					e.printStackTrace();
//					String logFileName = Constants.DOWNLOAD_ERROR_FILENAME;
//					logFileName = logFileName.substring(0,
//							logFileName.lastIndexOf("."))
//							+ "_row"
//							+ start
//							+ "-"
//							+ end
//							+ logFileName.substring(logFileName
//									.lastIndexOf("."));

					Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.DOWNLOAD_ERROR_FILENAME,
							thisLine);
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

		System.out.println("Done");
	}

//	public void startDownload(List<String[]> urlList) {
//		System.out.println("Start downloading....");
//		for (int i = 0; i < urlList.size(); i++) {
//			String[] itemAry = urlList.get(i);
//			String thisLine = itemAry[0] + Constants.TM_SEPERATOR + itemAry[1];
//			System.out.println(thisLine);
//
//			// 根据链接，下载各产品详细信息
//			try {
//				this.downloadProductDetailsWithURL(Constants.SHARE_FOLDER_PATH + "/"
//						+ itemAry[0], itemAry[1], 5);
//			} catch (Exception e) {
//				Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.DOWNLOAD_ERROR_FILENAME,
//						thisLine);
//			}
//
//			Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/" + Constants.DOWNLOAD_SUCCESS_FILENAME,
//					thisLine);
//		}
//
//		System.out.println("Done");
//	}
	
	
	private void tryCloseLoginPopup(){
		try{
			WebElement e = null;
			boolean wait = true;
			while (wait){
				e = this.driver.findElement(By.cssSelector("a[id^='ks-overlay-close-ks-component']"));
				System.out.println("Waiting " + 5000);
				if (e != null){
					WebElement p = driverUtils.getParent(e);
					if (p.isDisplayed()){
						Thread.sleep(5000);	
					}else{
						wait = false;
					}
				}else{
					wait = false;
				}
			}
			
//			if (e != null){
//				e.click();
//			}
		}catch (Exception e){
			
		}
	}


	public void downloadProductDetailsWithURL(String folder, String menus, String url, int tryTimes)
			throws Exception {
		try{
			System.out.print("Processing[" + folder + "]....");

			Utils.createFolder(folder);

			driver.navigate().to(url);

			//若有未完成的弹出框，则点击确认忽略
			driverUtils.acceptAlertMsg();

			List<String> notes = new ArrayList<String>();

			// Get title
			notes.add(">>#URL#-----------------------");
			notes.add(url);
			notes.add(">>#FOLDER#-----------------------");
			notes.add(folder);
			notes.add(">>#MENU#-----------------------");
//			String menu = folder.replace(Constants.SHARE_FOLDER_PATH + "/", "");
//			menu = menu.substring(0, menu.lastIndexOf("/"));
			notes.add(menus);
			notes.add(">>#TITLE#-----------------------");
			notes.add(this.driver.findElement(By.className("tb-detail-hd"))
					.getText());
			notes.add(">>#PARAM#-----------------------");
			notes.add(this.driver.findElement(By.id("J_AttrUL")).getText());
			this.downloadForEachProduct(folder, notes);
			// notes.add("----------DETAILS----------");
			// notes.add(this.driver.findElement(By.className("ke-post")).getText());

			Utils.createFile(folder, Constants.EACH_PRODUCT_NOTE_FILENAME, notes);

			System.out.println("	Complete!");
		}catch (Exception e){
			System.err.println(e.getMessage());
			if (tryTimes > 0){
				Thread.sleep(5000);
				downloadProductDetailsWithURL(folder, menus, url, tryTimes-1);	
			}else{
				throw e;
			}
		}
		
	}


	private void downloadForEachProduct(String folder, List<String> notes)
			throws Exception {
		//从文件夹中取出编码，作为保存文件前缀
		String filePrefix = folder.substring(folder.lastIndexOf("/")+1);
		// 主图下载
		notes.add(">>#MAIN#----------");
		WebElement elem = driverUtils.getWait().until(ExpectedConditions.elementToBeClickable(By
				.id("J_UlThumb")));
		List<WebElement> list = elem.findElements(By.tagName("A"));

		for (int i = 0; i < list.size(); i++) {
			WebElement wb = list.get(i);
			if (!wb.isDisplayed()) {
				continue;
			}
			driverUtils.clickOnElement(wb);
			Thread.sleep(1000); //Waiting for 1 seconds for image display
			WebElement imgEle = driver.findElement(By.id("J_ImgBooth"));
			System.out.println("Pictures in Subject: " + (i + 1) + " "
					+ imgEle.getAttribute("src"));
			String filePath = Utils.saveImage(imgEle.getAttribute("src"),
					folder + "/main", filePrefix + "_M_" + (i + 1));
			if (filePath != null) {
				notes.add((i + 1) + Constants.TM_SEPERATOR + filePath);
			}
		}

		// 价格，分类等
		notes.add(">>#PRICE#----------");
		
		if (driverUtils.isElementExisted(By.className("J_TSaleProp"))){
			//有价格分类情况
			elem = driver.findElement(By.className("J_TSaleProp"));
			list = elem.findElements(By.tagName("SPAN"));	
			for (int i = 0; i < list.size(); i++) {
				WebElement wb = list.get(i);
				if (!wb.isDisplayed()) {
					continue;
				}
				// System.out.println(" tag=" + wb.getTagName() + " class=" +
				// wb.getAttribute("class") + " isDisplayed=" + wb.isDisplayed() +
				// " isEnabled=" + wb.isEnabled() + " isSelected=" +
				// wb.isSelected());
				// this.clickOnElement(wb.findElement(By.xpath("..")));
				driverUtils.clickOnElement(wb);
				String price = driver.findElement(By.className("tm-price"))
						.getText();
				
				//检查是否有相应图片，有则保存
				WebElement parentLink = driverUtils.getParent(wb);
				String filePath = "NOTHING";
				if (parentLink.getAttribute("outerHTML").contains("background:url")) {
					Thread.sleep(1000); //Waiting for 1 seconds for image display
					WebElement imgEle = driver.findElement(By.id("J_ImgBooth"));
					System.out.println("Pictures in Price Category: " + (i + 1) + " "
							+ imgEle.getAttribute("src"));
	
					filePath = Utils.saveImage(imgEle.getAttribute("src"),
							folder + "/category", filePrefix + "_C_" + (i + 1));	
				}
	
				if (filePath != null) {
					WebElement storage = driver.findElement(By.id("J_EmStock"));
					notes.add((i + 1) + Constants.TM_SEPERATOR + wb.getText() + Constants.TM_SEPERATOR + price + Constants.TM_SEPERATOR
							+ storage.getText() + Constants.TM_SEPERATOR + filePath);
				}else{
					filePath = "NOTHING";
					WebElement storage = driver.findElement(By.id("J_EmStock"));
					notes.add((i + 1) + Constants.TM_SEPERATOR + wb.getText() + Constants.TM_SEPERATOR + price + Constants.TM_SEPERATOR
							+ storage.getText() + Constants.TM_SEPERATOR + filePath);
				}
	
			}
		}else{
			//无价格分类情况
			String price = driverUtils.getVisibleElement(By.id("J_StrPriceModBox")).findElement(By.className("tm-price")).getText();
			String stock = driverUtils.getVisibleElement(By.id("J_EmStock")).getText();
			String filePath = "NOTHING";
			//1.###27246（18寸）###19.99###库存977件###D:/Taobao/圣诞专区/F0810003/category/F0810003_C_1.jpg
			notes.add("1.###一口价###" + price + Constants.TM_SEPERATOR
					+ stock + Constants.TM_SEPERATOR + filePath);
		}

		notes.add(">>#PROPERTIES#----------");

		// 产品自定义属性
		if (driverUtils.isElementExisted(By.id("J_AttrUL"))) {
			WebElement attr = driver.findElement(By.id("J_AttrUL"));
			notes.add("品牌名称：Highparty/孩派");
			List<WebElement> attrList = attr.findElements(By.tagName("LI"));
			for (WebElement w : attrList) {
				notes.add(w.getText());
			}
		}

		notes.add(">>#DETAILS#----------");

//		driverUtils.scrollToEnd();
		// Scroll down to page
		// clickOnElement(By.id("J_Detail"));

		// try {
		// js.executeScript("window.scrollBy(0,1400)", "");
		// Thread.sleep(5000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// for (int i = 0; i < 10; i++){
		// Utils.scrollDown();
		// }

		// 移到最下方，以显示所有内容
		// for (int i = 0; i < 15; i++) {
		// Utils.scrollDown();
		// }
//		driverUtils.scrollToEnd();
		Thread.sleep(3000);
		elem = driverUtils.getVisibleElement(By.className("ke-post"));
		list = driverUtils.getChildren(elem);
		int imgSeq = 1;
		for (int i = 0; i < list.size(); i++) {
			WebElement wb = list.get(i);
			// System.out.println("Detail..." + wb.getText() + " [tag]:"
			// + wb.getTagName() + " IMG:" + wb.getAttribute("src"));
			driverUtils.scrollIntoView(wb);
			if (!wb.isDisplayed()) {
				continue;
			}

			// 忽略 店铺热销
			if (wb.getText().contains("店铺热销")) {
				System.out.println("Detail ignore...店铺热销");
				continue;
			}

			// 相关文字输出
			String txtTmp = wb.getText().trim();
			if (!txtTmp.equals("")){
				notes.add(Constants.TM_SEPERATOR + txtTmp);	
			}

			if (wb.getTagName().equalsIgnoreCase("img")) {
//				System.out.println("详细图片：" + wb.getAttribute("src"));
				String filePath = Utils.saveImage(wb.getAttribute("src"),
						folder + "/details", filePrefix + "_D_" + imgSeq);
				if (filePath != null) {
					//处理图片，重新设置高度
					Utils.resizeInWidth(filePath, Constants.PIC_MAX_WIDTH);
					List<String> results = Utils.croppImg(filePath, Constants.PIC_MAX_HEIGHT);
					if (!results.isEmpty()){
						//保存分割后图片
						for (String filePathPart : results){
							notes.add(imgSeq + Constants.TM_SEPERATOR + filePathPart.replace("\\", "/"));	
						}
					}else{
						notes.add(imgSeq + Constants.TM_SEPERATOR + filePath);	
					}
					
					imgSeq++;
				}
			}
			// for (int s = 0; s < 10; s++){
			// Utils.scrollDown();
			// }
			// 下载细节图
		
			List<WebElement> imgList = wb.findElements(By.tagName("IMG"));
			for (WebElement tmp : imgList){
				if (!Utils.isImageExist(tmp.getAttribute("src"))) {
					driverUtils.scrollIntoView(tmp);
					imgList = wb.findElements(By.tagName("IMG"));
				} 
			}

			if (imgList.size() > 0) {
				for (int k = 0; k < imgList.size(); k++) {
					WebElement imgEle = imgList.get(k);
					if (imgEle.getTagName().equalsIgnoreCase("img")) {
						String imgUrl = imgEle.getAttribute("src");
						System.out.println("Pictures in detail panel： " + imgUrl);
						if (imgUrl.substring(imgUrl.lastIndexOf(".")).length() > 4) {
							System.out.println("	Invalid picture address");
							continue;
						}
						String filePath = Utils.saveImage(imgUrl, folder
								+ "/details", filePrefix + "_D_" + imgSeq);
						if (filePath != null) {
							//处理图片，重新设置高度
							Utils.resizeInWidth(filePath, Constants.PIC_MAX_WIDTH);
							List<String> results = Utils.croppImg(filePath, Constants.PIC_MAX_HEIGHT);
							if (!results.isEmpty()){
								//保存分割后图片
								for (String filePathPart : results){
									notes.add(imgSeq + Constants.TM_SEPERATOR + filePathPart.replace("\\", "/"));	
								}
							}else{
								notes.add(imgSeq + Constants.TM_SEPERATOR + filePath);	
							}							
						}
					}

					imgSeq++;
				}
			}
		}
	}
	
}
