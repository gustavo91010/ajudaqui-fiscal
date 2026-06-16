package com.ajudaqui.service.filter;

import com.ajudaqui.model.FiscalDocument;
import com.ajudaqui.model.Input;

public abstract class StateParserHandler {

  protected StateParserHandler next;

  public StateParserHandler(StateParserHandler next) {
    this.next = next;
  }

  public abstract FiscalDocument handle(Input input);
  
}
