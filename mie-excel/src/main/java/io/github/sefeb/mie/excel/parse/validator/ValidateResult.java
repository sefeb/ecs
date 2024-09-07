package io.github.sefeb.mie.excel.parse.validator;

import io.github.sefeb.mie.excel.parse.CellInfo;

public record ValidateResult(
    boolean success,
    CellInfo cell,
    String errorCode,
    String errorMessage
) {
}
