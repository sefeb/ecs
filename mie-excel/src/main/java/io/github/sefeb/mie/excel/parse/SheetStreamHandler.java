package io.github.sefeb.mie.excel.parse;

import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.Map;

public class SheetStreamHandler extends DefaultHandler {

    private static final Logger log = LoggerFactory.getLogger(SheetStreamHandler.class);

    private record CellAttr(
        String index,
        String type,
        String style
    ){}

    private record RowAttr(
        String index
    ){}

    private final StylesTable stylesTable;
    private final ReadOnlySharedStringsTable sharedStringsTable;
    private final List<IExcelParserObserver> observers;

    private boolean isShareString;
    private String cellContent;
    private RowAttr currentRow;
    private CellAttr currentCell;
    private RowInfo currentRowData;
    private Map<String, String> currentHeaders; // key: cell id; value: cell value


    public SheetStreamHandler(StylesTable styles, ReadOnlySharedStringsTable strings) {
        this(styles, strings, null);
    }

    public SheetStreamHandler(StylesTable styles, ReadOnlySharedStringsTable strings, List<IExcelParserObserver> observers) {
        this.stylesTable = styles;
        this.sharedStringsTable = strings;
        this.observers = observers;
    }

    private void logAttributes(Attributes attributes){
        int len = attributes.getLength();
        for(int i = 0; i < len; i++){
            log.info(
                "attributes: localName={}, qname={}, type={}, uri={}, value={}",
                attributes.getLocalName(i),
                attributes.getQName(i),
                attributes.getType(i),
                attributes.getURI(i),
                attributes.getValue(i)
            );
        }
    }

    private boolean isHeader(RowAttr rowAttr){
        return Integer.parseInt(rowAttr.index()) == 1;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        switch(name){
            case "row" -> { // row
                this.currentRow = new RowAttr(attributes.getValue("r"));    // initialize current row info
                int rowIndex = Integer.parseInt(this.currentRow.index());
//                this.currentRowData = new RowInfo(rowIndex, Maps.newHashMap());  // initialize current row data
                this.currentRowData = RowInfo.builder().index(rowIndex).data(Maps.newHashMap()).build();
                // trigger observer event
                if(isHeader(this.currentRow)){  // header
                    this.currentHeaders = Maps.newHashMap();
                    ExcelParserUtils.fireObserverEvent(this.observers, IExcelParserObserver::headerStart);
                } else {    // data
                    ExcelParserUtils.fireObserverEvent(this.observers, o -> o.rowStart(this.currentRow.index()));
                }
                // clear cache
                this.currentCell = null;
                this.cellContent = "";
            }
            case "c" -> {   // cell
//                logAttributes(attributes);
                this.currentCell = new CellAttr(attributes.getValue("r"), attributes.getValue("t"), attributes.getValue("s"));
                cellContent = "";   // clear cache
                if("s".equals(this.currentCell.type())) {
                    this.isShareString = true;
                }
                // trigger cellStart
                if(!isHeader(this.currentRow)) {
                    ExcelParserUtils.fireObserverEvent(this.observers, o -> o.cellStart(this.currentRow.index(), this.currentCell.index()));
                }
            }
            case "f" -> {
                log.error("found formula cell, should throw Exception: localName={}, name={}", localName, name);
                throw new ExcelParseException("stream reader not support formula");
            }
            default -> {
                // do nothing
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if(name.equals("sheet")){
            ExcelParserUtils.fireObserverEvent(this.observers, o -> o.sheetEnd(1, "1"));
        } else if(name.equals("row")){
            // trigger observer event
            if(isHeader(this.currentRow)) {
                Map<String, CellInfo> h = Maps.newHashMap();
                this.currentRowData.getData().values().forEach(v -> {
                    this.currentHeaders.put(v.getColIndex(), v.getValue());
                    h.put(v.getValue(), v);
                });
                ExcelParserUtils.fireObserverEvent(this.observers, o -> o.headerEnd(h));
            } else {
                ExcelParserUtils.fireObserverEvent(this.observers, o -> o.rowEnd(this.currentRowData));
            }
            // clear cache
            this.currentRow = null;
        }else if(name.equals("c")){
            if (this.isShareString) {   // use shared string table
                int index = Integer.parseInt(cellContent);
                cellContent = new XSSFRichTextString(sharedStringsTable.getItemAt(index).getString()).toString();
            }else if("n".equals(this.currentCell.type())){  // date and number
                int styleIndex = Integer.parseInt(this.currentCell.style());
                XSSFCellStyle style = this.stylesTable.getStyleAt(styleIndex);
                int formatIndex = style.getDataFormat();
                String formatString = style.getDataFormatString();
                if (null == formatString) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                cellContent = new DataFormatter().formatRawCellContents(Double.parseDouble(cellContent), formatIndex, formatString);
            }
            // add cell data to row
            String rowId = this.currentRow.index();
            String cellId = this.currentCell.index();
            String colId = ExcelParserUtils.getCellColumnId(cellId);
            String header = this.currentHeaders.get(colId);
            CellInfo ci = new CellInfo(rowId, colId, cellId, header, cellContent);
            this.currentRowData.getData().put(ci.getColIndex(), ci);
            // trigger observer event
            if(!isHeader(this.currentRow)) {
                ExcelParserUtils.fireObserverEvent(this.observers, o -> o.cellEnd(ci));
            }
            // clear cache
            this.isShareString = false;
            this.currentCell = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        cellContent += new String(ch, start, length);
    }

}
