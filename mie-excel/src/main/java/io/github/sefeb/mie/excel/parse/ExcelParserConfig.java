package io.github.sefeb.mie.excel.parse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExcelParserConfig {

    public static final int MAX_ROW_AMOUNT = 0;   // max row amount to read. zero means unlimited.

    int maxRowAmount = MAX_ROW_AMOUNT;

}
