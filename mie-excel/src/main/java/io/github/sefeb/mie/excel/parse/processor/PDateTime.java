package io.github.sefeb.mie.excel.parse.processor;

import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PDateTime extends AbstractConfigurableInterceptor<PDateTimeConfig> implements IProcessor<String, ZonedDateTime>{

	public PDateTime(PDateTimeConfig config) {
		super(config);
	}

	@Override
    public ZonedDateTime process(String value) {
		if(StringUtils.isBlank(value) || getConfig().formats().isEmpty())
			return null;

		for(String fmt: getConfig().formats()){
			try {
				LocalDateTime ldt = LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern(fmt.trim()));
				return StringUtils.isBlank(getConfig().timeZone())
					? ZonedDateTime.of(ldt, ZoneId.systemDefault())
					: ZonedDateTime.of(ldt, ZoneId.of(getConfig().timeZone().trim()));
			} catch (DateTimeParseException e) {
				throw new ProcessorException("convert to datetime failed", e);
			}
		}
		throw new ProcessorException("no format matched the value");
    }
    
}
