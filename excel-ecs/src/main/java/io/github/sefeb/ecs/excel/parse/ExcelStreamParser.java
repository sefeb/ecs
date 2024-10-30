/*
 * Copyright [2024] [Hao Yong]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.sefeb.ecs.excel.parse;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelStreamParser implements IExcelParser {

    private final List<IExcelParserObserver> observers =new ArrayList<>();

    public ExcelStreamParser(){
        //
    }

    @Override
    public void readAllSheets(File file) {
        try {
            readSheets(file, null);
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            throw new ExcelParseException("excel read error", e);
        }
    }

    @Override
    public void readSheet(File file, String sheetName) {
        try {
            readSheets(file, List.of(sheetName));
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            throw new ExcelParseException("excel read error", e);
        }
    }

    private void readSheets(File file, List<String> sheetNames) throws OpenXML4JException, IOException, SAXException, ParserConfigurationException {
        OPCPackage pkg = OPCPackage.open(file);
        XSSFReader r = new XSSFReader( pkg );
        StylesTable styles = r.getStylesTable();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);

        XMLReader sheetParser = fetchSheetParser(styles, strings);
        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator)r.getSheetsData();
        int sheetIndex = 0;
        while(sheets.hasNext()) {
            InputStream sheet = sheets.next();
            String sheetName = sheets.getSheetName();
            final int si = sheetIndex++;
            if(null == sheetNames || sheetNames.contains(sheetName)){
                ExcelParserUtils.fireObserverEvent(this.observers, o -> o.sheetStart(si, sheetName));
                InputSource sheetSource = new InputSource(sheet);
                sheetParser.parse(sheetSource);
                ExcelParserUtils.fireObserverEvent(this.observers, o -> o.sheetEnd(si, sheetName));
            }
            sheet.close();
        }
    }

    private XMLReader fetchSheetParser(StylesTable styles, ReadOnlySharedStringsTable strings) throws SAXException, ParserConfigurationException {
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetStreamHandler(styles, strings, observers);
        parser.setContentHandler(handler);
        return parser;
    }

    @Override
    public IExcelParser registObserver(IExcelParserObserver observer) {
        observers.add(Objects.requireNonNull(observer));
        return this;
    }

    @Override
    public IExcelParserObserver[] getObservers() {
        return this.observers.toArray(new IExcelParserObserver[0]);
    }

}
