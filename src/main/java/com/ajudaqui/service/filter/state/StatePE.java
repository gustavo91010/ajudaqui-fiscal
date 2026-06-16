package com.ajudaqui.service.filter.state;

import com.ajudaqui.internal.parsers.nfce.pe.*;
import com.ajudaqui.model.*;
import com.ajudaqui.service.filter.StateParserHandler;

public class StatePE extends StateParserHandler {

  public StatePE(StateParserHandler next) {
    super(next);
  }

  @Override
  public FiscalDocument handle(Input input) {

    String url = ((UrlInput) input).getUrl();
    if (url.contains("pe.gov.br")) {
      NFCePEParser parser = new NFCePEParser();
      NFCePENormalizer normalizer = new NFCePENormalizer();

      NFCePERawDocument raw = parser.parse((UrlInput) input);
      return normalizer.normalize(raw);
    }
    return next.handle(input);

  }

}
