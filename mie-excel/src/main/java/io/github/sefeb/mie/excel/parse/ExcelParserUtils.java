package io.github.sefeb.mie.excel.parse;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ExcelParserUtils {

    public static void fireObserverEvent(List<IExcelParserObserver> observers, Consumer<IExcelParserObserver> observer){
        Optional.ofNullable(observers).ifPresent(os -> os.forEach(observer));
    }

    public static String getCellColumnId(String cellId){
        if(Strings.isNullOrEmpty(cellId))
            return null;
        int idx = 0;
        while(idx < cellId.length() && Character.isLetter(cellId.charAt(idx))){
            idx++;
        }
        return cellId.substring(0, idx);
    }

    public static Map<String, CellInfo> getRowHeaderCache(RowInfo row){
        Map<String, CellInfo> cache = Maps.newHashMap();
        for (CellInfo cell : row.getData().values()) {
            cache.put(cell.getHeader(), cell);
        }
        return cache;
    }

}
