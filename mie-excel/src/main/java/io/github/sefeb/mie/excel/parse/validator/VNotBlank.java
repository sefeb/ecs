package io.github.sefeb.mie.excel.parse.validator;


import io.github.sefeb.mie.excel.parse.CellInfo;
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

