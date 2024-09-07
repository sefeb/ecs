package io.github.sefeb.mie.excel.parse.processor;

import io.github.sefeb.mie.excel.parse.AbstractConfigurableInterceptor;

import java.util.Objects;

public class PBool extends AbstractConfigurableInterceptor<PBoolConfig> implements IProcessor<String, Boolean> {

	public PBool(PBoolConfig config) {
		super(config);
	}

	@Override
    public Boolean process(String text) {
		if(getConfig().symbols().isEmpty())
			return !getConfig().value();
		String value = Objects.requireNonNullElse(text, "").trim();
		return (getConfig().symbols().contains(value) == getConfig().value());
    }
}
