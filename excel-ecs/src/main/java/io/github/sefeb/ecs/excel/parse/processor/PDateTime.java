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

import io.github.sefeb.ecs.excel.parse.AbstractConfigurableInterceptor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class PDateTime extends AbstractConfigurableInterceptor<PDateTime.PDateTimeConfig> implements IProcessor<String, ZonedDateTime>{

	@Getter
	@Builder
	public static class PDateTimeConfig{
		private Set<String> formats;	// format collection
		private String timeZone;	// time zone
	}

	public PDateTime(PDateTimeConfig config) {
		super(config);
	}

	@Override
    public ZonedDateTime process(String value) {
		if(StringUtils.isBlank(value) || getConfig().getFormats().isEmpty())
			return null;

		for(String fmt: getConfig().getFormats()){
			try {
				LocalDateTime ldt = LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern(fmt.trim()));
				return StringUtils.isBlank(getConfig().getTimeZone())
					? ZonedDateTime.of(ldt, ZoneId.systemDefault())
					: ZonedDateTime.of(ldt, ZoneId.of(getConfig().getTimeZone().trim()));
			} catch (DateTimeParseException e) {
				throw new ProcessorException("convert to datetime failed", e);
			}
		}
		throw new ProcessorException("no format matched the value");
    }
    
}
