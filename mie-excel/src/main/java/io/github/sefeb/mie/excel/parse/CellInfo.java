package io.github.sefeb.mie.excel.parse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CellInfo {

    String rowIndex;    // row index
    String colIndex;    // col index
    String id;          // cell id
    String header;      // cell header
    String value;       // cell value


}
