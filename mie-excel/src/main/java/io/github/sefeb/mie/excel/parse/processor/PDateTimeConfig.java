package io.github.sefeb.mie.excel.parse.processor;

import java.util.Set;

public record PDateTimeConfig(
        Set<String> formats,    // 格式集合
        String timeZone         // 时区
) {
}
