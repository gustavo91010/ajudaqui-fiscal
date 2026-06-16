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
      String sanitizedUrl = input.getUrl().replace("|", "%7C");

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

    // Dados da Nota
    rawData.put("accessKey", doc.select("#lbl_chave_acesso").text().replaceAll("[^0-9]", ""));
    rawData.put("number", doc.select("#lbl_numero").text());
    rawData.put("series", doc.select("#lbl_serie").text());
    rawData.put("emissionDate", doc.select("#lbl_data_emissao").text());

    // Emitente
    rawData.put("issuerName", doc.select("#lbl_nome_razao_social_emit").text());
    rawData.put("cnpj", doc.select("#lbl_cnpj_emit").text().replaceAll("[^0-9]", ""));

    // Totais
    rawData.put("totalProducts", doc.select("#lbl_valor_total_bruto").text());
    rawData.put("totalInvoice", doc.select("#lbl_valor_a_pagar").text());
    rawData.put("discount", doc.select("#lbl_valor_total_desconto").text());

    // Pagamentos
    String formaPagto = doc.select("#lbl_forma_pagamento").text();
    String valorPagto = doc.select("#lbl_valor_pagamento").text();
    if (!formaPagto.isEmpty()) {
      Map<String, String> payment = new HashMap<>();
      payment.put("method", mapFormaPagto(formaPagto));
      payment.put("amount", valorPagto);
      payments.add(payment);
    }

    // Itens
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

    // Se a chave estiver vazia, tenta pegar da URL como fallback
    if (rawData.get("accessKey").isEmpty() && url != null && url.contains("p=")) {
      String p = url.split("p=")[1].split("%7C")[0];
      rawData.put("accessKey", p);
    }

    return new NFCeBARawDocument(rawData, items, payments);
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
