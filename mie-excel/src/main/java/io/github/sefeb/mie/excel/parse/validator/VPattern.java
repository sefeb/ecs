package io.github.sefeb.mie.excel.parse.validator;

import io.github.sefeb.mie.excel.parse.CellInfo;
import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPattern extends AbstractConfigurableInterceptor<VPatternConfig> implements IValidator<String> {
    public static final String EC_PATTERN_BLANK = "EC_PATTERN_BLANK";
    public static final String EC_PATTERN_NOT_MATCH = "EC_PATTERN_NOT_MATCH";
    public static final String EC_PATTERN_MATCH = "EC_PATTERN_MATCH";

    public VPattern(VPatternConfig config) {
        super(config);
    }

    @Override
    public ValidateResult validate(CellInfo cell, String value) {
        if(StringUtils.isBlank(value))
            return new ValidateResult(false, cell, EC_PATTERN_BLANK, getErrorMessage(EC_PATTERN_BLANK));
        Matcher matcher = Pattern.compile(getConfig().pattern()).matcher(value);
        boolean exactMatch = Objects.requireNonNullElse(getConfig().exactMatch(), Boolean.FALSE);
        boolean result = exactMatch ? matcher.matches() : matcher.find();
        boolean isNegative = Objects.requireNonNullElse(getConfig().not(), Boolean.FALSE);
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
