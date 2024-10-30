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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ExcelParserUtils {

    public static void fireObserverEvent(List<IExcelParserObserver> observers, Consumer<IExcelParserObserver> observer){
        Optional.ofNullable(observers).ifPresent(os -> os.forEach(observer));
    }

    public static String getCellColumnId(String cellId){
        if(StringUtils.isBlank(cellId))
            return null;
        int idx = 0;
        while(idx < cellId.length() && Character.isLetter(cellId.charAt(idx))){
            idx++;
        }
        return cellId.substring(0, idx);
    }

    public static Map<String, CellInfo> getRowHeaderCache(RowInfo row){
        Map<String, CellInfo> cache = new HashMap<>();
        for (CellInfo cell : row.getData().values()) {
            cache.put(cell.getHeader(), cell);
        }
        return cache;
    }

}
