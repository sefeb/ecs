package io.github.sefeb.mie.excel.parse;

import io.github.sefeb.mie.excel.parse.validator.ValidateResult;

import java.util.Map;

public interface IExcelParserObserver {

    default void start(){}

    default void sheetStart(int index, String sheetName){}

    default void headerStart(){}

    default void headerEnd(Map<String, CellInfo> header){}

    default void rowStart(String index){}

    default void cellStart(String rowIndex, String id){}

    default void cellEnd(CellInfo cell){}

    default void rowEnd(RowInfo row){}

    default void validateError(ValidateResult error){}

    default void sheetEnd(int index, String sheetName){}

    default void end(){}

}
