import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TaobaoMenu {

	WebDriver driver = null;
	WebDriverUtils driverUtils = null;

	public TaobaoMenu(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().window().maximize();
		driverUtils = new WebDriverUtils(driver);
	}

	/**
	 * 生成菜单和相应菜单链接
	 */
	public void generateMenus() {
		Utils.createFolder(Constants.SHARE_FOLDER_PATH);

		File f = new File(Constants.SHARE_FOLDER_PATH + "/"
				+ Constants.TM_MENU_LINKS_FILENAME);

		if (!f.exists()) {
			System.out.println("Generating menus...");
			
			List<String> menuList = new ArrayList<String>();
			WebElement parent = driver.findElement(By.className("J_TCatsTree"));

			List<WebElement> liOnelist = driverUtils.getChildren(parent);
			// System.out.println("Count of LI:" + liOnelist.size());
			// 第一层
			for (int i = 1; i < liOnelist.size(); i++) {

				// 根
				WebElement root = liOnelist.get(i).findElement(
						By.cssSelector("H4>A"));
				WebElement icon = driverUtils.getChildren(
						driverUtils.getParent(root)).get(0);
				// 若图标未展开，则先展开
				if (!icon.getAttribute("class").contains("active-trigger")) {
					driverUtils.clickOnElement(icon);
				}

				String rootText = Utils.filterNonBreakSpace(root.getText()
						.replace("/", "-"));

				List<WebElement> childs = liOnelist.get(i).findElements(
						By.tagName("UL"));
				if (childs.size() == 1) {
					List<WebElement> links = childs.get(0).findElements(
							By.tagName("A"));
					for (int k = 0; k < links.size(); k++) {
						WebElement link = links.get(k);
						String subLinkText = Utils.filterNonBreakSpace(link
								.getText().replace("/", "-"));
						String line = rootText + "/" + subLinkText
								+ Constants.TM_SEPERATOR
								+ link.getAttribute("href");
						System.out.println(line);
						menuList.add(line);

					}
				} else {
					String line = rootText + Constants.TM_SEPERATOR
							+ root.getAttribute("href");
					System.out.println(line);
					menuList.add(line);
				}
			}

			Utils.createFile(Constants.SHARE_FOLDER_PATH,
					Constants.TM_MENU_LINKS_FILENAME, menuList);

		}

	}

	public void startGenerating() {
		System.out.println("Generating product links...");
		Utils.createFolder(Constants.SHARE_FOLDER_PATH);

		// Get to homepage
		driver.navigate().to(Constants.HOME_PAGE);

		// Get all menu items
		this.generateMenus();

		String menuFile = Constants.SHARE_FOLDER_PATH + "/"
				+ Constants.TM_MENU_LINKS_FILENAME;
		String logFile = Constants.SHARE_FOLDER_PATH
				+ "/tm_menu_links_success.txt";

		Set<String> generatedMenus = Utils.getInfoFromLogs(logFile, 0);

		BufferedReader br = null;
		String line;
		try {
			// //////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(menuFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:2
				String menu = ary[0];
				String link = ary[1];

				if (generatedMenus.contains(menu)) {
					continue;
				}

				this.generateProductLinksForEachMenu(menu, link);

				// 若生成产品链接 成功，则记录日志
				Utils.addToLog(logFile, line);
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateProductLinksForEachMenu(String menuPath, String link) {

		List<String> allLinks = new ArrayList<String>();

		// 跳转至菜单子类
		driver.get(link);

		// 获取菜单下所有链接
		System.out
				.println("************************************************************");
		System.out.println(menuPath + Constants.TM_SEPERATOR + link);
		System.out
				.println("------------------------------------------------------------");

		// 跳转至菜单子类
		driver.get(link);

		allLinks.addAll(getProductLinksInMenu(menuPath, null, 1));

		// 保存所有产品链接及菜单对应关系
		Utils.addToLog(Constants.SHARE_FOLDER_PATH + "/"
				+ Constants.LINKS_FILENAME, allLinks, true);
	}

	private List<String> getProductLinksInMenu(String menuPath,
			String codePrefix, int currentItemSeq) {
		List<String> itemList = new ArrayList<String>();
		if (codePrefix == null) {
			codePrefix = Utils.generateRandomPrefix();
		}

		try {
			WebElement parent = driverUtils.getVisibleElement(By
					.className("J_TItems"));

			List<WebElement> items = driverUtils.getChildren(parent);
			for (int i = 0; i < items.size(); i++) {
				WebElement itemLine = items.get(i);
				if (itemLine.getAttribute("class").equals("pagination")) {
					break;
				}

				List<WebElement> details = itemLine.findElements(By
						.className("J_TGoldData"));
				// List<WebElement> details =
				// itemLine.findElements(By.xpath("//a[@class='item-name J_TGoldData']"));
				// System.out.println(details.size());
				// 生成编号前缀
				for (int k = 0; k < details.size(); k++) {
					if (k % 2 != 0) {
						WebElement detail = details.get(k);
						// driverUtils.scrollIntoView(detail);

						String url = detail.getAttribute("href");
						String productId = url.substring(
								url.indexOf("?id=") + 4, url.indexOf("&rn="));
						String linkText = detail.getText();
						String pTmp = menuPath + "/" + codePrefix
								+ Utils.formatNumber(currentItemSeq++)
								+ Constants.TM_SEPERATOR + productId
								+ Constants.TM_SEPERATOR + linkText;
						System.out.println(pTmp);
						itemList.add(pTmp);
					}
				}
			}

			// 若有分页，继续查找
			WebElement pageNbr = driver.findElement(By
					.className("ui-page-s-len"));
			String nbrStr = pageNbr.getText();
			String[] nbrAry = nbrStr.split("/");
			if (nbrAry.length == 2 && !nbrAry[0].equals(nbrAry[1])) {
				driver.findElement(By.className("ui-page-s-next")).click();
				getProductLinksInMenu(menuPath, codePrefix, currentItemSeq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(menuPath);
		}

		return itemList;
	}

}
