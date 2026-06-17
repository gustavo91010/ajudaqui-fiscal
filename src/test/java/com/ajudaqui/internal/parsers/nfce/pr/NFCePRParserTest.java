package com.ajudaqui.internal.parsers.nfce.pr;

import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.State;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NFCePRParserTest {

    @Test
    public void shouldParseHtmlFromParanaCorrectly() throws IOException {
        // GIVEN: Um HTML real (mockado) do Paraná
        String html = Files.readString(Path.of("src/test/resources/fixtures/pr/nfce_pr_sample.html"));
        NFCePRParser parser = new NFCePRParser();
        NFCePRNormalizer normalizer = new NFCePRNormalizer();

        // WHEN: Fazemos o parse e a normalização
        NFCePRRawDocument raw = parser.parseFromHtml(html, "https://www.fazenda.pr.gov.br/...");
        FiscalDocument doc = normalizer.normalize(raw);

        // THEN: A estrutura deve estar correta e os dados batendo com o HTML
        assertNotNull(doc);
        assertEquals(State.PR, doc.getState());
        assertEquals("41260679778379000115650010015944021856969602", doc.getDocument().getAccessKey());
        assertEquals("COMERCIO DE ALIMENTOS PR LTDA", doc.getIssuer().getBusinessName());
        assertEquals("79778379000115", doc.getIssuer().getCnpj());
        
        // Verificando Totais
        assertEquals("85,00", doc.getTotals().getTotalInvoice());
        assertEquals("5,00", doc.getTotals().getDiscount());
        
        // Verificando Itens
        assertEquals(2, doc.getItems().size());
        assertEquals("LEITE INTEGRAL 1L", doc.getItems().get(0).getDescription());
        assertEquals("66,00", doc.getItems().get(0).getTotalValue());
        
        // Verificando Pagamento
        assertEquals(1, doc.getPayments().size());
        assertEquals("CARTAO_CREDITO", doc.getPayments().get(0).getMethod());
        assertEquals("85,00", doc.getPayments().get(0).getAmount());
    }
}
