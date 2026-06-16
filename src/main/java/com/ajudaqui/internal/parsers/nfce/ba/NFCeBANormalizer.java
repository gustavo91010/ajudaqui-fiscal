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
            parseBigDecimal(m.get("amount"))))
        .collect(Collectors.toList());

    BigDecimal totalPaid = payments.stream()
        .map(Payment::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

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
            parseDateTime(raw.getRawData().get("emissionDate")),
            ""),
        new Totals(
            parseBigDecimal(raw.getRawData().get("totalProducts")),
            parseBigDecimal(raw.getRawData().get("totalInvoice")),
            parseBigDecimal(raw.getRawData().get("discount")),
            totalPaid,
            parseBigDecimal(raw.getRawData().get("change"))),
        payments,
        raw.getItems().stream().map(m -> new Item(
            m.get("description"),
            m.get("code"),
            parseBigDecimal(m.get("quantity")),
            m.get("unit"),
            parseBigDecimal(m.get("unitValue")),
            parseBigDecimal(m.get("totalValue")))).collect(Collectors.toList()));
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
      case "90":
        return "SEM_PAGAMENTO";
      case "99":
        return "OUTROS";
      default:
        return "OUTROS (" + code + ")";
    }
  }

  private LocalDateTime parseDateTime(String value) {
    if (value == null || value.isBlank())
      return null;
    try {
      return OffsetDateTime.parse(value).toLocalDateTime();
    } catch (Exception e) {
      try {
        return LocalDateTime.parse(value);
      } catch (Exception e2) {
        return null;
      }
    }
  }

  private BigDecimal parseBigDecimal(String value) {
    if (value == null || value.isBlank())
      return BigDecimal.ZERO;
    try {
      String cleaned = value.trim().replace(",", ".");
      return new BigDecimal(cleaned);
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }
}
