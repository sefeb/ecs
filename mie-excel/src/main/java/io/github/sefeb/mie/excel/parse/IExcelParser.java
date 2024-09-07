package io.github.sefeb.mie.excel.parse;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public interface IExcelParser {

    void readAllSheets(File file) throws OpenXML4JException, IOException, SAXException, ParserConfigurationException;

    void readSheet(File file, String sheetName);

    IExcelParser registObserver(IExcelParserObserver observer);

    IExcelParserObserver[] getObservers();

}
