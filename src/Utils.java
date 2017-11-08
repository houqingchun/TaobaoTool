import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

public class Utils {
	public static boolean addToLog(String filePathName, String content) {
		return addToLog(filePathName, content, true);
	}

	public static boolean addToLog(String filePathName, List<String> content, boolean append) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(filePathName, append);
			out = new BufferedWriter(fstream);
			for (String s : content) {
				out.write(s);
				out.newLine();
			}
			return true;
		} catch (Exception e) {

		} finally {
			// close buffer writer
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public static boolean addToLog(String filePathName, String content, boolean append) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(filePathName, append);
			out = new BufferedWriter(fstream);
			out.write(content);
			out.newLine();
			return true;
		} catch (Exception e) {

		} finally {
			// close buffer writer
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	private static String downloadImage(String url, String folder, String fileName) throws IOException {
		// System.out.print("URL:" + url);
		try {
			String ext = url.substring(url.lastIndexOf(".") + 1);
			File f = new File(folder);
			if (!f.exists()) {
				f.mkdirs();
			}

			String filePath = folder + "/" + fileName + "." + ext;
			URL imageURL = new URL(url);

			BufferedImage saveImage = ImageIO.read(imageURL);

			ImageIO.write(saveImage, ext, new File(filePath));
			return filePath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	public static String generateRandomPrefix() {
		Random r = new Random();
		char c = (char) (r.nextInt(26) + 'A');
		String prefix = String.valueOf(c);

		return prefix + String.format("%03d", (int) (Math.random() * 100));
	}

	public static String formatNumber(int nbr) {
		return String.format("%04d", nbr);
	}

	public static boolean isImageExist(String imageUrl) {
		if (imageUrl.endsWith("90-90.png")) {
			return false;
		}
		InputStream is = null;
		OutputStream os = null;
		URLConnection conn = null;

		try {
			if (Constants.USE_PROXY) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(Constants.PROXY_HOST, Constants.PROXY_PORT));
				conn = new URL(imageUrl).openConnection(proxy);
			} else {
				conn = new URL(imageUrl).openConnection();
			}

			is = conn.getInputStream();
			BufferedImage saveImage = ImageIO.read(is);
			if (saveImage.getHeight() != 90 && saveImage.getWidth() != 90) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static String saveImage(String imageUrl, String folder, String fileName) throws Exception {
		InputStream is = null;
		OutputStream os = null;
		URLConnection conn = null;

		try {
			if (Constants.USE_PROXY) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(Constants.PROXY_HOST, Constants.PROXY_PORT));
				conn = new URL(imageUrl).openConnection(proxy);
			} else {
				conn = new URL(imageUrl).openConnection();
			}

			String ext = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
			File f = new File(folder);
			if (!f.exists()) {
				f.mkdirs();
			}

			String filePath = folder + "/" + fileName + "." + ext;

			is = conn.getInputStream();
			os = new FileOutputStream(filePath);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			return filePath;
		} catch (FileNotFoundException fe) {
			System.err.println(" NOT FOUND");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	public static void createFile(String folder, String fileName, List<String> contentList) {
		createFolder(folder);

		File fout = new File(folder + "/" + fileName);
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (int i = 0; i < contentList.size(); i++) {
				bw.write(contentList.get(i));
				bw.newLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void createFolder(String folder) {
		File f = new File(folder);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	public static boolean uploadFile(String filePath) {
		StringSelection stringSelection = new StringSelection(filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

		// native key strokes for CTRL, V and ENTER keys
		try {
			Robot robot = new Robot();
			robot.delay(2000);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.delay(500);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.delay(1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean uploadFile(String folder, String nameList) {
		if (uploadFile(folder)) {
			return uploadFile(nameList);
		}

		return false;
	}

	public static String formatFilePath(String path) {
		return path.replace("/", "\\");
	}

	public static String formatTitleDesc(String title) {
		String tmp = title;
		if (title.startsWith("孩派儿")) {
			tmp = title.substring(3);
		} else if (title.startsWith("孩派")) {
			tmp = title.substring(2);
		}

		String newTitle = "天津";
		if (tmp.length() <= 28) {
			newTitle = newTitle + tmp;
		} else {
			String t = tmp.replace("成人", "");
			if (t.length() <= 28) {
				newTitle = newTitle + t;
			} else {
				newTitle = newTitle + tmp.substring(0, 28);
			}
		}

		newTitle = newTitle.replace("顶级", "高档"); // 标题中不可有顶级，涉嫌违反广告法

		return newTitle;
	}

	public static void croppImgs(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				croppImgs(file.listFiles()); // Calls same method again.
			} else {
				String abPath = file.getAbsolutePath().replace("\\", "/");

				if (abPath.contains("details")) {
					if (abPath.endsWith(".jpg") || abPath.endsWith(".png") || abPath.endsWith(".bmp")
							|| abPath.endsWith(".jpeg") || abPath.endsWith(".gif")) {
						// Only for images in details page

						resizeInWidth(abPath, Constants.PIC_MAX_WIDTH);
						List<String> results = croppImg(abPath, Constants.PIC_MAX_HEIGHT);
						if (!results.isEmpty()) {

							// Update Note.txt file
							updateNotesFile(abPath, results);
						}
					}
				}
			}
		}
	}

	/**
	 * 遍历目录下所有NOTES.TXT，并更新所包括的图形文件，按固定编码格式重命名
	 * 
	 * @param folder
	 */
	public static void startRename(String folder) {
		readNotesAndRenameFiles(new File(folder).listFiles());
	}

	/**
	 * 处理特定details文件夹下图片，高度超过指定值时，自动分割，同时按标准宽度重新设置大小
	 * 
	 * @param folder
	 */
	public static void startCutPics(String folder) {
		croppImgs(new File(folder).listFiles());
	}

	/**
	 * 遍历文件夹，查找NOTES，并处理
	 * 
	 * @param files
	 */
	private static void readNotesAndRenameFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				readNotesAndRenameFiles(file.listFiles()); // Calls same method
															// again.
			} else {
				String abPath = file.getAbsolutePath().replace("\\", "/");
				System.out.println("File: " + abPath);
				if (FilenameUtils.getName(abPath).equals(Constants.EACH_PRODUCT_NOTE_FILENAME)) {
					renameFileInNotesFile(abPath);
				}
			}
		}
	}

	/**
	 * 遍历文件夹，查找NOTES，并处理
	 * 
	 * @param files
	 */
	private static void readNotesTitles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				// System.out.println("Directory: " + file.getName());
				readNotesTitles(file.listFiles()); // Calls same method again.
			} else {
				String abPath = file.getAbsolutePath().replace("\\", "/");
				// System.out.println("File: " + abPath);
				if (FilenameUtils.getName(abPath).equals(Constants.EACH_PRODUCT_NOTE_FILENAME)) {
					BufferedReader br = null;
					String line = "";
					try {
						br = new BufferedReader(new FileReader(file));
						boolean found = false;
						while ((line = br.readLine()) != null) {
							if (found) {
								System.out.println(file.getAbsolutePath() + Constants.TM_SEPERATOR + line);
								found = false;
								break;
							}
							if (line.startsWith(">>#TITLE#")) {
								found = true;
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 根据NOTES 记录 重新命名路径内文件，并更新NOTES
	 * 
	 * @param notesFilePath
	 */
	private static void renameFileInNotesFile(String notesFilePath) {
		System.out.println("Renaming files...");
		String notesPath = notesFilePath.replace("\\", "/");

		String folder = FilenameUtils.getFullPath(notesFilePath);
		String notesNewPath = folder + "note_new.txt";

		File newFile = new File(notesNewPath);
		File oldFile = new File(notesPath);

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(oldFile));
			bw = new BufferedWriter(new FileWriter(newFile));
			String thisLine;
			String currentKey = null;
			String codePrefix = null;
			while ((thisLine = br.readLine()) != null) {
				String newLine = thisLine;
				String imgPath = null;
				String newImgPath = null;
				boolean toNext = false;

				if (thisLine.trim().equals("")) {
					toNext = true;
				}

				if (thisLine.startsWith(Constants.MAPKEY_TITLE)) {
					currentKey = Constants.MAPKEY_TITLE;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_URL)) {
					currentKey = Constants.MAPKEY_URL;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_MENU)) {
					currentKey = Constants.MAPKEY_MENU;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_FOLDER)) {
					currentKey = Constants.MAPKEY_FOLDER;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_PARAM)) {
					currentKey = Constants.MAPKEY_PARAM;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_MAIN)) {
					currentKey = Constants.MAPKEY_MAIN;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_DETAILS)) {
					currentKey = Constants.MAPKEY_DETAILS;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_PRICE)) {
					currentKey = Constants.MAPKEY_PRICE;
					toNext = true;
				} else if (thisLine.startsWith(Constants.MAPKEY_PROPERTIES)) {
					currentKey = Constants.MAPKEY_PROPERTIES;
					toNext = true;
				}

				if (toNext) {
					bw.write(newLine);
					bw.newLine();
					continue;
				}

				// Get code prefix
				if (Constants.MAPKEY_FOLDER.equals(currentKey)) {
					codePrefix = thisLine.substring(thisLine.lastIndexOf("/") + 1);
				}

				// Main photo
				if (Constants.MAPKEY_MAIN.equals(currentKey)) {
					String[] lineAry = thisLine.split(Constants.TM_SEPERATOR);
					if (lineAry.length == 2 && new File(lineAry[1]).exists()) {
						imgPath = lineAry[1];
						newImgPath = FilenameUtils.getFullPath(imgPath) + codePrefix + "_M_"
								+ FilenameUtils.getName(imgPath);
						newLine = thisLine.replace(imgPath, newImgPath);
					}

				} else if (Constants.MAPKEY_PRICE.equals(currentKey)) {
					String[] lineAry = thisLine.split(Constants.TM_SEPERATOR);
					if (lineAry.length == 5 && new File(lineAry[4]).exists()) {
						imgPath = lineAry[4];
						newImgPath = FilenameUtils.getFullPath(imgPath) + codePrefix + "_C_"
								+ FilenameUtils.getName(imgPath);
						newLine = thisLine.replace(imgPath, newImgPath);
					}

				} else if (Constants.MAPKEY_DETAILS.equals(currentKey)) {
					String[] lineAry = thisLine.split(Constants.TM_SEPERATOR);
					if (lineAry.length == 2 && new File(lineAry[1]).exists()) {
						imgPath = lineAry[1];
						newImgPath = FilenameUtils.getFullPath(imgPath) + codePrefix + "_D_"
								+ FilenameUtils.getName(imgPath);
						newLine = thisLine.replace(imgPath, newImgPath);
					}
				}

				if (imgPath != null) {
					System.out.println(imgPath + " rename to " + newImgPath);
					// Rename
					File imgFile = new File(imgPath);
					imgFile.renameTo(new File(newImgPath));
					imgFile.delete();
					// Delete old file
				}

				bw.write(newLine);
				bw.newLine();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Remove note.txt and rename note_new.txt to note.txt

		try {
			oldFile.delete();
			newFile.renameTo(oldFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	private static void updateNotesFile(String imgOriPath, List<String> croppedImgList) {
		System.out.print("Updating note.txt...");
		// Update note.txt
		String notesPath = imgOriPath.substring(0, imgOriPath.indexOf("details")).replace("\\", "/") + "note.txt";
		String notesNewPath = imgOriPath.substring(0, imgOriPath.indexOf("details")).replace("\\", "/")
				+ "note_new.txt";

		File newFile = new File(notesNewPath);
		File oldFile = new File(notesPath);

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(oldFile));
			bw = new BufferedWriter(new FileWriter(newFile));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(imgOriPath)) {
					for (String tmp : croppedImgList) {
						bw.write(line.replaceAll(imgOriPath, tmp.replace("\\", "/")));
						bw.newLine();
					}
				} else {
					bw.write(line);
					bw.newLine();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Remove note.txt and rename note_new.txt to note.txt
		try {
			oldFile.delete();
			newFile.renameTo(oldFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	public static void resizeInWidth(String filePath, int targetWidth) {
		File file = new File(filePath);
		System.out.print("Trying to resize [" + filePath + "] to " + targetWidth + "px in width...");
		try {
			BufferedImage image = ImageIO.read(file);
			if (image.getWidth() == targetWidth) {
				System.out.println(" DO NOT NEED RESIZE!");
			} else {
				BufferedImage newImg = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
						targetWidth, 0, Scalr.OP_BRIGHTER);
				ImageIO.write(newImg, FilenameUtils.getExtension(filePath), file);
				System.out.println(" from " + image.getWidth() + "x" + image.getHeight() + " to " + newImg.getWidth()
						+ "x" + newImg.getHeight());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public static List<String> croppImg(String filePath, int maxHeight) {
		System.out.println("----------------------------------------------");
		System.out.println("Checking file:" + filePath);
		List<String> ret = new ArrayList<String>();
		try {
			File originalFile = new File(filePath);
			BufferedImage originalImgage = ImageIO.read(originalFile);

			System.out.println(
					"Original Image Dimension: " + originalImgage.getWidth() + "x" + originalImgage.getHeight());

			if (originalImgage.getHeight() <= maxHeight) {
				System.out.println("Ignore...");
			} else {
				int origiHeight = originalImgage.getHeight();
				for (int i = 0; i < (origiHeight / maxHeight) + 1; i++) {
					int cropHeight = maxHeight;
					if (cropHeight * (i + 1) > origiHeight) {
						cropHeight = origiHeight - cropHeight * i;
					}
					if (cropHeight == 0) {
						break;
					}
					BufferedImage SubImgage = originalImgage.getSubimage(0, maxHeight * i, originalImgage.getWidth(),
							cropHeight);
					System.out.print("Cropped Image Dim(" + (i + 1) + "): " + SubImgage.getWidth() + "x" + cropHeight);
					File outputfile = new File(FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath)
							+ "_" + (i + 1) + "." + FilenameUtils.getExtension(filePath));
					ImageIO.write(SubImgage, "jpg", outputfile);
					System.out.println("  Path: " + outputfile.getPath());
					ret.add(outputfile.getPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static void formatProductList(String linkSrc) {
		File oldFile = new File(linkSrc);

		BufferedReader br = null;
		BufferedWriter bw = null;

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		List<String[]> linkList = new ArrayList<String[]>();
		try {
			br = new BufferedReader(new FileReader(oldFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);
				String folder = ary[0];
				String menuPath = Utils.formatMenuName(ary[0].substring(0, ary[0].lastIndexOf("/")));
				String proId = ary[1];
				
				if ("派对服务".equals(menuPath)) {
					continue;
				}
				
				linkList.add(new String[] { folder, menuPath, proId });
				Set<String> menus;
				
				if (map.containsKey(proId)) {
					menus = map.get(proId);
				} else {
					menus = new HashSet<String>();
				}

				menus.add(menuPath);
				map.put(proId, menus);

				// bw.write(newLine);
				// bw.newLine();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String[] oAry : linkList) {
			if (map.containsKey(oAry[2])) {
				Set<String> menus = map.get(oAry[2]);
				StringBuffer menusStr = new StringBuffer();
				int k = 0;
				for (String menu : menus) {
					menu = menu.replace("派对游戏 皮纳塔Pinata", "皮纳塔 Pinata");
					if (k == 0) {
						menusStr.append(menu);
					} else {
						menusStr.append(";" + menu);
					}
					k++;
				}
				System.out.println(oAry[0].replaceAll("正品", "") + Constants.TM_SEPERATOR
						+ menusStr.toString().replaceAll("正品", "") + Constants.TM_SEPERATOR + oAry[2]);
				map.remove(oAry[2]);
			}
		}
	}

	public static void printDuplicateRows(String filePath) {
		File oldFile = new File(filePath);

		BufferedReader br = null;
		BufferedWriter bw = null;

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		List<String[]> rows = new ArrayList<String[]>();

		try {
			br = new BufferedReader(new FileReader(oldFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String folder = ary[0];
				String menuPath = ary[0].substring(0, ary[0].lastIndexOf("/"));
				String proId = ary[1].substring(ary[1].indexOf("?id=") + 4, ary[1].indexOf("&rn="));
				String newTitle = ary[2];
				String newProId = ary[3].substring(ary[3].indexOf("?id=") + 4);

				rows.add(new String[] { folder, menuPath, proId, newTitle, newProId });

				Set<String> menus;
				if (map.containsKey(proId)) {
					menus = map.get(proId);
				} else {
					menus = new HashSet<String>();
				}
				menus.add(newTitle + Constants.TM_SEPERATOR + newProId);

				map.put(proId, menus);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		for (String[] ary : rows) {
			System.out.println(ary[0] + Constants.TM_SEPERATOR + ary[1] + Constants.TM_SEPERATOR + ary[2]
					+ Constants.TM_SEPERATOR + ary[3] + Constants.TM_SEPERATOR + ary[4]);
		}

	}

	public static void matchFolderWithProID(String srcFile, String targetFile) {
		File sFile = new File(srcFile);
		File tFile = new File(targetFile);

		BufferedReader br = null;
		BufferedWriter bw = null;
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		List<String[]> rows = new ArrayList<String[]>();
		Map<String, String> fldIDMap = new HashMap<String, String>();

		try {
			br = new BufferedReader(new FileReader(sFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:2
				String folder = ary[0];
				String proId = ary[1].substring(ary[1].indexOf("?id=") + 4, ary[1].indexOf("&rn="));

				fldIDMap.put(proId, folder);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			br = new BufferedReader(new FileReader(tFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String menuPath = ary[0].substring(0, ary[0].lastIndexOf("/"));
				String proId = ary[1].substring(ary[1].indexOf("?id=") + 4, ary[1].indexOf("&rn="));
				String newTitle = ary[2];
				String newProId = ary[3].substring(ary[3].indexOf("?id=") + 4);

				// if (fldIDMap.get(proId) != null){
				// rows.add(new String[]{fldIDMap.get(proId), menuPath, proId,
				// newTitle, newProId});
				// }

				if (fldIDMap.get(proId) == null) {
					rows.add(new String[] { ary[0], menuPath, proId, newTitle, newProId });
				}

				Set<String> menus;
				if (map.containsKey(proId)) {
					menus = map.get(proId);
				} else {
					menus = new HashSet<String>();
				}
				menus.add(newTitle + Constants.TM_SEPERATOR + newProId);

				map.put(proId, menus);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		rows.sort(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub

				String[] o1Ary = (String[]) o1;
				String[] o2Ary = (String[]) o2;

				// try{
				return o1Ary[0].compareTo(o2Ary[0]);
				// }catch (Exception e){
				// return -1;
				// }

			}

		});

		for (String[] ary : rows) {
			String line = ary[0] + Constants.TM_SEPERATOR + ary[2] + Constants.TM_SEPERATOR + ary[3]
					+ Constants.TM_SEPERATOR + ary[4];
			System.out.println(line);
		}

	}

	public static void filterDuplicateForSuccessRows(String filePath) {
		File oldFile = new File(filePath);

		BufferedReader br = null;
		BufferedWriter bw = null;

		Map<String, Set<String>> menuMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> newProIDMap = new HashMap<String, Set<String>>();
		List<String[]> rows = new ArrayList<String[]>();

		try {
			br = new BufferedReader(new FileReader(oldFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String folder = ary[0];
				String menuPath = ary[0].substring(0, ary[0].lastIndexOf("/"));
				String proId = ary[1];
				String newTitle = ary[2];
				String newProId = ary[3];

				rows.add(new String[] { folder, menuPath, proId, newTitle, newProId });

				// duplicate product id
				Set<String> menus;
				if (menuMap.containsKey(proId)) {
					menus = menuMap.get(proId);
				} else {
					menus = new HashSet<String>();
				}
				menus.add(menuPath);

				menuMap.put(proId, menus);

				// duplicate new product id
				Set<String> newProIds;
				if (newProIDMap.containsKey(proId)) {
					newProIds = newProIDMap.get(proId);
				} else {
					newProIds = new HashSet<String>();
				}
				newProIds.add(newProId);

				newProIDMap.put(proId, newProIds);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Output duplicate products
		// Iterator<Entry<String, Set<String>>> items =
		// map.entrySet().iterator();
		// while(items.hasNext()){
		// Entry<String, List<String>> entry = items.next();
		// if (entry.getValue().size() > 1){
		// System.out.print(entry.getKey() + ": ");
		// for (String s : entry.getValue()){
		// System.out.print(s + Constants.TM_SEPERATOR);
		// }
		// System.out.println();
		//
		// }
		// }

		// for (String[] ary : rows){
		// String proId = ary[1].substring(ary[1].indexOf("?id=") + 4,
		// ary[1].indexOf("&rn="));
		// if (map.containsKey(proId) && map.get(proId).size() > 1){
		// System.out.println("----------------------------------------");
		// System.out.println(ary[0] + Constants.TM_SEPERATOR + ary[2] +
		// Constants.TM_SEPERATOR + ary[3]);
		//
		// int k = 0;
		// for (String s : map.get(proId)){
		// if (k++ > 0){
		// String[] sAry = s.split(Constants.TM_SEPERATOR);//length:4
		// System.out.println(ary[2] + Constants.TM_SEPERATOR +
		// sAry[3].substring(sAry[3].indexOf("?id=") + 4));
		// }
		// }
		//
		// map.remove(proId);
		// }
		// }
		// rows.sort(new Comparator(){
		//
		// @Override
		// public int compare(Object o1, Object o2) {
		// // TODO Auto-generated method stub
		// String[] o1Ary = (String[]) o1;
		// String[] o2Ary = (String[]) o2;
		// return o1Ary[0].compareTo(o2Ary[0]);
		// }
		//
		// });
		for (String[] oAry : rows) {
			if (menuMap.containsKey(oAry[2])) {
				Set<String> menus = menuMap.get(oAry[2]);
				StringBuffer menusStr = new StringBuffer();
				int k = 0;
				for (String menu : menus) {
					if (k == 0) {
						menusStr.append(menu);
					} else {
						menusStr.append(";" + menu);
					}
					k++;
				}

				Set<String> newProIds = newProIDMap.get(oAry[2]);
				StringBuffer newIdStr = new StringBuffer();
				k = 0;
				for (String newId : newProIds) {
					if (k == 0) {
						newIdStr.append(newId);
					} else {
						newIdStr.append(";" + newId);
					}
					k++;
				}
				System.out.println(
						oAry[2] + Constants.TM_SEPERATOR + oAry[3] + Constants.TM_SEPERATOR + newIdStr.toString());
				menuMap.remove(oAry[2]);
			}
		}

	}

	public static void printAllProductId() {
		File uploadFinalFile = new File(Constants.SHARE_FOLDER_PATH + "/" + Constants.UPLOAD_SUCCESS_FILENAME);
		BufferedReader br = null;
		String line;
		Map<String, String> linkMap = new HashMap<String, String>();
		Map<String, String> uploadMap = new HashMap<String, String>();

		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(uploadFinalFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String[] proIds = ary[4].split(";");
				for (String proId : proIds) {
					System.out.println(proId.split("-")[0]);
				}
			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String filterNonBreakSpace(String s) {
		return s.replace("\uFEFF", "");
	}

	/**
	 * 从 更新成功LOG取得到所有已经更新的产品，避免重复更新
	 * 
	 * @param logFile
	 * @return
	 */
	public static Set<String> getInfoFromLogs(String logFile, int targetColumn) {
		// 蜡烛/生日-卡通/K0150031###蜡烛/节日-婚礼|蜡烛/生日-卡通###45529687101###孩派天津纯手工彩绘蜡烛###559324037542
		// PATH/MENUS/ID/NEW_TITLE/NEW_ID
		Set<String> set = new HashSet<String>();

		if (new File(logFile).exists()) {
			String thisLine = null;
			BufferedReader br = null;
			try {
				// open input stream test.txt for reading purpose.
				br = new BufferedReader(new FileReader(logFile));
				while ((thisLine = br.readLine()) != null) {
					thisLine = thisLine.replace("\uFEFF", "");

					String[] itemAry = thisLine.split(Constants.TM_SEPERATOR);
					set.add(itemAry[targetColumn]);
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

		}
		return set;
	}

	public static void printDifference(String oldLinkPath, String newLinkPath) {
		// Menu
		printMenuDifferenceList(oldLinkPath, newLinkPath);

		// products
		printProductsDifferenceList(oldLinkPath, newLinkPath);
	}
	
	private static void readProdList(String listFilePath, Set<String> proIdSet, Map<String, Set<String>> proMenusMap, Map<String, String> maxSeqPerMenu, Map<String, String> newProdIdMap ){
		BufferedReader br = null;
		String line;
		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(listFilePath));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String folder = ary[0];
				String[] menuAry = ary[1].split(";");
				String id = ary[2];
				String seq = folder.substring(folder.lastIndexOf("/") + 1);
				String menuBase = folder.substring(0, folder.lastIndexOf("/"));
				
				proIdSet.add(id);
				Set<String> set;
				if (proMenusMap.containsKey(id)) {
					set = proMenusMap.get(id);
				} else {
					set = new HashSet<String>();
				}
				set.addAll(Arrays.asList(menuAry));

				proMenusMap.put(id, set);

				// 生成菜单下最大编码的MAP
				if (maxSeqPerMenu != null && (!maxSeqPerMenu.containsKey(menuBase) || maxSeqPerMenu.get(menuBase).compareTo(seq) < 0)) {
					maxSeqPerMenu.put(menuBase, seq);
				}
				if (maxSeqPerMenu != null && ary.length >= 5){
					newProdIdMap.put(id, ary[4]);	
				}
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void compareAllProds(String source, String target) {
		Set<String> oldProIds = new HashSet<String>();
		Set<String> newProIds = new HashSet<String>();
		Map<String, Set<String>> proMenuMapOld = new HashMap<String, Set<String>>();
		Map<String, Set<String>> proMenuMapNew = new HashMap<String, Set<String>>();
		Map<String, String> maxSeqPerMenu = new HashMap<String, String>();
		Map<String, String> newProdIdMap = new HashMap<String, String>();

		readProdList(source, oldProIds, proMenuMapOld, maxSeqPerMenu, newProdIdMap);
		readProdList(target, newProIds, proMenuMapNew, null, null);
		

		Set<String> oldMenusClone = new HashSet<String>(oldProIds);
		Set<String> newMenusClone = new HashSet<String>(newProIds);
		newMenusClone.removeAll(oldProIds);
		oldMenusClone.removeAll(newProIds);

		System.out.println("*********PRODUCTS NEW********");
		for (String id : newMenusClone) {
			// 生成新的序号
			List<String> sortedList = convertToSortedList(proMenuMapNew.get(id));
			String currentSeq = null;
			String menuName = null;
			for (String m : sortedList){
				if (maxSeqPerMenu.containsKey(m)){
					currentSeq = maxSeqPerMenu.get(m);
					menuName = m;
					break;
				}
			}
			
			String prefix = "";
			String seq = ""; 
			if (currentSeq == null){
				prefix =Utils.generateRandomPrefix();
				seq = "0";
			}else{
				prefix = currentSeq.substring(0, 4);
				seq = currentSeq.substring(4);
			}
			
			String newCode = prefix + Utils.formatNumber(Integer.valueOf(seq) + 1);
			// 更新最新序号
			maxSeqPerMenu.put(menuName, newCode);

			System.out.println(menuName + "/" + newCode + Constants.TM_SEPERATOR + formatMenuList(sortedList)
					+ Constants.TM_SEPERATOR + id);
		}

		System.out.println("\n*********PRODUCTS REMOVED********");
		for (String id : oldMenusClone) {
			System.out
					.println(formatMenuList(convertToSortedList(proMenuMapOld.get(id))) + Constants.TM_SEPERATOR + id + Constants.TM_SEPERATOR + newProdIdMap.get(id));
		}

		System.out.println("\n*********PRODUCTS CHANGED********");
		for (String id : oldProIds) {
			List<String> sortedOldMenusPerPro = convertToSortedList(proMenuMapOld.get(id));
			List<String> sortedNewMenusPerPro = convertToSortedList(proMenuMapNew.get(id));
			if (sortedNewMenusPerPro != null
					&& !sortedOldMenusPerPro.toString().equals(sortedNewMenusPerPro.toString())) {
				System.out.println(formatMenuList(sortedOldMenusPerPro) + " >>>> "
						+ formatMenuList(sortedNewMenusPerPro) + Constants.TM_SEPERATOR + id + Constants.TM_SEPERATOR + newProdIdMap.get(id));
			}
		}

		System.out.println();
	}

	/**
	 * 根据下载的文件对比，得出分类差异
	 * 
	 * @param oldLinkPath
	 * @param newLinkPath
	 * @return [0] 新增的菜单 [1] 删除的菜单
	 */
	private static void printMenuDifferenceList(String oldLinkPath, String newLinkPath) {
		File oldLinkFile = new File(oldLinkPath);
		File newLinkFile = new File(newLinkPath);
		Set[] ret = new Set[2];
		BufferedReader br = null;
		String line;
		Set<String> oldMenus = new HashSet<String>();
		Set<String> newMenus = new HashSet<String>();

		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(oldLinkFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				oldMenus.add(filterNonBreakSpace(ary[1]));
			}

			br.close();

			//////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(newLinkFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				newMenus.add(filterNonBreakSpace(ary[1]));
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<String> oldMenusClone = new HashSet<String>(oldMenus);
		Set<String> newMenusClone = new HashSet<String>(newMenus);
		newMenusClone.removeAll(oldMenus);
		oldMenusClone.removeAll(newMenus);

		System.out.println("*********MENU NEW********");
		for (String S : newMenusClone) {
			System.out.println(S);
		}

		System.out.println("*********MENU REMOVED********");
		for (String S : oldMenusClone) {
			System.out.println(S);
		}
		System.out.println();
	}

	/**
	 * 根据下载的文件对比，得出分类差异
	 * 
	 * @param oldLinkPath
	 * @param newLinkPath
	 * @return [0] 新增的产品 [1] 删除的产品
	 */
	private static void printProductsDifferenceList(String oldLinkPath, String newLinkPath) {
		File oldLinkFile = new File(oldLinkPath);
		File newLinkFile = new File(newLinkPath);
		Set[] ret = new Set[2];
		BufferedReader br = null;
		String line;
		Set<String> oldMenus = new HashSet<String>();
		Set<String> newMenus = new HashSet<String>();
		Map<String, Set<String>> proMenuMapOld = new HashMap<String, Set<String>>();
		Map<String, Set<String>> proMenuMapNew = new HashMap<String, Set<String>>();
		Map<String, String> maxSeqPerMenu = new HashMap<String, String>();

		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(oldLinkFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String id = ary[2];
				String seq = ary[0].substring(ary[0].lastIndexOf("/") + 1);
				String menu = ary[0].substring(0, ary[0].lastIndexOf("/"));
				oldMenus.add(id);
				Set<String> set;
				if (proMenuMapOld.containsKey(id)) {
					set = proMenuMapOld.get(id);
				} else {
					set = new HashSet<String>();
				}
				set.addAll(Arrays.asList(ary[1].split(";")));

				proMenuMapOld.put(id, set);

				// 生成菜单下最大编码的MAP
				if (!maxSeqPerMenu.containsKey(id) || maxSeqPerMenu.get(id).compareTo(seq) < 0) {
					maxSeqPerMenu.put(menu, seq);
				}
			}

			br.close();

			//////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(newLinkFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				newMenus.add(ary[2]);
				Set<String> set;
				if (proMenuMapNew.containsKey(ary[2])) {
					set = proMenuMapNew.get(ary[2]);
				} else {
					set = new HashSet<String>();
				}
				set.add(ary[1]);

				proMenuMapNew.put(ary[2], set);
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<String> oldMenusClone = new HashSet<String>(oldMenus);
		Set<String> newMenusClone = new HashSet<String>(newMenus);
		newMenusClone.removeAll(oldMenus);
		oldMenusClone.removeAll(newMenus);

		System.out.println("*********PRODUCTS NEW********");
		for (String id : newMenusClone) {
			// 生成新的序号
			List<String> sortedList = convertToSortedList(proMenuMapNew.get(id));
			String menuName = sortedList.get(0);
			String currentSeq = maxSeqPerMenu.get(menuName);
			String prefix = currentSeq.substring(0, 4);
			String seq = currentSeq.substring(4);
			String newCode = prefix + Utils.formatNumber(Integer.valueOf(seq) + 1);
			// 更新最新序号
			maxSeqPerMenu.put(menuName, newCode);

			System.out.println(menuName + "/" + newCode + Constants.TM_SEPERATOR + formatMenuList(sortedList)
					+ Constants.TM_SEPERATOR + id);
		}

		System.out.println("*********PRODUCTS REMOVED********");
		for (String id : oldMenusClone) {
			System.out
					.println(formatMenuList(convertToSortedList(proMenuMapOld.get(id))) + Constants.TM_SEPERATOR + id);
		}

		System.out.println("*********PRODUCTS CHANGED********");
		for (String id : oldMenus) {
			List<String> sortedOldMenusPerPro = convertToSortedList(proMenuMapOld.get(id));
			List<String> sortedNewMenusPerPro = convertToSortedList(proMenuMapNew.get(id));
			if (sortedNewMenusPerPro != null
					&& !sortedOldMenusPerPro.toString().equals(sortedNewMenusPerPro.toString())) {
				System.out.println(formatMenuList(sortedOldMenusPerPro) + " >>>> "
						+ formatMenuList(sortedNewMenusPerPro) + Constants.TM_SEPERATOR + id);
			}
		}

		System.out.println();
	}

	private static String formatMenuList(List<String> list) {
		StringBuffer buf = new StringBuffer();
		int k = 0;
		for (String s : list) {
			if (k++ == 0) {
				buf.append(s);
			} else {
				buf.append(";" + s);
			}
		}

		return buf.toString();
	}

	public static <T extends Comparable<? super T>> List<T> convertToSortedList(Collection<T> c) {
		if (c == null)
			return null;

		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}

	public static void printAnalysisResult() {
		File linkFile = new File("d:/Taobao/links.txt");
		File uploadFinalFile = new File("d:/Taobao/upload_success.txt");

		BufferedReader br = null;
		String line;
		Map<String, String> linkMap = new HashMap<String, String>();
		Map<String, String> uploadMap = new HashMap<String, String>();

		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(linkFile));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				linkMap.put(ary[1], line);
			}

			br.close();

			//////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(uploadFinalFile));
			List<String> result = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String oldProId = ary[2];

				if (!linkMap.containsKey(oldProId)) {
					System.out.println(line);
				}
			}

			Collections.sort(result);
			for (String s : result) {
				System.out.println(s);
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void shrinkProductLinkList(String linkFile) {
		BufferedReader br = null;
		String line;

		try {

			////////////////////////////////////////////////////////
			br = new BufferedReader(new FileReader(new File(linkFile)));
			while ((line = br.readLine()) != null) {
				String[] ary = line.split(Constants.TM_SEPERATOR);// length:4
				String foldPath = Utils.formatMenuName(ary[0]);
				String menuPath = Utils.formatMenuName(ary[0].substring(0, ary[0].lastIndexOf("/")));
				// String menuPath = Utils.formatMenuName(ary[0]);
				// String proId = ary[1].substring(ary[1].indexOf("?id=") + 4,
				// ary[1].indexOf("&rn="));
				String proId = ary[1];
				String title = "";
				if (ary.length == 3) {
					title = ary[2];
				}
				if ("派对服务".equals(menuPath)) {
					continue;
				}

				String newLine = foldPath + Constants.TM_SEPERATOR + menuPath + Constants.TM_SEPERATOR + proId
						+ Constants.TM_SEPERATOR + title;
				System.out.println(newLine);
			}

			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String formatMenuName(String menuName) {
		return filterNonBreakSpace(
				menuName.replaceAll("正品", "").replaceAll("蓝球", "篮球").replaceAll("派对游戏 皮纳塔Pinata", "皮纳塔 Pinata"));
	}
	
	/**
	 * 检查图片是否被过滤
	 * @param targetImgFile
	 * @return
	 */
	public static boolean isImageIgnored(String targetImgFile){
		try {
		    BufferedImage target = ImageIO.read(new File(targetImgFile));
			for(String img : Constants.IMAGE_EXCLUDE_LIST){
			    BufferedImage src = ImageIO.read(new File(Constants.SHARE_FOLDER_PATH + "/" + img));
				if (Utils.compareImages(src, target)){
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return false;
	}

	/**
	 * Compares two images pixel by pixel.
	 *
	 * @param imgA
	 *            the first image.
	 * @param imgB
	 *            the second image.
	 * @return whether the images are both the same or not.
	 */
	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
			int width = imgA.getWidth();
			int height = imgA.getHeight();

			// Loop over every pixel.
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// Compare the pixels for equality.
					if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
						return false;
					}
				}
			}
		} else {
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws IOException {
		// String s = "孩派儿童生日聚会漫威钢铁侠复仇者联盟主题派对装饰布置用品";
		// System.out.println(s.length());
		// System.out.println(Utils.formatTitleDesc(s));
		// System.out.println(Utils.generateRandomPrefix() + "" +
		// Utils.formatNumber(90));

		// String s = "D:\\Taobao\\万圣节专区";
		// Utils.startRename(s);
		// Utils.startCutPics(s);
		// s = "D:\\Taobao\\蜡烛";
		// Utils.startRename(s);
		// Utils.startCutPics(s);
		// s = "D:\\Taobao\\迪士尼";
		// Utils.startRename(s);
		// Utils.startCutPics(s);

		// String p = "D:/products/迪士尼正品/超能陆战队/6/details/1.jpg";
		// System.out.println(FilenameUtils.getFullPath(p));

		// Utils.readNotesTitles(new File("D:\\products\\迪士尼正品").listFiles());
		// Utils.readNotesTitles(new File("D:\\Taobao\\派对元素").listFiles());

		// Utils.filterDuplicate("D:/Taobao/Tools/links.txt");
		// Utils.filterDuplicateForSuccessRows("D:/Taobao/Tools/upload_success.txt");
		// Utils.printDuplicateRows("D:/Taobao/Tools/upload_success_all.txt");
		// Utils.matchFolderWithProID("D:/Taobao/Tools/links_all.txt",
		// "D:/Taobao/Tools/upload_success_all.txt");
		// Utils.filterDuplicateForSuccessRows("D:/Taobao/Tools/upload_success_all_ordered.txt");
		// Utils.printAnalysisResult();

		// https://upload.taobao.com/auction/container/publish.htm?catId=50003854&itemId=559668807460
		// 从URL中获取CATEGORY ID
		// String url =
		// "https://upload.taobao.com/auction/container/publish.htm?catId=50003854&itemId=559668807460";
		// String catId = url.substring(url.indexOf("?catId=") + 7,
		// url.indexOf("&itemId="));
		// System.out.println(catId);
		// Utils.printAnalysisResult();
//		 Utils.shrinkProductLinkList("d:/Taobao/links_download_Nov_5.txt");
//		Utils.printDifference("d:/Taobao/tm_products_under_menu_base.txt", "d:/Taobao/tm_products_under_menu.txt");
		
//		 Utils.formatProductList("d:/Taobao/links_download_Nov_5.txt");
		 Utils.compareAllProds(Constants.SHARE_FOLDER_PATH + "/" + Constants.UPLOAD_SUCCESS_FILENAME, "d:/Taobao/links.txt");
	}
}
