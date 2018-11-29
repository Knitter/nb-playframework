package com.qualixium.playnb.filetype.routes.parser;

/**
 * Utility class to encapsulate the three components of an Play's route (HTTP Verb, URL and Java/Scala method).
 */
public class RoutesLineParsedDTO {

    //TODO: Cleanup this DTO, reduce name
    private String verb;
    private String url;
    private String method;

    public RoutesLineParsedDTO() {
        this("", "", "");
    }

    public RoutesLineParsedDTO(String verb, String url, String method) {
        this.verb = verb != null ? verb.trim() : "";
        this.url = url != null ? url.trim() : "";
        this.method = method != null ? method.trim() : "";
    }

    public boolean isCorrect() {
        //TODO: this doesn't validate the data to be correct.
        return !(verb.isEmpty() || url.isEmpty() || method.isEmpty());
    }

    /**
     * Gets the HTTP verb identified for this routes' line.
     *
     * @return
     */
    public String getVerb() {
        return verb;
    }

    /**
     * Gets relative path (URL) for this route.
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the Java/Scala method that responds to requests to this route.
     *
     * @return
     */
    public String getMethod() {
        return method;
    }

}
