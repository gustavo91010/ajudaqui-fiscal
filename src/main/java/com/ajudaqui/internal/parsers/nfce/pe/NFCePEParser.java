package com.ajudaqui.internal.parsers.nfce.pe;

import com.ajudaqui.model.UrlInput;
import com.ajudaqui.internal.FiscalParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFCePEParser implements FiscalParser<NFCePERawDocument, UrlInput> {

    @Override
    public NFCePERawDocument parse(UrlInput input) {
        try {
            String sanitizedUrl = input.getUrl().replace("|", "%7C");
            
            String xml = Jsoup.connect(sanitizedUrl)
                    .userAgent("Mozilla/5.0")
                    .execute()
                    .body();

            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            
            Map<String, String> rawData = new HashMap<>();
            List<Map<String, String>> items = new ArrayList<>();

            Element ide = doc.select("ide").first();
            if (ide != null) {
                rawData.put("emissionDate", ide.select("dhEmi").text());
                rawData.put("number", ide.select("nNF").text());
                rawData.put("series", ide.select("serie").text());
            }

            Element emit = doc.select("emit").first();
            if (emit != null) {
                rawData.put("issuerName", emit.select("xNome").text());
                rawData.put("cnpj", emit.select("CNPJ").text());
            }
            
            Element chNFe = doc.select("chNFe").first();
            if (chNFe != null) {
                rawData.put("accessKey", chNFe.text());
            } else {
                Element infNFe = doc.select("infNFe").first();
                if (infNFe != null) {
                    rawData.put("accessKey", infNFe.attr("Id").replaceAll("[^0-9]", ""));
                }
            }

            Element icmsTot = doc.select("total > ICMSTot").first();
            if (icmsTot != null) {
                rawData.put("totalProducts", icmsTot.select("vProd").text());
                rawData.put("totalInvoice", icmsTot.select("vNF").text());
                rawData.put("discount", icmsTot.select("vDesc").text());
            }
            
            List<Map<String, String>> payments = new ArrayList<>();
            Elements detPags = doc.select("pag > detPag");
            for (Element detPag : detPags) {
                Map<String, String> payment = new HashMap<>();
                payment.put("method", detPag.select("tPag").text());
                payment.put("amount", detPag.select("vPag").text());
                payments.add(payment);
            }
            
            Element vTroco = doc.select("pag > vTroco").first();
            if (vTroco != null) {
                rawData.put("change", vTroco.text());
            }

            if (payments.isEmpty()) {
                Element pag = doc.select("pag").first();
                if (pag != null && !pag.select("vPag").text().isEmpty()) {
                    Map<String, String> payment = new HashMap<>();
                    payment.put("method", "99"); 
                    payment.put("amount", pag.select("vPag").text());
                    payments.add(payment);
                }
            }

            Elements dets = doc.select("det");
            for (Element det : dets) {
                Element prod = det.select("prod").first();
                if (prod != null) {
                    Map<String, String> item = new HashMap<>();
                    item.put("description", prod.select("xProd").text());
                    item.put("code", prod.select("cProd").text());
                    item.put("quantity", prod.select("qCom").text());
                    item.put("unit", prod.select("uCom").text());
                    item.put("unitValue", prod.select("vUnCom").text());
                    item.put("totalValue", prod.select("vProd").text());
                    items.add(item);
                }
            }

            return new NFCePERawDocument(rawData, items, payments);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao capturar ou parsear XML da SEFAZ-PE", e);
        }
    }
}
