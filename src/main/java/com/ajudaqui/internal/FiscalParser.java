package com.ajudaqui.internal;

import com.ajudaqui.model.Input;

public interface FiscalParser<T extends RawFiscalDocument, I extends Input> {
    T parse(I input);
}
