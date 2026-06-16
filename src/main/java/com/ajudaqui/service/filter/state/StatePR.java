package com.ajudaqui.service.filter.state;

import com.ajudaqui.internal.parsers.nfce.pr.*;
import com.ajudaqui.model.*;
import com.ajudaqui.service.filter.StateParserHandler;

public class StatePR extends StateParserHandler {

  public StatePR(StateParserHandler next) {
    super(next);
  }

  @Override
  public FiscalDocument handle(Input input) {

    String url = ((UrlInput) input).getUrl();
    if (url.contains("pr.gov.br")) {
      NFCePRParser parser = new NFCePRParser();
      NFCePRNormalizer normalizer = new NFCePRNormalizer();

      NFCePRRawDocument raw = parser.parse((UrlInput) input);
      return normalizer.normalize(raw);
    }
    return next.handle(input);

  }

}
