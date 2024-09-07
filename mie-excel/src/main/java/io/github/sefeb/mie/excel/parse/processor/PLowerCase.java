package io.github.sefeb.mie.excel.parse.processor;

import java.util.Locale;

public class PLowerCase implements IProcessor<String, String> {
    @Override
    public String process(String value) {
        if(null != value)
            return value.toLowerCase(Locale.ROOT);
        return null;
    }
}
