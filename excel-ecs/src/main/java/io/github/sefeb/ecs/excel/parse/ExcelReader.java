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

import io.github.sefeb.ecs.excel.parse.processor.ProcessorException;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class ExcelReader {

    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);

    private IExcelParser parser;

    private Map<String, List<IInterceptor>> interceptors;

    private static class RowSerializerObserver<T> implements IExcelParserObserver{

        RowSerializer<T> serializer;
        Class<T> obClazz;
        Map<String, List<IInterceptor>> obInterceptors;
        List<T> obResult;
        IExcelParserObserver[] obObservers;

        public RowSerializerObserver(Class<T> clazz, Map<String, List<IInterceptor>> interceptors, IExcelParserObserver[] observers, List<T> result){
            this.obClazz = clazz;
            this.obInterceptors = interceptors;
            this.obResult = result;
            this.obObservers = observers;
        }

        @Override
        public void headerEnd(Map<String, CellInfo> header) {
            this.serializer = new RowSerializer<>(this.obClazz, header, this.obInterceptors, this.obObservers);
        }

        @Override
        public void rowEnd(RowInfo row) {
            this.obResult.add(this.serializer.serialize(row));
        }
    }

    private ExcelReader(){}

    public static ExcelReader build(){
        return new ExcelReader();
    }

    public ExcelReader parser(IExcelParser parser){
        if(null != this.parser){
            throw new ExcelParseException("only one parser can be registed!");
        }
        this.parser = Objects.requireNonNullElse(parser, ExcelParserFactory.defaultParser());
        return this;
    }

    public ExcelReader observer(IExcelParserObserver observer){
        if(null == this.parser){
            this.parser = Objects.requireNonNullElse(parser, ExcelParserFactory.defaultParser());
        }
        IExcelParser cp = Objects.requireNonNull(this.parser, "can not regist observer because parser is null");
        cp.registObserver(Objects.requireNonNull(observer));
        return this;
    }

    public ExcelReader interceptor(String header, List<IInterceptor> interceptors){
        if(null == this.interceptors) {
            this.interceptors = new HashMap<>();
        }
        if(Strings.isBlank(header)) {
            throw new ExcelParseException("can not regist interceptor to blank header!");
        }
        if(!this.interceptors.containsKey(header)) {
            this.interceptors.put(header, new ArrayList<>());
        }
        this.interceptors.get(header).addAll(interceptors);

        return this;
    }

    public <T> List<T> readObjects(File file, Class<T> clazz){
        List<T> result = new ArrayList<>();
        IExcelParserObserver obs = new RowSerializerObserver<T>(clazz, this.interceptors, this.parser.getObservers(), result);
        observer(obs);
        try {
            this.parser.readAllSheets(file);
        }catch(ProcessorException e){
            throw e;
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
        return result;
    }


//    public void processAllSheets(String filename) throws Exception {
//        OPCPackage pkg = OPCPackage.open(filename);
//        XSSFReader r = new XSSFReader( pkg );
//        StylesTable styles = r.getStylesTable();
//        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
//
//        XMLReader parser = fetchSheetParser(styles, strings);
//        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator)r.getSheetsData();
//        while(sheets.hasNext()) {
//            InputStream sheet = sheets.next();
//            String sheetName = sheets.getSheetName();
//            System.out.println("start sheet: " + sheetName);
//            InputSource sheetSource = new InputSource(sheet);
//            parser.parse(sheetSource);
//            sheet.close();
//        }
//    }
//    public XMLReader fetchSheetParser(StylesTable styles, ReadOnlySharedStringsTable strings) throws SAXException, ParserConfigurationException {
//        XMLReader parser = XMLHelper.newXMLReader();
//        List<IExcelParserObserver> observers = List.of(new IExcelParserObserver(){
//
//            @Override
//            public void headerStart() {
//                log.info("<<<<<<<<<< header >>>>>>>>>>");
//            }
//
//            @Override
//            public void headerEnd(Map<String, CellInfo> headers) {
//                log.info("{}", new Gson().toJson(headers));
//                log.info(">>>>>>>>>> header <<<<<<<<<<");
//            }
//
//            @Override
//            public void rowStart(String index) {
//                log.info("<<<<<<<<<< row {} >>>>>>>>>>", index);
//            }
//
//            @Override
//            public void cellStart(String rowIndex, String id) {
//                log.info("<<<<<<<<<< cell {} >>>>>>>>>>", id);
//            }
//
//            @Override
//            public void cellEnd(CellInfo cell) {
////                log.info("cell value: {}", new Gson().toJson(cell));
//                log.info(">>>>>>>>>> cell <<<<<<<<<<");
//            }
//
//            @Override
//            public void rowEnd(RowInfo row) {
//                log.info("row data: {}", new Gson().toJson(row.getData()));
//                log.info(">>>>>>>>>> row <<<<<<<<<<");
//            }
//
//        });
//        ContentHandler handler = new SheetStreamHandler(styles, strings, observers);
//        parser.setContentHandler(handler);
//        return parser;
//    }

}
