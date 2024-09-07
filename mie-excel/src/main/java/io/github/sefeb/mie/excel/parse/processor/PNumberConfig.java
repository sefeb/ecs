package io.github.sefeb.mie.excel.parse.processor;

public record PNumberConfig(
        Integer scale,
        Double nullAsValue,
        String roundingMode
) {
}
