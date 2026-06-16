package com.ajudaqui.internal.parsers.nfce.pr;

import com.ajudaqui.internal.RawFiscalDocument;
import java.util.Map;
import java.util.List;

public class NFCePRRawDocument implements RawFiscalDocument {
    private final Map<String, String> rawData;
    private final List<Map<String, String>> items;
    private final List<Map<String, String>> payments;

    public NFCePRRawDocument(Map<String, String> rawData, List<Map<String, String>> items, List<Map<String, String>> payments) {
        this.rawData = rawData;
        this.items = items;
        this.payments = payments;
    }

    public Map<String, String> getRawData() { return rawData; }
    public List<Map<String, String>> getItems() { return items; }
    public List<Map<String, String>> getPayments() { return payments; }
}
