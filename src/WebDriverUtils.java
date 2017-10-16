import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class WebDriverUtils {
	private WebDriver driver;
	private JavascriptExecutor js;
	private WebDriverWait wait;
	public WebDriverWait getWait() {
		return wait;
	}

	public void setWait(WebDriverWait wait) {
		this.wait = wait;
	}

	public void waitForPageLoad(){
 		ExpectedCondition pageLoads = new  ExpectedCondition() {
				@Override
				public Object apply(Object arg0) {
					// TODO Auto-generated method stub
					return (Boolean)((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
				}
            };
        wait.until(pageLoads);
	}

	public void scrollIntoView(By by) {
		WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		this.scrollIntoView(elem);
	}
	
	public void scrollIntoView(WebElement elem) {
		js.executeScript("arguments[0].scrollIntoView(true);", elem);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public WebElement getParent(WebElement elem) {
		return elem.findElement(By.xpath("./.."));
	}
	
	public void scrollDown(){
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
			Thread.sleep(1000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void scrollToEnd(){
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_END);
			robot.keyRelease(KeyEvent.VK_END);
			Thread.sleep(1000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private boolean isAllImagesVisible(List<WebElement> imgList) {
		for (int k = 0; k < imgList.size(); k++) {
			WebElement imgEle = imgList.get(k);
			String url = imgEle.getAttribute("src");
			if (imgEle.getTagName().equalsIgnoreCase("img")) {
				if (!imgEle.isDisplayed() || !Utils.isImageExist(url)) {
					System.out.println("Waiting... "
							+ imgEle.getAttribute("src"));
					return false;
				}
			}
		}

		return false;
	}

	
	public void scrollUp(){
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_PAGE_UP);
			robot.keyRelease(KeyEvent.VK_PAGE_UP);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<WebElement> getChildren(WebElement elem){
		return elem.findElements(By.xpath("./*"));
	}
	
	public JavascriptExecutor getJs() {
		return js;
	}

	public void setJs(JavascriptExecutor js) {
		this.js = js;
	}

	public WebDriverUtils(WebDriver driver){
		this.driver = driver;
		this.js = (JavascriptExecutor) driver;
		this.wait = new WebDriverWait(driver, 100);
	}

	public void clickOnElementWithJS(String tagName, String textContent) {
		String script = "";
		script += "var aTags = document.getElementsByTagName('" + tagName
				+ "');	";
		script += "var searchText = '" + textContent + "';	";
		script += "var found;	";

		script += "	for (var i = 0; i < aTags.length; i++) {	";
		script += "		if (aTags[i].textContent == searchText) {	";
		script += "			found = aTags[i];	";
		script += "			found.click();";
		script += "			break;	";
		script += "		}";
		script += "}";
		System.out.println(script);
		js = (JavascriptExecutor) driver;
		js.executeScript(script);
	}
	

	public WebElement getVisibleElement(By by){
		WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		js.executeScript("arguments[0].scrollIntoView(true);", elem);
		this.scrollIntoView(elem);
		
		return elem;
	}

	public void clickOnElement(By by) throws Exception {
		WebElement elem = null;
		try {
			Thread.sleep(500);
			Actions builder = new Actions(driver);

			elem = wait.until(ExpectedConditions
					.elementToBeClickable(by));
			js.executeScript("arguments[0].scrollIntoView(true);", elem);
			Thread.sleep(300);
			builder.moveToElement(elem).click().build().perform();
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println("<<Click failed, try with JS>>");
			js.executeScript("arguments[0].click();", elem);
			
			//throw e;
			// try{
			// System.out.println("Try to click again thorugh javascript...");
			// js.executeScript("arguments[0].click();",
			// driver.findElement(by));
			// }catch (Exception ex){
			// ex.printStackTrace();
			// hasError = true;
			// }
		}
	}
	
	public void acceptAlertMsg(){
		try{
			//Check alert, and Accept
			Alert alert = driver.switchTo().alert();
			if (alert != null) {
				System.out.println("Accept Message: " + alert.getText());
				alert.accept();	
			}	
		}catch (Exception e){
			
		}
	}
	
	public void dismissAlertMsg(){
		try{
			//Check alert, and Accept
			Alert alert = driver.switchTo().alert();
			if (alert != null) {
				System.out.println("Dismiss Message: " + alert.getText());
				alert.dismiss();;	
			}	
		}catch (Exception e){
			
		}
	}
	
	public void clickOnElementInFrame(By by) throws Exception {
		// 上传图片
		//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		List<WebElement> frmList = driver.findElements(By.tagName("IFRAME"));
		driver.switchTo().frame(frmList.get(frmList.size() - 1));
		
		this.clickOnElement(by);
		
		driver.switchTo().defaultContent();
	}

	public boolean isElementExisted(By locator) {
		try {
			WebElement e = driver.findElement(locator);
			if (e != null && e.isDisplayed()){
				return true;
			}
		} catch (NoSuchElementException ex) {
			return false;
		}
		return false;
	}

	public void clickOnElement(WebElement wb) {
		WebElement elem = null;
		try {
			Actions builder = new Actions(driver);
			elem = wait.until(ExpectedConditions
					.elementToBeClickable(wb));
			js.executeScript("arguments[0].scrollIntoView(true);", elem);
			builder.moveToElement(elem).click().build().perform();
			Thread.sleep(100);
		} catch (Exception e) {
			System.err.println("<<Click failed, try with JS>>");
			js.executeScript("arguments[0].click();", elem);
			// try{
			// System.out.println("Try to click again thorugh javascript...");
			// js.executeScript("arguments[0].click();", wb);
			// }catch (Exception ex){
			// ex.printStackTrace();
			// hasError = true;
			// }
		}
	}

	public void sendKeys(By by, String value) throws Exception {
		try {
//			Thread.sleep(5000);
			Actions actions = new Actions(driver);
			
//			WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			WebElement elem = wait.until(
				    ExpectedConditions.elementToBeClickable(by)
				);
			js.executeScript("arguments[0].scrollIntoView(true);", elem);
			Thread.sleep(300);
//			WebElement elem = driver.findElement(by);
			
			actions.moveToElement(elem);
			actions.click();
//			try{
//				elem.clear();
//			}catch(Exception e){
//				System.err.println("Clear failed...");
//			}
//			elem.clear();
			actions.sendKeys(value).sendKeys(Keys.TAB);
			actions.build().perform();
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	

	public void sendKeys(WebElement elem, String value) throws Exception {
		try {
			Actions actions = new Actions(driver);

			js.executeScript("arguments[0].scrollIntoView(true);", elem);
			Thread.sleep(300);
			
			actions.moveToElement(elem);
			actions.click();
//			try{
//				elem.clear();
//			}catch(Exception e){
//				System.err.println("Clear failed...");
//			}
			actions.sendKeys(value).sendKeys(Keys.TAB);
			actions.build().perform();
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
}
