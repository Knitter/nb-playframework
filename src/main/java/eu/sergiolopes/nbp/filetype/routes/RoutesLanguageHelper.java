package eu.sergiolopes.nbp.filetype.routes;

import eu.sergiolopes.nbp.PlayProject;
import eu.sergiolopes.nbp.filetype.routes.parser.RoutesLineInfo;
import eu.sergiolopes.nbp.util.MiscUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openide.filesystems.FileObject;

public class RoutesLanguageHelper {

    public static final String COMMENT_SYMBOL = "#";
    public static final String URL_START_SYMBOL = "/";

    public static final Pattern COMPILED_PATTERN_STANDARDLINE = Pattern.compile("^(GET|POST|HEAD|OPTIONS|PUT|DELETE|PATCH|->)\\s+(\\/[!#$&-;=?-\\[\\]_a-z~]*)\\s+(\\S+.+)$");
    public static final Pattern COMPILED_PATTERN_ROUTE_MODIFIER = Pattern.compile("^(\\+[a-z]+)$");

    public static List<String> getHTTPVerbs() {
        List<String> verbs = new ArrayList<>();

        verbs.add("GET");
        verbs.add("HEAD");
        verbs.add("POST");
        verbs.add("PUT");
        verbs.add("DELETE");
        verbs.add("OPTIONS");
        verbs.add("PATCH");

        return verbs;
    }

