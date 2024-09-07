package io.github.sefeb.mie.excel.parse.validator;

import io.github.sefeb.mie.excel.parse.CellInfo;
import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class VEnum extends AbstractConfigurableInterceptor<VEnumConfig> implements IValidator<String> {
	public static final String EC_ENUM_BLANK = "EC_ENUM_BLANK";
	public static final String EC_ENUM_OPT_NOT_FOUND = "EC_ENUM_OPT_NOT_FOUND";
	public static final String EC_ENUM_OPT_FOUND = "EC_ENUM_OPT_FOUND";

	public VEnum(VEnumConfig config) {
		super(config);
	}

	@Override
	public ValidateResult validate(CellInfo cell, String value) {
		if(StringUtils.isBlank(value))
            return new ValidateResult(false, cell, EC_ENUM_BLANK, getErrorMessage(EC_ENUM_BLANK));

		Set<String> inc = getConfig().include();
		if(null != inc && !inc.isEmpty() && !inc.contains(value)){
			return new ValidateResult(false, cell, EC_ENUM_OPT_NOT_FOUND, getErrorMessage(EC_ENUM_OPT_NOT_FOUND));
		}

		Set<String> exc = getConfig().exclude();
		if(null != exc && !exc.isEmpty() && exc.contains(value)){
			return new ValidateResult(false, cell, EC_ENUM_OPT_FOUND, getErrorMessage(EC_ENUM_OPT_FOUND));
		}

		return new ValidateResult(true, cell, null, null);
	}

	@Override
	public String getErrorMessage(String errorCode) {
		String msg = null;
		switch(errorCode){
			case EC_ENUM_BLANK -> {
				msg = "value is blank.";
			}
			case EC_ENUM_OPT_NOT_FOUND -> {   // cell
				msg = "value not found in options.";
			}
			case EC_ENUM_OPT_FOUND -> {   // cell
				msg = "value found in options.";
			}
			default -> {
				// do nothing
			}
		}
		return msg;
	}

}
