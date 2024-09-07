package io.github.sefeb.mie.excel.parse.validator;

import java.util.Set;

public record VEnumConfig(
    Set<String> include,
    Set<String> exclude
) {
}
