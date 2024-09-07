package io.github.sefeb.mie.excel.parse.processor;

import io.github.sefeb.mie.excel.parse.CellInfo;

public class ProcessorException extends RuntimeException{

    private CellInfo cell;

    public ProcessorException(CellInfo cell, String message) {
        super(message);
        this.cell = cell;
    }

    public ProcessorException(String message) {
        this(null, message);
    }

    public ProcessorException(CellInfo cell, String message, Throwable cause) {
        super(message, cause);
        this.cell = cell;
    }

    public ProcessorException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public CellInfo getCell() {
        return this.cell;
    }

}
