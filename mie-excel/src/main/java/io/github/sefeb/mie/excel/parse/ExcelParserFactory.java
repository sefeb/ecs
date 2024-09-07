package io.github.sefeb.mie.excel.parse;

public class ExcelParserFactory {

    public static IExcelParser streamParser(){
        return new ExcelStreamParser();
    }

    public static IExcelParser defaultParser(){
        return new ExcelStreamParser();
    }

}
