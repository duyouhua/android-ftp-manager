package com.test.ftpclient.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import org.textmining.text.extraction.WordExtractor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class ParseUtils {
	/**
	 * 解析DOC
	 * 
	 * @param path
	 * @return
	 */
	public static String readDOC(String path) {
		// 创建输入流读取doc文件
		FileInputStream in;
		String text = null;
		// Environment.getExternalStorageDirectory().getAbsolutePath()+
		// "/aa.doc")
		try {
			in = new FileInputStream(new File(path));
			int a = in.available();
			WordExtractor extractor = null;
			// 创建WordExtractor
			extractor = new WordExtractor();
			// 对doc文件进行提取
			text = extractor.extractText(in);
			System.out.println("解析得到的东西" + text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;
	}

	/**
	 * 解析xls
	 * 
	 * @param path
	 * @return
	 */
	public static String readXLS(String path) {
		String str = "";
		try {
			Workbook workbook = null;
			workbook = Workbook.getWorkbook(new File(path));
			Sheet sheet = workbook.getSheet(0);
			Cell cell = null;
			int columnCount = sheet.getColumns();
			int rowCount = sheet.getRows();
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					cell = sheet.getCell(j, i);
					String temp2 = "";
					if (cell.getType() == CellType.NUMBER) {
						temp2 = ((NumberCell) cell).getValue() + "";
					} else if (cell.getType() == CellType.DATE) {
						temp2 = "" + ((DateCell) cell).getDate();
					} else {
						temp2 = "" + cell.getContents();
					}
					str = str + "  " + temp2;
				}
				str += "\n";
			}
			workbook.close();
		} catch (Exception e) {
		}
		return str;

	}

	/**
	 * 解析docx
	 * 
	 * @param path
	 * @return
	 */
	public static String readDOCX(String path) {
		String river = "";
		try {
			ZipFile xlsxFile = new ZipFile(new File(path));
			ZipEntry sharedStringXML = xlsxFile.getEntry("word/document.xml");
			InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
			XmlPullParser xmlParser = Xml.newPullParser();
			xmlParser.setInput(inputStream, "utf-8");
			int evtType = xmlParser.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				switch (evtType) {
				case XmlPullParser.START_TAG:
					String tag = xmlParser.getName();
					System.out.println(tag);
					if (tag.equalsIgnoreCase("t")) {
						river += xmlParser.nextText() + "\n";
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				evtType = xmlParser.next();
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return river;
	}

	/**
	 * 解析xlsx
	 * 
	 * @param path
	 * @return
	 */

	public static String readXLSX(String path) {
		String str = "";
		String v = null;
		boolean flat = false;
		List<String> ls = new ArrayList<String>();
		try {
			ZipFile xlsxFile = new ZipFile(new File(path));
			ZipEntry sharedStringXML = xlsxFile
					.getEntry("xl/sharedStrings.xml");
			InputStream inputStream = xlsxFile.getInputStream(sharedStringXML);
			XmlPullParser xmlParser = Xml.newPullParser();
			xmlParser.setInput(inputStream, "utf-8");
			int evtType = xmlParser.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				switch (evtType) {
				case XmlPullParser.START_TAG:
					String tag = xmlParser.getName();
					if (tag.equalsIgnoreCase("t")) {
						ls.add(xmlParser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				evtType = xmlParser.next();
			}
			ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/sheet1.xml");
			InputStream inputStreamsheet = xlsxFile.getInputStream(sheetXML);
			XmlPullParser xmlParsersheet = Xml.newPullParser();
			xmlParsersheet.setInput(inputStreamsheet, "utf-8");
			int evtTypesheet = xmlParsersheet.getEventType();
			while (evtTypesheet != XmlPullParser.END_DOCUMENT) {
				switch (evtTypesheet) {
				case XmlPullParser.START_TAG:
					String tag = xmlParsersheet.getName();
					if (tag.equalsIgnoreCase("row")) {
					} else if (tag.equalsIgnoreCase("c")) {
						String t = xmlParsersheet.getAttributeValue(null, "t");
						if (t != null) {
							flat = true;
							System.out.println(flat + "有");
						} else {
							System.out.println(flat + "没有");
							flat = false;
						}
					} else if (tag.equalsIgnoreCase("v")) {
						v = xmlParsersheet.nextText();
						if (v != null) {
							if (flat) {
								str += ls.get(Integer.parseInt(v)) + "  ";
							} else {
								str += v + "  ";
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (xmlParsersheet.getName().equalsIgnoreCase("row")
							&& v != null) {
						str += "\n";
					}
					break;
				}
				evtTypesheet = xmlParsersheet.next();
			}
			System.out.println(str);
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return str;
	}
}
