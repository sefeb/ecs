package io.github.sefeb.mie.excel.parse.processor;

import java.util.Locale;

public class PUpperCase implements IProcessor<String, String> {
    @Override
    public String process(String value) {
        if(null != value)
            return value.toUpperCase(Locale.ROOT);
        return null;
    }
}