    public static RoutesLineInfo extractLineInfo(String line) {
        line = line.trim();

        Matcher matcher = COMPILED_PATTERN_STANDARDLINE.matcher(line);
        if (matcher.matches()) {
            return new RoutesLineInfo(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        matcher = COMPILED_PATTERN_ROUTE_MODIFIER.matcher(line);
        if (matcher.matches()) {
            return new RoutesLineInfo(matcher.group());
        }

        return new RoutesLineInfo();
    }

    public static String formatFile(String fileContent, int spaces) {
        //TODO: Not yet validated
        StringBuilder result = new StringBuilder();
        List<String> listAllLines = MiscUtil.getLinesFromFileContent(fileContent);

        Optional<Integer> maxLengthHTTPMethodOptional = listAllLines.stream()
                .map(line -> {
                    if (canFormatLine(line)) {
                        RoutesLineInfo lineParsedDTO = extractLineInfo(line);
                        if (lineParsedDTO.isCorrect()) {
                            return lineParsedDTO.getHttMethod().length();
                        }
                    }

                    return 0;
                })
                .max(Integer::compare);

        Optional<Integer> maxLengthURLOptional = getAllPathsFromRoutesFile(fileContent)
                .stream()
                .map(url -> url.length())
                .max(Integer::compare);

        if (maxLengthHTTPMethodOptional.isPresent() && maxLengthURLOptional.isPresent()) {
            listAllLines.stream()
                    .forEach(line -> {
                        RoutesLineInfo lineParsedDTO = extractLineInfo(line);

                        if (canFormatLine(line) && lineParsedDTO.isCorrect()) {
                            Integer maxLengthHTTPMethod = maxLengthHTTPMethodOptional.get();
                            Integer maxLenthURL = maxLengthURLOptional.get();
                            result.append(lineParsedDTO.getHttMethod())
                                    .append(MiscUtil.getAmountSeparatorChars(maxLengthHTTPMethod + spaces - lineParsedDTO.getHttMethod().length()))
                                    .append(lineParsedDTO.getPath())
                                    .append(MiscUtil.getAmountSeparatorChars(maxLenthURL + spaces - lineParsedDTO.getPath().length()))
                                    .append(lineParsedDTO.getAction());
                        } else {
                            result.append(line);
                        }

                        result.append(MiscUtil.LINE_SEPARATOR);
                    });

            //substring to remove the last LINE_SEPARATOR
            return result.toString().substring(0, result.length() - 1);
        }

        return fileContent;
    }

    public static boolean canFormatLine(String line) {
        return !line.trim().isEmpty() && !line.trim().startsWith(COMMENT_SYMBOL) && !COMPILED_PATTERN_ROUTE_MODIFIER.matcher(line).matches();
    }

    public static boolean canAutocompleteLine(String line) {
        return !line.trim().startsWith(COMMENT_SYMBOL) && !COMPILED_PATTERN_ROUTE_MODIFIER.matcher(line).matches();
    }

    public static boolean isWhiteSpaceCharacterAtIndex(String line, int indexAt) {
        return line.length() > indexAt && Character.isWhitespace(line.charAt(indexAt));
    }

    public static boolean isCharacterAtIndex(char aChar, String line, int indexAt) {
        return line.length() > indexAt && aChar == line.charAt(indexAt);
    }

    public static List<String> getAllPathsFromRoutesFile(String fileContent) {
        List<String> urls = MiscUtil.getLinesFromFileContent(fileContent).stream()
                .map(line -> {
                    if (canFormatLine(line)) {
                        RoutesLineInfo lineParsedDTO = extractLineInfo(line);
                        if (lineParsedDTO.isCorrect()) {
                            return lineParsedDTO.getPath();
                        }
                    }

                    return null;
                })
                .filter(line -> line != null)
                .distinct()
                .collect(Collectors.toList());

        return urls;
    }

    public static List<String> getAllActionsFromRoutesFile(String fileContent, boolean withRepeatedValues) {
        Stream<String> filterStream = MiscUtil.getLinesFromFileContent(fileContent).stream()
                .map(line -> {
                    if (canFormatLine(line)) {
                        RoutesLineInfo lineParsedDTO = extractLineInfo(line);
                        if (lineParsedDTO.isCorrect()) {
                            return lineParsedDTO.getAction();
                        }
                    }

                    return null;
                })
                .filter(line -> line != null);

        if (withRepeatedValues) {
            return filterStream.collect(Collectors.toList());
        }

        return filterStream.distinct().collect(Collectors.toList());
    }

    public static Optional<String> getRouteAction(String routesFileContent, int offset) {
        List<String> actions = getAllActionsFromRoutesFile(routesFileContent, true);

        return actions.stream()
                .filter(method -> {
                    int indexStart = routesFileContent.indexOf(method);
                    int indexEnd = indexStart + method.length();

                    return (offset >= indexStart && offset <= indexEnd);
                })
                .findFirst()
                .map(action -> getOnlyClassAndMethodNameFromCompleteMethodSignature(action));
    }

    public static String getOnlyClassAndMethodNameFromCompleteMethodSignature(String completeMethodSignature) {
        //TODO: Not yet validated, may not work on latest Play version
        if (completeMethodSignature.contains("(")) {
            return completeMethodSignature.substring(0, completeMethodSignature.indexOf("("));
        } else {
            return completeMethodSignature;
        }
    }

    public static String getOnlyClassNameFromCompleteMethodSignature(String completeSignatureMethod) {
        //TODO: Not yet validated, may not work on latest Play version
        if (completeSignatureMethod.contains(".")) {
            String classWithMethodName = getOnlyClassAndMethodNameFromCompleteMethodSignature(completeSignatureMethod);
            return classWithMethodName.substring(0, classWithMethodName.lastIndexOf("."));
        }

        return "";
    }

    public static String getOnlyMethodNameFromCompleteMethodSignature(String completeSignatureMethod) {
        //TODO: Not yet validated, may not work on latest Play version
        String classWithMethodName = getOnlyClassAndMethodNameFromCompleteMethodSignature(completeSignatureMethod);
        return classWithMethodName.substring(classWithMethodName.lastIndexOf(".") + 1, classWithMethodName.length());
    }

    public static List<String> getFullClassNamesFromSourceDir(PlayProject playProject) {
        //TODO: Not yet validated, may not work on latest Play version
        List<String> listSourceFiles = new ArrayList<>();
        FileObject sourceDirFO = playProject.getProjectDirectory().getFileObject("app");
        Enumeration<? extends FileObject> childrens = sourceDirFO.getChildren(true);
        while (childrens.hasMoreElements()) {
            FileObject children = childrens.nextElement();
            if (!children.isFolder()) {
                if (children.getExt().equals("java") || children.getExt().equals("scala")) {
                    String fullClassName = children.getPath()
                            .replace(sourceDirFO.getPath() + "/", "")
                            .replace("/", ".")
                            .replace(".java", "")
                            .replace(".scala", "");
                    listSourceFiles.add(fullClassName);
                }
            }
        }

        return listSourceFiles;
    }

}
