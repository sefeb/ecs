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

package io.github.sefeb.ecs.excel.parse.validator;


import io.github.sefeb.ecs.excel.parse.CellInfo;
import org.apache.commons.lang3.StringUtils;

public class VNotBlank implements IValidator<String> {

    public static final String EC_NB_BLANK = "EC_NB_BLANK";

    @Override
    public ValidateResult validate(CellInfo cell, String text) {
        return (StringUtils.isBlank(text)) ? new ValidateResult(false, cell, "EC_NB_BLANK", getErrorMessage(EC_NB_BLANK)) : new ValidateResult(true, cell, null, null);
    }

    @Override
    public String getErrorMessage(String errorCode) {
        String msg = null;
        switch(errorCode){
            case EC_NB_BLANK -> {
                msg = "value is blank.";
            }
            default -> {
                // do nothing
            }
        }
        return msg;
    }

}

