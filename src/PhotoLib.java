import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class PhotoLib {
	WebDriver driver;
	WebDriverUtils driverUtils;
	public PhotoLib(WebDriver driver){
		this.driver = driver;
		this.driverUtils = new WebDriverUtils(driver);
	}
	
	/**
	 * 创建 图片目录
	 * @param folderName
	 */
	private boolean createFolderInTargetNav(WebElement parent, Stack<String> stack, boolean createNewFolder){
		boolean found = false;
		String nextFldName = stack.pop();
		
		//检查下级目录是否存在
		WebElement nextFldObj = null;
		try{
			if (parent != null){
				nextFldObj = this.getElemFromParentByTitle(parent, nextFldName);
			}else{
				nextFldObj = driver.findElement(By.cssSelector("a[title='" + nextFldName + "']"));	
			}			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		//不存在，则创建
		if (nextFldObj == null && createNewFolder){
			System.out.println("Folder " + nextFldName + " is created.");
			try {
				driverUtils.clickOnElement(By.xpath("//*[@id=\"J_UpAndNew\"]/button[2]"));
				driverUtils.sendKeys(By.id("J_NewFoldername"), nextFldName);
				driverUtils.clickOnElement(By.id("J_ModalSure"));
				
				Thread.sleep(1000);
				//查询是否创建成功，若创建太快或失败，则确定按扭未消失
				if (driverUtils.isElementExisted(By.id("J_ModalSure"))){
					WebElement msgBox = driver.findElement(By.id("J_Infor_Box"));
					if (msgBox.getAttribute("innerText").contains("喝口水")){
						System.out.println(">>>上传太快，喝口水吧。。。");
						//休息20秒，继续尝试
						Thread.sleep(20000);
					}else{
						//重复错误
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			if (parent != null){
				nextFldObj = this.getElemFromParentByTitle(parent, nextFldName);
			}else{
				nextFldObj = driver.findElement(By.cssSelector("a[title='" + nextFldName + "']"));	
			}
		}else{
			driverUtils.scrollIntoView(nextFldObj);
			//存在的话，则继续查找下一级目录
			//若存在 + 号，则展开
			WebElement pTmp = driverUtils.getParent(nextFldObj);
			List<WebElement> childs = driverUtils.getChildren(pTmp);
			if (childs.size() > 0){
				WebElement icon = childs.get(0);
				if (icon.getAttribute("class").contains("center_close") || icon.getAttribute("class").contains("bottom_close")){
					driverUtils.clickOnElement(icon);
				}
			}
			//选中当前目录
			driverUtils.clickOnElement(nextFldObj);
	
			if (!stack.isEmpty()){
				//继续进行下一级
				found = createFolderInTargetNav(nextFldObj, stack, createNewFolder);	
			}
		}
		
		return found;
	}
	
	/**
	 * 根据父节点和标题，查找元素
	 * @param parent
	 * @param title
	 * @return
	 */
	private WebElement getElemFromParentByTitle(WebElement parent, String title){
		WebElement ret = null;
		List<WebElement> childs = driverUtils.getChildren(driverUtils.getParent(parent));
		if (childs.size() == 3){
			WebElement ul = childs.get(2);
			childs = driverUtils.getChildren(ul);
			for (WebElement t : childs){
//				System.out.println(">>>" + t.getText());
				try{
					ret = t.findElement(By.cssSelector("a[title='" + title + "']"));
					if (ret != null){
						break;	
					}							
				}catch (Exception e){
					continue;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 导航
	 * @param folders
	 * @throws Exception
	 */
	private boolean navigateToPath(String navigation, boolean createNewFolder) {
		//取消续费公告
		try{
			WebElement cancelBtn = driver.findElement(By.xpath("//*[@id=\"tu-business-2\"]/div/div/div[3]/div")); 
			if (cancelBtn != null && cancelBtn.isDisplayed()){
				driverUtils.clickOnElement(cancelBtn);
			}	
		}catch (Exception e){
			
		}
		String[] nav = navigation.split("/");
		Stack<String> stack = new Stack<String>();
		for (int i = nav.length-1; i >= 0; i--){
			stack.push(nav[i]);
		}
		return createFolderInTargetNav(null, stack, createNewFolder);
	}
	
	/**
	 * 获取淘宝图片目录树
	 * @param imgFolder
	 * @return
	 */
	private String getRelativeTaobaoFld(String imgFolder){
		String validStr = imgFolder.replace("\\", "/");
//		String fullPath = FilenameUtils.getFullPathNoEndSeparator(imgFolder);
		String drivePath = Constants.SHARE_FOLDER_PATH;
		if (!drivePath.endsWith("/")){
			drivePath +="/";
		}
		
		return "宝贝图片/" + validStr.replace(drivePath, "");
	}
	
	/**
	 * 发布产品时，选择图片
	 * @param imgFolder
	 * @param imgFileNameList
	 */
	public boolean selectPhotoFromLib(String imgFolder, String imgFileName){
		boolean found = false;
		try {
			//切换至从图片空间选择
			driverUtils.clickOnElement(By.linkText("从图片空间选择"));
			
			//导航
			this.navigateToPath("图片空间/" + this.getRelativeTaobaoFld(imgFolder), false);
			
			//列表显示
			driverUtils.clickOnElement(By.cssSelector("a.category-list.media-iconfont.media-iconfont-list"));
			
			//根据文件名查找并点击
			WebElement results = driver.findElement(By.className("search-result"));
			List<WebElement> items = driverUtils.getChildren(results);
			for (WebElement elem : items){
				String innerHTML = elem.getAttribute("innerHTML");
				
				if (innerHTML.contains("title=\"" + imgFileName + "\"")){
					found = true;
					driverUtils.clickOnElement(elem);
//					Thread.sleep(1000);
				}					
			}

			if (driverUtils.isElementExisted(By.linkText("插入"))){
				//点击插入
				driverUtils.clickOnElement(By.linkText("插入"));	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			found = false;
		}
		
		return found;
	}
	
	/**
	 * 根据给定目录，开始上传
	 * @param folder
	 */
	public void startUpload(String folder){
		//跳转到图片空间
		driver.navigate().to(Constants.PHOTO_PAGE);
		
		this.uploadFilesInEachFolder(new File(folder).listFiles());
	}
	
	/**
	 * 根据给定目录，创建目录结构到产品编码一级
	 * @param folder
	 */
	public void startCreateFolders(int start, int end){
		//跳转到图片空间
		driver.navigate().to(Constants.PHOTO_PAGE);
		
		String linksFilePath = Constants.SHARE_FOLDER_PATH +"/" +Constants.LINKS_FILENAME;
		
		BufferedReader br = null;
		int total = (end - start + 1);
		if (total == 1){
			total = 9999;
		}
		int seq = 0;
		int processCount = 0;
		try {
			br = new BufferedReader(new FileReader(linksFilePath));
			String thisLine;
			while((thisLine = br.readLine()) != null){
				seq++;
				if (seq > end && end > 0) {
					break;
				}

				if (seq < start && start > 0) {
					continue;
				}
				String folder = this.getRelativeTaobaoFld(thisLine.split(Constants.TM_SEPERATOR)[0]);

				System.out.println("当前行[ " + (++processCount) + " / " + total + " ]: " + folder);
				this.navigateToPath(folder, true);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if (br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	
	/**
	 * 遍历每个目录，上传图片
	 * @param files
	 */
	private void uploadFilesInEachFolder(File[] files) {
		List<String> imageNameList = new ArrayList<String>();
		String currentDirectory = null;
		for (File file : files) {
			String p = file.getAbsolutePath().replace("\\", "/");
			
			if (file.isDirectory()) {
//				System.out.println("Directory: " + file.getName());
				uploadFilesInEachFolder(file.listFiles()); // Calls same method again.
			} else {				
				if (p.endsWith(".jpg") || p.endsWith(".png") || p.endsWith(".bmp")
						|| p.endsWith(".jpeg") || p.endsWith(".gif")) {
					currentDirectory = FilenameUtils.getFullPathNoEndSeparator(p);
					
					// Only for images
					imageNameList.add(FilenameUtils.getName(p));					
				}
			}
		}
		
		//相同目录下图片统一上传
		int total = imageNameList.size();
		System.out.println("Total files: " + total);
		if (total > 0){
			for (int i = 0; i <(total/10 + 1); i++){
				int start = i*10;
				int end = (i*10 + 10);
				if (end >= total){
					end = total;
				}
				//System.out.println(start + " " + end);
				this.checkAndUploadPhotos(currentDirectory, imageNameList.subList(start, end));
			}
		}
	}
	
	/**
	 * 检查当前目录下，图片是否已经上传
	 * @param imgName
	 * @return
	 */
	private boolean isUploaded(String imgName){
		boolean found = false;
		System.out.println("");
		System.out.println("           >>>Checking upload status...[" + imgName + "]");
		try{
			List<WebElement> list = driverUtils.getChildren(driver.findElement(By.id("J_Picture"))); 
			
			for (int i = 0; i <list.size(); i++){
				WebElement tmp = list.get(i);
				if (tmp.getAttribute("innerHTML").contains("title=\"" + imgName + "\"")){
					if (!found){
						System.out.println("           >>>Uploaded!");
						found = true;
					}else{
						//Remove duplicate items
						System.out.print("           >>>Removing image: " + imgName + "...");
						driverUtils.clickOnElement(tmp);
						driverUtils.clickOnElement(By.cssSelector("#J_ControlBar > li.delete > a"));
						driverUtils.clickOnElement(By.id("J_ModalSure"));
						System.out.println(" [DONE]");
					}
				}
			}
			
//			List<WebElement> e = driver.findElements(By.cssSelector("div:contains(\"" + imgName + "\")"));
//			if (e.size() > 0){
//				//Remove Uploaded!! items
//				if (e.size() > 1){
//					System.out.print(">>>Duplicate rows:" + (e.size()-1));
//					for (int i =1; i < e.size(); i++){
//						try{
//							System.out.print(">>>>Trying to remove Uploaded!! image: " + imgName);
//							WebElement p = driverUtils.getChildren(driverUtils.getParent(e.get(i))).get(0);
//							p.click();
//							driverUtils.clickOnElement(By.cssSelector("#J_ControlBar > li.delete > a"));
//							driverUtils.clickOnElement(By.id("J_ModalSure"));
//							Thread.sleep(2000);
//							System.out.println(" REMOVED!");
//						}catch (Exception ex){
//							System.err.println(" ERROR! " + ex.getMessage());
//						}
//					}	
//				}
//				System.out.println("  Uploaded!");
//				return true;
//			}else{
//				System.out.println("  NOT FOUND");
//				return false;
//			}
		}catch (Exception e){
			System.err.println("\n           >>>Error Try again!");
			//Try again
			found = isUploaded(imgName);
		}
		
		return found;
	}
	
	/**
	 * 跳转到目标目录，检查并上传图片
	 * @param imageFolder
	 * @param imageNameList
	 */
	private void checkAndUploadPhotos(String imageFolder, List<String> imageNameList){
		try {	
			System.out.println("Uploading...[" + imageFolder + "] Total: " + imageNameList.size());
			//获取图片所有目录，妆导航至此目录
			this.navigateToPath(this.getRelativeTaobaoFld(imageFolder), true);
			Thread.sleep(2000);
			
			StringBuffer imageNamesStr = new StringBuffer();
			for (String imgTmp : imageNameList){
				System.out.print(">>[" + imgTmp + "] ");
				if (!isUploaded(imgTmp)){
					imageNamesStr.append("\"" + imgTmp + "\" ");
				}
			}
			
			
			if (imageNamesStr.toString().length() == 0){
				return;
			}
			
			//上传图片按扭
			driverUtils.clickOnElement(By.xpath("//*[@id=\"J_UpAndNew\"]/button[1]"));
			
			//点击上传
			driverUtils.clickOnElement(By.className("flash-up-btn"));
			
			//勾选水印
			WebElement wm = driver.findElement(By.id("J_IsWater"));
			if (!Boolean.valueOf(wm.getAttribute("checked"))) {
				driverUtils.clickOnElement(wm);
			}
			
			//好传图片
			Utils.uploadFile(imageFolder.toString().replace("/", "\\"), imageNamesStr.toString());
			
			//关闭上传成功列表框
			WebElement tmpDiv = driver.findElement(By.cssSelector("span[title='最大化']"));
			while (tmpDiv == null || !tmpDiv.isDisplayed()){				
				Thread.sleep(1000);
				tmpDiv = driver.findElement(By.cssSelector("span[title='最大化']"));
			}
			
			//关闭上传文件弹出窗口
			driverUtils.clickOnElement(By.cssSelector("span[title='关闭']"));			
			
			System.out.println(" DONE!");			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(" ERROR!");	
		}
	}
	
	public static void main(String[] args){
//		String p = "D:/Taobao/蜡烛/生日-卡通/K0150002/details";
//		System.out.println(FilenameUtils.getFullPath(p));
//		System.out.println(FilenameUtils.getPath(p));
//		int total = 25;
//		for (int i = 0; i <(total/10 + 1); i++){
//			int start = i*10;
//			int end = (i*10 + 10);
//			if (end >= total){
//				end = total;
//			}
//			
//			System.out.println((start) + " " + (end));
//		}
		
//		String s = "2. D:/Taobao/蜡烛/数字-生肖/G0000009/details/2_1.jpg";
//		String[] tmp = s.split(" ");
//		if (tmp.length == 2 && (new File(tmp[1])).isFile()){
//			System.out.println("exist");
//		}
		String s = "D:/products/迪士尼正品/超能陆战队/4";
		System.out.println(s.substring(s.lastIndexOf("/")+1));
	}
}
