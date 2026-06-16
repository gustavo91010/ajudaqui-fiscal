package com.ajudaqui.internal.parsers.nfce.ba;

import com.ajudaqui.model.DocumentType;
import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.State;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NFCeBANormalizerTest {

    @Test
    public void shouldNormalizeRawDocumentCorrectly() {
        Map<String, String> rawData = new HashMap<>();
        rawData.put("issuerName", "BAHIA COMERCIO LTDA");
        rawData.put("cnpj", "12345678000199");
        rawData.put("accessKey", "29260606057223031565650080004489391081980086");
        rawData.put("number", "448939");
        rawData.put("series", "8");
        rawData.put("emissionDate", "2026-06-15T10:00:00-03:00");
        rawData.put("totalProducts", "100.00");
        rawData.put("totalInvoice", "100.00");
        rawData.put("discount", "0.00");

        List<Map<String, String>> items = List.of(
            Map.of(
                "description", "PRODUTO TESTE BA",
                "code", "101",
                "quantity", "1.00",
                "unit", "UN",
                "unitValue", "100.00",
                "totalValue", "100.00"
            )
        );

        List<Map<String, String>> payments = List.of(
            Map.of("method", "01", "amount", "100.00")
        );

        NFCeBARawDocument raw = new NFCeBARawDocument(rawData, items, payments);
        NFCeBANormalizer normalizer = new NFCeBANormalizer();

        FiscalDocument doc = normalizer.normalize(raw);

        assertEquals(DocumentType.NFCE, doc.getDocumentType());
        assertEquals(State.BA, doc.getState());
        assertEquals("BAHIA COMERCIO LTDA", doc.getIssuer().getBusinessName());
    }
}
