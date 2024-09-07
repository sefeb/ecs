package io.github.sefeb.mie.excel.parse.processor;

import io.github.sefeb.mie.excel.parse.IInterceptor;

public interface IProcessor<I, O> extends IInterceptor {

    O process(I value);

}
