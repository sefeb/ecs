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

import java.util.Set;

public class VEnum extends AbstractConfigurableInterceptor<VEnum.VEnumConfig> implements IValidator<String> {
	public static final String EC_ENUM_BLANK = "EC_ENUM_BLANK";
	public static final String EC_ENUM_OPT_NOT_FOUND = "EC_ENUM_OPT_NOT_FOUND";
	public static final String EC_ENUM_OPT_FOUND = "EC_ENUM_OPT_FOUND";

	@Getter
	@Builder
	public static class VEnumConfig{
		private Set<String> include;
		private Set<String> exclude;
	}

	public VEnum(VEnumConfig config) {
		super(config);
	}

	@Override
	public ValidateResult validate(CellInfo cell, String value) {
		if(StringUtils.isBlank(value))
            return new ValidateResult(false, cell, EC_ENUM_BLANK, getErrorMessage(EC_ENUM_BLANK));

		Set<String> inc = getConfig().getInclude();
		if(null != inc && !inc.isEmpty() && !inc.contains(value)){
			return new ValidateResult(false, cell, EC_ENUM_OPT_NOT_FOUND, getErrorMessage(EC_ENUM_OPT_NOT_FOUND));
		}

		Set<String> exc = getConfig().getExclude();
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
