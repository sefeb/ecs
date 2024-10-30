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

package io.github.sefeb.ecs.excel.parse.processor;

import io.github.sefeb.ecs.excel.parse.CellInfo;

public class ProcessorException extends RuntimeException{

    private CellInfo cell;

    public ProcessorException(CellInfo cell, String message) {
        super(message);
        this.cell = cell;
    }

    public ProcessorException(String message) {
        this(null, message);
    }

    public ProcessorException(CellInfo cell, String message, Throwable cause) {
        super(message, cause);
        this.cell = cell;
    }

    public ProcessorException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public CellInfo getCell() {
        return this.cell;
    }

}
