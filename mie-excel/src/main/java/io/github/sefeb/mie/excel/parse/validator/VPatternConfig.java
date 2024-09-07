package io.github.sefeb.mie.excel.parse.validator;

public record VPatternConfig(
    String pattern,
    Boolean exactMatch,
    Boolean not
) {
}
