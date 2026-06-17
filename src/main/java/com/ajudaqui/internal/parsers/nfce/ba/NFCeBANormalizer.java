package com.ajudaqui.internal.parsers.nfce.ba;

import com.ajudaqui.model.*;
import com.ajudaqui.internal.Normalizer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NFCeBANormalizer implements Normalizer<NFCeBARawDocument> {

  @Override
  public FiscalDocument normalize(NFCeBARawDocument raw) {
    List<Payment> payments = raw.getPayments().stream()
        .map(m -> new Payment(
            translatePaymentMethod(m.get("method")),
            cleanValue(m.get("amount"))))
        .collect(Collectors.toList());

    String totalPaid = raw.getRawData().getOrDefault("totalInvoice", "0");
    if (payments.size() == 1) {
        totalPaid = payments.get(0).getAmount();
    }

    return new FiscalDocument(
        DocumentType.NFCE,
        State.BA,
        new Issuer(
            raw.getRawData().getOrDefault("issuerName", ""),
            "",
            raw.getRawData().getOrDefault("cnpj", ""),
            ""),
        new DocumentInfo(
            DocumentType.NFCE,
            State.BA,
            raw.getRawData().getOrDefault("accessKey", ""),
            raw.getRawData().getOrDefault("number", ""),
            raw.getRawData().getOrDefault("series", ""),
            raw.getRawData().get("emissionDate"),
            ""),
        new Totals(
            cleanValue(raw.getRawData().get("totalProducts")),
            cleanValue(raw.getRawData().get("totalInvoice")),
            cleanValue(raw.getRawData().get("discount")),
            totalPaid,
            cleanValue(raw.getRawData().get("change"))),
        payments,
        raw.getItems().stream().map(m -> new Item(
            m.get("description"),
            m.get("code"),
            cleanValue(m.get("quantity")),
            m.get("unit"),
            cleanValue(m.get("unitValue")),
            cleanValue(m.get("totalValue")))).collect(Collectors.toList()));
  }

  private String translatePaymentMethod(String code) {
    if (code == null)
      return "OUTROS";
    switch (code) {
      case "01":
        return "DINHEIRO";
      case "02":
        return "CHEQUE";
      case "03":
        return "CARTAO_CREDITO";
      case "04":
        return "CARTAO_DEBITO";
      case "05":
        return "CREDITO_LOJA";
      case "10":
        return "VALE_ALIMENTACAO";
      case "11":
        return "VALE_REFEICAO";
      case "12":
        return "VALE_PRESENTE";
      case "13":
        return "VALE_COMBUSTIVEL";
      case "15":
        return "BOLETO_BANCARIO";
      case "17":
        return "PIX";
      case "90":
        return "SEM_PAGAMENTO";
      case "99":
        return "OUTROS";
      default:
        return "OUTROS (" + code + ")";
    }
  }

  private String cleanValue(String value) {
    if (value == null || value.isBlank()) return "0";
    return value.trim().replace("R$", "").replace(" ", "").replace("\u00A0", "").trim();
  }
}
