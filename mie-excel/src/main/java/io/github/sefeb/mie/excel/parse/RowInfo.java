package io.github.sefeb.mie.excel.parse;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class RowInfo {

    int index;  // row index

    Map<String, CellInfo> data; // row data

}
