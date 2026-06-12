package com.ajudaqui.internal;

import com.ajudaqui.model.FiscalDocument;

public interface Normalizer<T extends RawFiscalDocument> {
    FiscalDocument normalize(T raw);
}
