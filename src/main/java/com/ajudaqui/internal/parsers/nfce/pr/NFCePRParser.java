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
        rawData.put("number", extractByRegex(textoInfo, "(?:nº|Número):\\s*(\\d+)", 1));
        rawData.put("series", extractByRegex(textoInfo, "Série:\\s*(\\d+)", 1));
        rawData.put("emissionDate", extractByRegex(textoInfo, "Emissão:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2}:\\d{2})", 1));
    }

    // Emitente
    Element emitenteArea = doc.select(".txtCenter").first();
    if (emitenteArea == null) emitenteArea = doc.select("#conteudo .txtCenter").first();
    if (emitenteArea != null) {
        rawData.put("issuerName", emitenteArea.select(".txtMaior, .txtTopo").text());
        rawData.put("cnpj", emitenteArea.text().replaceAll(".*CNPJ:\\s*([0-9.\\-/]+).*", "$1").replaceAll("[^0-9]", ""));
    }

    // Totais e Pagamentos (Suporta layout antigo e novo 2026)
    Elements labels = doc.select("#totalNota label");
    if (labels.isEmpty()) labels = doc.select("#totalNota .totalNf label, #totalNota div label");
    
    for (Element label : labels) {
        String text = label.text().toLowerCase();
        Element value = label.nextElementSibling();
        if (value == null) continue;
        String valText = value.text().trim();

        if (text.contains("valor total r$") || text.contains("total bruto")) {
            rawData.put("totalProducts", valText);
        } else if (text.contains("valor a pagar r$")) {
            rawData.put("totalInvoice", valText);
        } else if (text.contains("descontos r$")) {
            rawData.put("discount", valText);
        } else if (text.contains("forma de pagamento") && !valText.toLowerCase().contains("valor pago")) {
            Map<String, String> payment = new HashMap<>();
            payment.put("method", mapFormaPagto(valText));
            // Busca o valor pago na mesma div ou próxima
            Element valPagtoLabel = label.parent().select("label:contains(Valor pago)").first();
            String strVal = (valPagtoLabel != null && valPagtoLabel.nextElementSibling() != null) ? valPagtoLabel.nextElementSibling().text() : valText;
            payment.put("amount", strVal);
            payments.add(payment);
        } else if (label.hasClass("tx") || (text.length() > 2 && valText.matches(".*\\d+.*") && !text.contains("qtd.") && !text.contains("tributos") && !text.contains("valor pago"))) {
            // Provável forma de pagamento no layout novo (o label é o nome da forma)
            Map<String, String> payment = new HashMap<>();
            payment.put("method", mapFormaPagto(text));
            payment.put("amount", valText);
            payments.add(payment);
        }
    }

    // Itens
    Elements rows = doc.select("#tabResult tr");
    for (Element row : rows) {
      Elements cols = row.select("td");
      if (cols.size() > 0) {
        Map<String, String> item = new HashMap<>();
        if (row.id().startsWith("linha_") && cols.size() >= 6) {
            // Layout Antigo
            item.put("code", cols.get(0).text());
            item.put("description", cols.get(1).text());
            item.put("quantity", cols.get(2).text());
            item.put("unit", cols.get(3).text());
            item.put("unitValue", cols.get(4).text());
            item.put("totalValue", cols.get(5).text());
        } else if (cols.get(0).select(".txtTit2").size() > 0) {
            // Layout Novo 2026
            Element descCell = cols.get(0);
            item.put("description", descCell.select(".txtTit2").text().trim());
            item.put("code", descCell.select(".RCod").text().replaceAll("[^0-9]", ""));
            
            String qtdText = descCell.select(".Rqtd").text();
            item.put("quantity", qtdText.contains(":") ? qtdText.substring(qtdText.indexOf(":") + 1).trim() : qtdText.trim());
            
            String unitText = descCell.select(".RUN, .Run").text();
            item.put("unit", unitText.contains(":") ? unitText.substring(unitText.indexOf(":") + 1).trim() : unitText.trim());
            
            String valUnitText = descCell.select(".RvalUnit").text();
            valUnitText = valUnitText.contains(":") ? valUnitText.substring(valUnitText.indexOf(":") + 1).trim() : valUnitText.trim();
            // Limpa caracteres especiais como \u00A0
            valUnitText = valUnitText.replace("\u00A0", " ").trim();
            item.put("unitValue", valUnitText);
            
            Element valCell = cols.size() > 1 ? cols.get(1) : descCell;
            item.put("totalValue", valCell.select(".valor").text().trim());
        }
        
        if (item.get("description") != null && !item.get("description").isEmpty()) {
            items.add(item);
        }
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
    if (t.contains("credito") && t.contains("loja")) return "05";
    if (t.contains("cartao") && t.contains("credito")) return "03";
    if (t.contains("cartao") && t.contains("debito")) return "04";
    if (t.contains("pix")) return "17";
    return "99";
  }
}


