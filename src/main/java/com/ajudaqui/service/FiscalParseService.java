package com.ajudaqui.service;

import com.ajudaqui.exception.FiscalParseException;
import com.ajudaqui.model.*;
import com.ajudaqui.service.filter.StateSelection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FiscalParseService {
  private final ObjectMapper mapper;

  public FiscalParseService() {
    this.mapper = new ObjectMapper();
    this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  /**
   * Faz o parse do input e retorna o objeto ParseResult com validações.
   */
  public ParseResult parse(Input input) {
    try {
      if (!(input instanceof UrlInput))
        return ParseResult.failure("Tipo de documento não suportado para o input fornecido.");

      FiscalDocument doc = StateSelection.resolve(input);
      ParseResult result = ParseResult.success(doc);
      validate(doc, result);
      return result;
    } catch (Exception e) {
      return ParseResult.failure(e.getMessage());
    }
  }

  private void validate(FiscalDocument doc, ParseResult result) {
    if (doc.getDocument() == null || doc.getDocument().getAccessKey().isEmpty()) {
      result.addWarning("Chave de acesso não identificada.");
    }
    if (doc.getItems() == null || doc.getItems().isEmpty()) {
      result.addWarning("Nenhum item foi extraído.");
    }
    if (doc.getTotals() == null || "0".equals(doc.getTotals().getTotalInvoice())) {
      result.addWarning("Valor total da nota parece estar zerado ou não foi identificado.");
    }
  }

  /**
   * Faz o parse do input e retorna uma String JSON do ParseResult.
   */
  public String parseToJson(Input input) {
    try {
      ParseResult result = parse(input);
      return mapper.writeValueAsString(result);
    } catch (Exception e) {
      return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
    }
  }
}
