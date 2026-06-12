package com.ajudaqui.model;

public class UrlInput implements Input {
    private final String url;

    public UrlInput(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public Object getData() {
        return url;
    }
}
