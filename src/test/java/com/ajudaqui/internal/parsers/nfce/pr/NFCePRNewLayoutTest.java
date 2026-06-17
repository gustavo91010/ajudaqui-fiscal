package com.ajudaqui.internal.parsers.nfce.pr;

import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.State;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NFCePRNewLayoutTest {

    @Test
    public void shouldParseNewHtmlFromParanaCorrectly() throws IOException {
        // GIVEN: O HTML que capturamos do portal novo (2026)
        String html = Files.readString(Path.of("../parana_new.html"));
        NFCePRParser parser = new NFCePRParser();
        NFCePRNormalizer normalizer = new NFCePRNormalizer();

        // WHEN: Fazemos o parse e a normalização
        NFCePRRawDocument raw = parser.parseFromHtml(html, "https://www.fazenda.pr.gov.br/nfce/qrcode?p=41260679778379000115650010015944021856969602|3|1");
        FiscalDocument doc = normalizer.normalize(raw);

        // THEN: A estrutura deve estar correta
        assertNotNull(doc);
        assertEquals(State.PR, doc.getState());
        assertEquals("41260679778379000115650010015944021856969602", doc.getDocument().getAccessKey());
        assertEquals("AUTO POSTO HILGEMBERG LTDA", doc.getIssuer().getBusinessName());
        assertEquals("79778379000115", doc.getIssuer().getCnpj());
        assertEquals("1594402", doc.getDocument().getNumber());
        assertEquals("1", doc.getDocument().getSeries());
        assertNotNull(doc.getDocument().getEmissionDate());
        
        // Verificando Totais
        assertEquals("170,01", doc.getTotals().getTotalInvoice());
        
        // Verificando Itens
        assertEquals(1, doc.getItems().size());
        assertEquals("ETANOL", doc.getItems().get(0).getDescription());
        assertEquals("170,01", doc.getItems().get(0).getTotalValue());
        assertEquals("37,865", doc.getItems().get(0).getQuantity());
        
        // Verificando Pagamento
        assertEquals(1, doc.getPayments().size());
        assertEquals("CREDITO_LOJA", doc.getPayments().get(0).getMethod());
        assertEquals("170,01", doc.getPayments().get(0).getAmount());
    }
}
