package com.ajudaqui.service.filter.state;

import com.ajudaqui.internal.parsers.nfce.ba.*;
import com.ajudaqui.model.*;
import com.ajudaqui.service.filter.StateParserHandler;

public class StateBA extends StateParserHandler {

  public StateBA(StateParserHandler next) {
    super(next);
  }

  @Override
  public FiscalDocument handle(Input input) {

    String url = ((UrlInput) input).getUrl();
    if (url.contains("ba.gov.br")) {
      NFCeBAParser parser = new NFCeBAParser();
      NFCeBANormalizer normalizer = new NFCeBANormalizer();

      NFCeBARawDocument raw = parser.parse((UrlInput) input);
      return normalizer.normalize(raw);
    }
    return next.handle(input);

  }

}
