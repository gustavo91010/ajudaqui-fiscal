package com.ajudaqui.internal.parsers.nfce.ba;

import com.ajudaqui.model.FiscalDocument;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NFCeBAParserTest {

    @Test
    public void shouldParseHtmlFromBahiaCorrectly() throws IOException {
        // GIVEN: Um HTML real (mockado) da Bahia
        String html = Files.readString(Path.of("src/test/resources/fixtures/ba/nfce_ba_sample.html"));
        NFCeBAParser parser = new NFCeBAParser();
        NFCeBANormalizer normalizer = new NFCeBANormalizer();

        // WHEN: Fazemos o parse e a normalização
        NFCeBARawDocument raw = parser.parseFromHtml(html, "http://nfe.sefaz.ba.gov.br/...");
        FiscalDocument doc = normalizer.normalize(raw);

        // THEN: A estrutura deve estar correta e os dados batendo com o HTML
        assertNotNull(doc);
        assertEquals("29260606057223031565650080004489391081980086", doc.getDocument().getAccessKey());
        assertEquals("BAHIA COMERCIO DE ALIMENTOS LTDA", doc.getIssuer().getBusinessName());
        assertEquals("06057223000156", doc.getIssuer().getCnpj());
        
        // Verificando Totais
        assertEquals(new BigDecimal("140.00"), doc.getTotals().getTotalInvoice());
        assertEquals(new BigDecimal("10.00"), doc.getTotals().getDiscount());
        
        // Verificando Itens
        assertEquals(2, doc.getItems().size());
        assertEquals("ARROZ 5KG", doc.getItems().get(0).getDescription());
        assertEquals(new BigDecimal("70.00"), doc.getItems().get(0).getTotalValue());
        assertEquals("FEIJAO 1KG", doc.getItems().get(1).getDescription());
        
        // Verificando Pagamento
        assertEquals(1, doc.getPayments().size());
        assertEquals("DINHEIRO", doc.getPayments().get(0).getMethod());
        assertEquals(new BigDecimal("140.00"), doc.getPayments().get(0).getAmount());
    }
}
