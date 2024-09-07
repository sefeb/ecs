package io.github.sefeb.mie.excel.parse.processor;

import java.util.Set;

public record PBoolConfig(
        Set<String> symbols,   // 词汇集合
        boolean value           // 词汇集合对应的取值
) {
}
