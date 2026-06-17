package com.ajudaqui.internal.parsers.nfce.ba;

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

public class NFCeBAParser implements FiscalParser<NFCeBARawDocument, UrlInput> {

  @Override
  public NFCeBARawDocument parse(UrlInput input) {
    try {
      String url = input.getUrl();
      // Normaliza para o endpoint de QR Code se for o de consulta completa, para evitar captcha
      if (url.contains("NFCEC_consulta_chave_acesso.aspx")) {
        url = url.replace("modulos/geral/NFCEC_consulta_chave_acesso.aspx", "qrcode.aspx");
      }
      
      String sanitizedUrl = url.replace("|", "%7C");

      Document doc = Jsoup.connect(sanitizedUrl)
          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.31")
          .followRedirects(true)
          .timeout(15000)
          .get();

      return parseFromDocument(doc, sanitizedUrl);
    } catch (IOException e) {
      throw new FiscalParseException("Erro ao capturar ou parsear HTML da SEFAZ-BA: " + e.getMessage());
    }
  }

  public NFCeBARawDocument parseFromHtml(String html, String url) {
    Document doc = Jsoup.parse(html);
    return parseFromDocument(doc, url);
  }

  private NFCeBARawDocument parseFromDocument(Document doc, String url) {
    Map<String, String> rawData = new HashMap<>();
    List<Map<String, String>> items = new ArrayList<>();
    List<Map<String, String>> payments = new ArrayList<>();

    // Tenta Layout Novo (Similar ao PR)
    if (doc.select("#tabResult").iterator().hasNext() || doc.select(".chave").iterator().hasNext()) {
        parseNewLayout(doc, rawData, items, payments);
    } else {
        // Layout Antigo (Fallback)
        parseOldLayout(doc, rawData, items, payments);
    }

    // Se a chave estiver vazia, tenta pegar da URL como fallback
    if (rawData.getOrDefault("accessKey", "").isEmpty() && url != null && url.contains("p=")) {
      try {
          String p = url.split("p=")[1].split("[|&%]")[0];
          rawData.put("accessKey", p);
      } catch (Exception e) { /* ignore */ }
    }

    return new NFCeBARawDocument(rawData, items, payments);
  }

  private void parseNewLayout(Document doc, Map<String, String> rawData, List<Map<String, String>> items, List<Map<String, String>> payments) {
    rawData.put("accessKey", doc.select(".chave").text().replaceAll("[^0-9]", ""));
    
    Element infoNota = doc.select("#infos").first();
    if (infoNota != null) {
        String text = infoNota.text();
        rawData.put("number", extractByRegex(text, "nº:\\s*(\\d+)", 1));
        rawData.put("series", extractByRegex(text, "Série:\\s*(\\d+)", 1));
        rawData.put("emissionDate", extractByRegex(text, "Emissão:\\s*(\\d{2}/\\d{2}/\\d{4}\\s*\\d{2}:\\d{2}:\\d{2})", 1));
    }

    Element emitente = doc.select(".txtCenter").first();
    if (emitente != null) {
        rawData.put("issuerName", emitente.select(".txtMaior").text());
        rawData.put("cnpj", emitente.text().replaceAll(".*CNPJ:\\s*([0-9.\\-/]+).*", "$1").replaceAll("[^0-9]", ""));
    }

    // Totais e Pagamentos no layout novo
    Elements totais = doc.select("#totalNota .totalNf");
    for (Element row : totais) {
        String label = row.select("label").text().toLowerCase();
        String value = row.select("span").text();
        if (label.contains("valor total r$")) rawData.put("totalProducts", value);
        if (label.contains("valor a pagar r$")) rawData.put("totalInvoice", value);
        if (label.contains("descontos r$")) rawData.put("discount", value);
        
        if (label.contains("forma de pagamento")) {
            Map<String, String> payment = new HashMap<>();
            payment.put("method", mapFormaPagto(value));
            // Busca o valor pago na mesma div ou próxima
            Element valPagto = row.select("label:contains(Valor pago)").first();
            String strVal = (valPagto != null && valPagto.nextElementSibling() != null) ? valPagto.nextElementSibling().text() : value;
            payment.put("amount", strVal);
            payments.add(payment);
        }
    }

    // Fallback para pagamento se estiver vazio
    if (payments.isEmpty() && rawData.containsKey("totalInvoice")) {
        Map<String, String> payment = new HashMap<>();
        payment.put("method", "99");
        payment.put("amount", rawData.get("totalInvoice"));
        payments.add(payment);
    }

    // Itens no layout novo
    Elements rows = doc.select("#tabResult tr");
    for (Element row : rows) {
        if (row.select(".txtTit").iterator().hasNext()) {
            Map<String, String> item = new HashMap<>();
            item.put("description", row.select(".txtTit").text());
            item.put("code", row.select(".txtCod").text().replaceAll("[^0-9]", ""));
            
            String qtdText = row.select(".Rqtd").text();
            item.put("quantity", qtdText.contains(":") ? qtdText.substring(qtdText.indexOf(":") + 1).trim() : qtdText.trim());
            
            String unitText = row.select(".Runid").text();
            item.put("unit", unitText.contains(":") ? unitText.substring(unitText.indexOf(":") + 1).trim() : unitText.trim());
            
            String valUnitText = row.select(".RvalUnit").text();
            item.put("unitValue", valUnitText.contains(":") ? valUnitText.substring(valUnitText.indexOf(":") + 1).trim() : valUnitText.trim());
            
            item.put("totalValue", row.select(".valor").text());
            items.add(item);
        }
    }
  }

  private void parseOldLayout(Document doc, Map<String, String> rawData, List<Map<String, String>> items, List<Map<String, String>> payments) {
    rawData.put("accessKey", doc.select("#lbl_chave_acesso").text().replaceAll("[^0-9]", ""));
    rawData.put("number", doc.select("#lbl_numero").text());
    rawData.put("series", doc.select("#lbl_serie").text());
    rawData.put("emissionDate", doc.select("#lbl_data_emissao").text());
    rawData.put("issuerName", doc.select("#lbl_nome_razao_social_emit").text());
    rawData.put("cnpj", doc.select("#lbl_cnpj_emit").text().replaceAll("[^0-9]", ""));
    rawData.put("totalProducts", doc.select("#lbl_valor_total_bruto").text());
    rawData.put("totalInvoice", doc.select("#lbl_valor_a_pagar").text());
    rawData.put("discount", doc.select("#lbl_valor_total_desconto").text());

    // Pagamentos (Layout Antigo)
    String formaPagto = doc.select("#lbl_forma_pagamento").text();
    String valorPagto = doc.select("#lbl_valor_pagamento").text();
    if (!formaPagto.isEmpty()) {
      Map<String, String> payment = new HashMap<>();
      payment.put("method", mapFormaPagto(formaPagto));
      payment.put("amount", valorPagto);
      payments.add(payment);
    }

    Elements rows = doc.select("#Gdv_itens_nfce tr:has(td)");
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
    String t = texto.toLowerCase();
    if (t.contains("dinheiro")) return "01";
    if (t.contains("cartao") && t.contains("credito")) return "03";
    if (t.contains("cartao") && t.contains("debito")) return "04";
    if (t.contains("pix")) return "17";
    return "99";
  }
}
