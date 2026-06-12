package com.ajudaqui.internal.parsers.nfce.pe;

import com.ajudaqui.model.DocumentType;
import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.State;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NFCePENormalizerTest {

    @Test
    public void shouldNormalizeRawDocumentCorrectly() {
        // GIVEN: Um documento bruto simulando os dados da Padaria (Teste 2)
        Map<String, String> rawData = new HashMap<>();
        rawData.put("issuerName", "I L MAIA PADARIA LTDA");
        rawData.put("cnpj", "60832569000145");
        rawData.put("accessKey", "26260660832569000145650030000716601473652830");
        rawData.put("number", "71660");
        rawData.put("series", "3");
        rawData.put("emissionDate", "2026-06-08T18:01:55-03:00");
        rawData.put("totalProducts", "9.00");
        rawData.put("totalInvoice", "9.00");
        rawData.put("discount", "0.00");
        rawData.put("change", "1.00");

        List<Map<String, String>> items = List.of(
            Map.of(
                "description", "PAO FRANCES KG",
                "code", "607",
                "quantity", "0.7200",
                "unit", "KG",
                "unitValue", "12.50",
                "totalValue", "9.00"
            )
        );

        List<Map<String, String>> payments = List.of(
            Map.of("method", "01", "amount", "10.00")
        );

        NFCePERawDocument raw = new NFCePERawDocument(rawData, items, payments);
        NFCePENormalizer normalizer = new NFCePENormalizer();

        // WHEN: O documento é normalizado
        FiscalDocument doc = normalizer.normalize(raw);

        // THEN: Os valores devem corresponder ao esperado (Usando Getters da Lib)
        assertEquals(DocumentType.NFCE, doc.getDocumentType());
        assertEquals(State.PE, doc.getState());
        assertEquals("I L MAIA PADARIA LTDA", doc.getIssuer().getBusinessName());
        assertEquals("60832569000145", doc.getIssuer().getCnpj());
        
        // Totais
        assertEquals(new BigDecimal("9.00"), doc.getTotals().getTotalInvoice());
        assertEquals(new BigDecimal("10.00"), doc.getTotals().getTotalPaid());
        assertEquals(new BigDecimal("1.00"), doc.getTotals().getChange());
        
        // Itens
        assertEquals(1, doc.getItems().size());
        assertEquals("PAO FRANCES KG", doc.getItems().get(0).getDescription());
        assertEquals(new BigDecimal("0.7200"), doc.getItems().get(0).getQuantity());
        
        // Pagamentos
        assertEquals(1, doc.getPayments().size());
        assertEquals("DINHEIRO", doc.getPayments().get(0).getMethod());
    }
}
