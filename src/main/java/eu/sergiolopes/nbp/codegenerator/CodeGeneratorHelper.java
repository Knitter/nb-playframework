package eu.sergiolopes.nbp.codegenerator;

import eu.sergiolopes.nbp.util.MiscUtil;
import java.util.List;

public class CodeGeneratorHelper {

    public static final String TAB_SPACES = "    ";

    public static String getCodeNewMethodScala(String methodName) {
        StringBuilder builder = new StringBuilder();
        builder.append("def ")
                .append(methodName)
                .append(" = TODO")
                .append(MiscUtil.LINE_SEPARATOR);

        return applySpaces(builder.toString());
    }

    public static String getCodeNewMethodJava(String methodName) {
        StringBuilder builder = new StringBuilder();

        builder.append("public Result ")
                .append(methodName)
                .append("() {")
                .append(MiscUtil.LINE_SEPARATOR)
                .append(TAB_SPACES).append("return play.mvc.Results.TODO;")
                .append(MiscUtil.LINE_SEPARATOR)
                .append("}");

        return applySpaces(builder.toString());
    }

    public static String getFileContentWithNewMethodCode(String fileContent, String newMethodCode) {
        StringBuilder sbResult = new StringBuilder(
                fileContent.substring(0, fileContent.lastIndexOf("}"))
        );

        sbResult.append(MiscUtil.LINE_SEPARATOR)
                .append(newMethodCode).append(MiscUtil.LINE_SEPARATOR)
                .append("}");

        return sbResult.toString();
    }

    public static String applySpaces(String text) {
        List<String> listLines = MiscUtil.getLinesFromFileContent(text);

        StringBuilder sbResult = new StringBuilder();
        listLines.stream()
                .forEach(line -> {
                    if (line.isEmpty()) {
                        sbResult.append(MiscUtil.LINE_SEPARATOR);
                    } else {
                        sbResult.append(TAB_SPACES).append(line).append(MiscUtil.LINE_SEPARATOR);
                    }
                });

        return sbResult.toString();
    }

}
