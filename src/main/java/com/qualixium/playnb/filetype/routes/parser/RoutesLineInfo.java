package com.qualixium.playnb.filetype.routes.parser;

/**
 * Utility class to encapsulate the three components of an Play's route (HTTP Verb, URL and Java/Scala method).
 */
public class RoutesLineInfo {

    private String httpMethod;
    private String path;
    private String action;
    private String modifierCode;

    public RoutesLineInfo() {
        this("", "", "", "");
    }

    public RoutesLineInfo(String modifierCode) {
        this("", "", "", modifierCode);
    }

    public RoutesLineInfo(String httpMethod, String path, String action) {
        this(httpMethod, path, action, "");
    }

    public RoutesLineInfo(String httpMethod, String path, String action, String modifierCode) {
        this.httpMethod = httpMethod != null ? httpMethod.trim() : "";
        this.path = path != null ? path.trim() : "";
        this.action = action != null ? action.trim() : "";
        this.modifierCode = modifierCode != null ? modifierCode.trim() : "";
    }

    /**
     * Checks if ths DTO represents a valid line. Assumes that the DTO instance can only be created by parser code that
     * makes sure the line is valid before creating the DTO value.
     *
     * @return
     */
    public boolean isCorrect() {
        return isPathModifier() || (!httpMethod.isEmpty() && !path.isEmpty() && !action.isEmpty());
    }

    /**
     * Checks if the DTO represents a path modifier line.
     *
     * @return
     */
    public boolean isPathModifier() {
        return !modifierCode.isEmpty();
    }

    /**
     * Gets the HTTP verb identified for this routes' line.
     *
     * @return
     */
    public String getHttMethod() {
        return httpMethod;
    }

    /**
     * Gets relative path (URL) for this route.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the Java/Scala method that responds to requests to this route.
     *
     * @return
     */
    public String getAction() {
        return action;
    }

}
