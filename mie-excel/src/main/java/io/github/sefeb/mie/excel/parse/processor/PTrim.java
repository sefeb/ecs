package io.github.sefeb.mie.excel.parse.processor;

public class PTrim implements IProcessor<String, String> {

    @Override
    public String process(String value) {
        return null == value ? null : value.trim();
    }

}
