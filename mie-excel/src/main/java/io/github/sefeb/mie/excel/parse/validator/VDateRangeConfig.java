package io.github.sefeb.mie.excel.parse.validator;

public record VDateRangeConfig(
        String start,
        String end,
        String dateTimeFormat,
        Integer backwardOffset,
        Integer forwardOffset,
        String offsetUnit
) {
}
