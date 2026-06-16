package com.ajudaqui.service.filter.state;

import com.ajudaqui.exception.FiscalParseException;
import com.ajudaqui.model.*;
import com.ajudaqui.service.filter.StateParserHandler;

public class UnsupportedStateHandler extends StateParserHandler {

  public UnsupportedStateHandler() {
    super(null);
  }

  @Override
  public FiscalDocument handle(Input input) {
    String url = ((UrlInput) input).getUrl();
    throw new FiscalParseException("Extração para a URL (" + url + ") ainda não é suportada por esta biblioteca.");
  }

}
