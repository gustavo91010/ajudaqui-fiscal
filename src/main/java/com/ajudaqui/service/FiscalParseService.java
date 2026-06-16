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
    this.mapper.registerModule(new JavaTimeModule());
    this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  /**
   * Faz o parse do input e retorna o objeto FiscalDocument.
   */
  public FiscalDocument parse(Input input) {
    if (!(input instanceof UrlInput))
      throw new FiscalParseException("Tipo de documento não suportado para o input fornecido.");

    return StateSelection.resolve(input);
  }

  /**
   * Faz o parse do input e retorna uma String JSON.
   */
  public String parseToJson(Input input) {
    try {
      FiscalDocument doc = parse(input);
      return mapper.writeValueAsString(doc);
    } catch (FiscalParseException e) {
      throw e;
    } catch (Exception e) {
      String message = e.getMessage();
      if (e.getCause() != null) {
        message += " -> " + e.getCause().getMessage();
      }
      throw new FiscalParseException("Erro ao processar documento: " + message);
    }
  }
}
