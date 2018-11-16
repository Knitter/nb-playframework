package com.qualixium.playnb.filetype.routes.parser;

public class RoutesLineParsedDTO {

    public String httpMethod;
    public String url;
    public String method;

    public RoutesLineParsedDTO(String httpMethod, String url, String method) {
        this.httpMethod = httpMethod.trim();
        this.url = url.trim();
        this.method = method.trim();
    }

    public boolean isCorrect() {
        return !(httpMethod.isEmpty() || url.isEmpty() || method.isEmpty());
    }

    public static RoutesLineParsedDTO getNewEmptyDTO() {
        return new RoutesLineParsedDTO("", "", "");
    }

}
