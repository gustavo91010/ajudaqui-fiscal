package com.ajudaqui.internal.parsers.nfce.pr;

import com.ajudaqui.exception.FiscalParseException;
import com.ajudaqui.model.UrlInput;
import com.ajudaqui.internal.FiscalParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFCePRParser implements FiscalParser<NFCePRRawDocument, UrlInput> {

  @Override
  public NFCePRRawDocument parse(UrlInput input) {
    try {
      String sanitizedUrl = input.getUrl().replace("|", "%7C");

      Document doc = Jsoup.connect(sanitizedUrl)
          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.31")
          .followRedirects(true)
          .timeout(15000)
          .get();

      return parseFromDocument(doc, sanitizedUrl);
    } catch (IOException e) {
      throw new RuntimeException("Erro ao capturar ou parsear HTML da SEFAZ-PR: " + e.getMessage());
    }
  }

  public NFCePRRawDocument parseFromHtml(String html, String url) {
    Document doc = Jsoup.parse(html);
    return parseFromDocument(doc, url);
  }

  private NFCePRRawDocument parseFromDocument(Document doc, String url) {
    Map<String, String> rawData = new HashMap<>();
    List<Map<String, String>> items = new ArrayList<>();
    List<Map<String, String>> payments = new ArrayList<>();

    // Dados da Nota (Sefaz-PR usa classes como .txtCenter e .chave)
    rawData.put("accessKey", doc.select(".chave").text().replaceAll("[^0-9]", ""));
    
    Element infoNota = doc.select("#infos").first();
    if (infoNota != null) {
        String textoInfo = infoNota.text();
        // Regex simples ou split para pegar Numero e Serie se necessário
        // No PR geralmente está em <strong>NFC-e nº: 000...  Série: 1</strong>
        rawData.put("number", extractByRegex(textoInfo, "nº:\\s*(\\d+)", 1));
        rawData.put("series", extractByRegex(textoInfo, "Série:\\s*(\\d+)", 1));
        rawData.put("emissionDate", extractByRegex(textoInfo, "Emissão:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2}:\\d{2})", 1));
    }

    // Emitente
    Element emitenteArea = doc.select(".txtCenter").first();
    if (emitenteArea != null) {
        rawData.put("issuerName", emitenteArea.select(".txtMaior").text());
        rawData.put("cnpj", emitenteArea.text().replaceAll(".*CNPJ:\\s*([0-9.\\-/]+).*", "$1").replaceAll("[^0-9]", ""));
    }

    // Totais e Pagamentos
    Elements rowsTotal = doc.select("#totalNota .totalNf");
    for (Element rowTotal : rowsTotal) {
        Elements labels = rowTotal.select("label");
        for (Element label : labels) {
            String text = label.text().toLowerCase();
            Element value = label.nextElementSibling();
            if (value != null) {
                if (text.contains("valor total r$")) rawData.put("totalProducts", value.text());
                if (text.contains("valor a pagar r$")) rawData.put("totalInvoice", value.text());
                if (text.contains("descontos r$")) rawData.put("discount", value.text());
                
                if (text.contains("forma de pagamento")) {
                    Map<String, String> payment = new HashMap<>();
                    payment.put("method", mapFormaPagto(value.text()));
                    // Busca o valor pago na mesma div ou próxima
                    Element valPagto = label.parent().select("label:contains(Valor pago)").first();
                    String strVal = (valPagto != null && valPagto.nextElementSibling() != null) ? valPagto.nextElementSibling().text() : "";
                    payment.put("amount", strVal);
                    payments.add(payment);
                }
            }
        }
    }

    // Itens
    Elements rows = doc.select("#tabResult tr[id^=linha_]");
    for (Element row : rows) {
      Elements cols = row.select("td");
      if (cols.size() >= 6) {
        Map<String, String> item = new HashMap<>();
        item.put("code", cols.get(0).text());
        item.put("description", cols.get(1).text());
        item.put("quantity", cols.get(2).text());
        item.put("unit", cols.get(3).text());
        item.put("unitValue", cols.get(4).text());
        item.put("totalValue", cols.get(5).text());
        items.add(item);
      }
    }

    // Se a forma de pagamento for vazia ou não tiver valor, tenta um fallback básico
    if (payments.isEmpty() && rawData.containsKey("totalInvoice")) {
        Map<String, String> payment = new HashMap<>();
        payment.put("method", "99");
        payment.put("amount", rawData.get("totalInvoice"));
        payments.add(payment);
    }

    // Fallback access key from URL
    if (rawData.get("accessKey").isEmpty() && url != null && url.contains("p=")) {
      String p = url.split("p=")[1].split("%7C")[0];
      rawData.put("accessKey", p);
    }

    return new NFCePRRawDocument(rawData, items, payments);
  }

  private String extractByRegex(String text, String regex, int group) {
      try {
          java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
          java.util.regex.Matcher matcher = pattern.matcher(text);
          if (matcher.find()) return matcher.group(group);
      } catch (Exception e) {}
      return "";
  }

  private String mapFormaPagto(String texto) {
    if (texto == null) return "99";
    String t = java.text.Normalizer.normalize(texto.toLowerCase(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    if (t.contains("dinheiro")) return "01";
    if (t.contains("cartao") && t.contains("credito")) return "03";
    if (t.contains("cartao") && t.contains("debito")) return "04";
    if (t.contains("pix")) return "17";
    return "99";
  }
}


