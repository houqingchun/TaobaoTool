import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	private WebDriver driver;

	public Main(WebDriver driver) {
		this.driver = driver;
		
		this.driver.manage().window().maximize();
	}

	public void login() throws Exception {
		// driver.get("https://login.taobao.com/member/login.jhtml");
		driver.get(Constants.LOGIN_PAGE);
		//clickOnElement(By.cssSelector(".forget-pwd.J_Quick2Static"));
//		Thread.sleep(3000);
		//sendKeys(By.id("TPL_username_1"), "孩派天津");
//		Thread.sleep(3000);
		//sendKeys(By.id("TPL_password_1"), "Dan811005");
//		Thread.sleep(20000);
		//clickOnElement(By.id("J_SubmitStatic"));
		
		while (driver.getCurrentUrl().contains("login.jhtml")){
			System.out.println("Waiting for login...");
			Thread.sleep(1000);
		}
	}

	private void close() {
		driver.close();
		driver.quit();
	}
	

	/**
	 * 获取用户操作选择
	 * @return
	 */
	public static String getUserAction(){
		Scanner userInput = new Scanner(System.in);
		System.out.println("Select action  you want to procced and correspond parameters. e.g., 1/100-105");
		System.out.println("--------------------------------");
		System.out.println("[1] Create folder in Photo Space. Example:1/100-105");
		System.out.println("[2] Download products. Example:2/100-105");
		System.out.println("[3] Upload products. Example:3/100-105");
		System.out.println("[4] Download&Upload products. Example:4/100-105");
		System.out.println("[5] Remove duplicates. Example:5/100-105");
		System.out.println("[6] Update products. Example:6/100-105");
		System.out.println("[7] Detect uncomplete products. Example:6/100-105");
		System.out.println("[8] Generate links from TM.");
		System.out.println("----------------------------------------------------------------");
		
		String line = null;
		int action = 0;
		boolean isEnd = false;
		while (userInput.hasNextLine()){
			line = userInput.nextLine();
			if (line.split("/").length == 2){
				String actStr = line.split("/")[0];
				String[] idxAry = line.split("/")[1].split("-");

				String startStr = null;
				String endStr = null;
				if (idxAry.length == 1) {
					startStr = line.split("/")[1].split("-")[0];
					endStr = startStr;
				}else if   (idxAry.length == 2) {
					startStr = line.split("/")[1].split("-")[0];
					endStr = line.split("/")[1].split("-")[1];
				}else {
					System.err.println("Invalid option.");
					continue;
				}
				
				if (StringUtils.isNumeric(actStr) && StringUtils.isNumeric(startStr) && StringUtils.isNumeric(endStr)){
					action = Integer.valueOf(actStr);
					if (action >= 1 && action <= 8){
						isEnd = true;
						break;
					}else{
						System.err.println("Invalid option.");
						continue;
					}
				}else{
					System.err.println("Invalid option.");
					continue;
				}
			}else if (line.split("/").length == 1){
				String actStr = line.split("/")[0];
				action = Integer.valueOf(actStr);
				isEnd = true;
				break;
			}else{
				System.err.println("Invalid option.");
				continue;
			}
		}
		
		if (!isEnd){
			line = getUserAction();
		}
		
		userInput.close();
		
		return line;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding","UTF-8");
		System.setProperty("client.encoding.override","UTF-8");
		
		String userInput = Main.getUserAction();
		String[] inputAry = userInput.split("/");
		
		int action = Integer.valueOf(inputAry[0]);
		int start = 0, end = 0;
		if (inputAry.length == 2){
			String[] parmAry = inputAry[1].split("-");
			if (parmAry.length == 1) {
				start = Integer.valueOf(parmAry[0]);
				end = start;
			}else {
				start = Integer.valueOf(parmAry[0]);
				end = Integer.valueOf(parmAry[1]);
			}

			
			System.out.println("Action: " + action + " start:" + start + " end:" + end);
		}
		
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
		Main bs = new Main(driver);
		
		switch (action){
			case 1:
				//Create folder
				bs.login();
				PhotoLib pLib = new PhotoLib(driver);
				pLib.startCreateFolders(start, end);
				break;
			case 2:
				//download
				TaobaoDownload d = new TaobaoDownload(driver);
				d.startDownload(start, end);
				break;
			case 3:
				//upload
				bs.login();
				TaobaoUpload up = new TaobaoUpload(driver);
				up.startUploadProduct(start, end);
				break;
			case 4:
				//download and upload if data does not exist 
				bs.login();
				TaobaoUpload up1 = new TaobaoUpload(driver);
				up1.startUploadProduct(start, end, true);
				break;
			case 5:
				//upload
				bs.login();
				TaobaoUpload rm = new TaobaoUpload(driver);
				rm.startRemoveDups(start, end);
				break;
			case 6:
				//update
				bs.login();
				TaobaoUpload ud = new TaobaoUpload(driver);
				ud.startUpdateProduct(start, end);
				break;
			case 7:
				//detect uncomplete products
				bs.login();
				TaobaoUpload uc = new TaobaoUpload(driver);
				uc.startDetectUncompleteProduct(start, end);
				break;
			case 8:
				TaobaoMenu link = new TaobaoMenu(driver);
				link.startGenerating();
				break;
				
		}
	}
}
