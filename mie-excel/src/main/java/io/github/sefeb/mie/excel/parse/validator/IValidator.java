package io.github.sefeb.mie.excel.parse.validator;

import io.github.sefeb.mie.excel.parse.CellInfo;
import io.github.sefeb.mie.excel.parse.IInterceptor;

public interface IValidator<I> extends IInterceptor {

    ValidateResult validate(CellInfo cell, I value);

    String getErrorMessage(String errorCode);

}
