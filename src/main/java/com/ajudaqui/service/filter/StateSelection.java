package com.ajudaqui.service.filter;

import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.Input;
import com.ajudaqui.service.filter.state.*;

public class StateSelection {

  public static FiscalDocument resolve(Input input) {

    StateParserHandler chain = new StatePE(
        new StateBA(
            new StatePR(
                new UnsupportedStateHandler())));

    return chain.handle(input);
  }
}
