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

import io.github.sefeb.ecs.excel.parse.validator.ValidateResult;

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
