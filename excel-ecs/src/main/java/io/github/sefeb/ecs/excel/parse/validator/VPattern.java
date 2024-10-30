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
import io.github.sefeb.ecs.excel.parse.AbstractConfigurableInterceptor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPattern extends AbstractConfigurableInterceptor<VPattern.VPatternConfig> implements IValidator<String> {
    public static final String EC_PATTERN_BLANK = "EC_PATTERN_BLANK";
    public static final String EC_PATTERN_NOT_MATCH = "EC_PATTERN_NOT_MATCH";
    public static final String EC_PATTERN_MATCH = "EC_PATTERN_MATCH";

    @Getter
    @Builder
    public static class VPatternConfig{
        private String pattern;
        private boolean exactMatch = false;
        private boolean not = false;
    }

    public VPattern(VPatternConfig config) {
        super(config);
    }

    @Override
    public ValidateResult validate(CellInfo cell, String value) {
        if(StringUtils.isBlank(value))
            return new ValidateResult(false, cell, EC_PATTERN_BLANK, getErrorMessage(EC_PATTERN_BLANK));
        Matcher matcher = Pattern.compile(getConfig().getPattern()).matcher(value);
        boolean result = getConfig().isExactMatch() ? matcher.matches() : matcher.find();
        boolean isNegative = getConfig().isNot();
        if(result && isNegative)
            return new ValidateResult(true, cell, EC_PATTERN_MATCH, getErrorMessage(EC_PATTERN_MATCH));
        else if(!result && !isNegative)
            return new ValidateResult(true, cell, EC_PATTERN_NOT_MATCH, getErrorMessage(EC_PATTERN_NOT_MATCH));
        return new ValidateResult(true, cell, null, null);
    }

    @Override
    public String getErrorMessage(String errorCode) {
        String msg = null;
        switch(errorCode){
            case EC_PATTERN_BLANK -> {
                msg = "value is blank.";
            }
            case EC_PATTERN_NOT_MATCH -> {
                msg = "value not matched the pattern.";
            }
            case EC_PATTERN_MATCH -> {
                msg = "value matched the pattern.";
            }
            default -> {
                // do nothing
            }
        }
        return msg;
    }

}
