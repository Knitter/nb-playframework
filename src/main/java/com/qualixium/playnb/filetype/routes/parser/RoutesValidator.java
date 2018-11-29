package com.qualixium.playnb.filetype.routes.parser;

import com.qualixium.playnb.filetype.routes.RoutesLanguageHelper;
import com.qualixium.playnb.filetype.routes.parser.RoutesParsingError.RoutesErrorType;
import com.qualixium.playnb.util.ExceptionManager;
import com.qualixium.playnb.util.MiscUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.csl.api.Severity;
import org.openide.filesystems.FileObject;

public class RoutesValidator {

    private static final String INVALID_CHARS_FOR_METHOD = "!$%^&*()[]-=/+";

    public static List<RoutesParsingError> validateFile(Document document) {
        List<RoutesParsingError> listErrors = new ArrayList<>();

        try {
            String fileContent = document.getText(0, document.getLength());
            List<String> lines = MiscUtil.getLinesFromFileContent(fileContent);

            lines.stream()
                    .map(line -> line.trim())
                    .filter(line -> !line.isEmpty() && !line.startsWith(RoutesLanguageHelper.COMMENT_SYMBOL))
                    .forEach(line -> {
                        RoutesLineInfo parsedLine = RoutesLanguageHelper.extractLineInfo(line);

                        if (parsedLine.isCorrect()) {
                            listErrors.addAll(validateHttpMethod(fileContent, line, parsedLine.getHttMethod()));
                            listErrors.addAll(validateURL(fileContent, line, parsedLine.getPath()));
                            listErrors.addAll(validateMethod(document, line, parsedLine.getAction()));

                        } else {
                            int start = fileContent.indexOf(line);
                            listErrors.add(new RoutesParsingError(RoutesErrorType.BAD_LINE, Severity.ERROR, start, start + line.length()));
                        }
                    });
        } catch (BadLocationException ex) {
            ExceptionManager.logException(ex);
        }

        return listErrors;
    }

    public static List<RoutesParsingError> validateHttpMethod(String fileContent, String line, String httpMethod) {
        List<RoutesParsingError> errors = new ArrayList<>();

        boolean isHttpMethodValid = RoutesLanguageHelper.getHTTPVerbs()
                .stream()
                .anyMatch(w3cHttpMethod -> w3cHttpMethod.equals(httpMethod));

        if (!isHttpMethodValid) {
            int startPosition = MiscUtil.getStartPosition(fileContent, line, httpMethod);
            errors.add(new RoutesParsingError(RoutesErrorType.HTTP_METHOD_ERROR, Severity.ERROR, startPosition, startPosition + httpMethod.length()));
        }

        return errors;
    }

    public static List<RoutesParsingError> validateURL(String fileContent, String line, String url) {
        List<RoutesParsingError> errors = new ArrayList<>();
        boolean urlStartCorrect = url.startsWith(RoutesLanguageHelper.URL_START_SYMBOL);

        if (!urlStartCorrect) {
            int startPosition = MiscUtil.getStartPosition(fileContent, line, url);
            errors.add(new RoutesParsingError(RoutesErrorType.URL_START_INCORRECT_ERROR, Severity.ERROR, startPosition, startPosition + url.length()));
        }

        return errors;
    }

    private static List<RoutesParsingError> validateMethod(Document document, String line, String method) {
        List<RoutesParsingError> errors = new ArrayList<>();
        boolean methodExists = false;

        try {
            String fileContent = document.getText(0, document.getLength());
            Optional<RoutesParsingError> errorOptional = validateMethodStartWithInvalidCharacter(fileContent, line, method);
            errorOptional.ifPresent(error -> errors.add(error));

            String className = RoutesLanguageHelper.getOnlyClassNameFromCompleteMethodSignature(method);
            if (className.contains("Assets")) {//if contains Assets should not be validated
                methodExists = true;
            } else {
                String methodName = RoutesLanguageHelper.getOnlyMethodNameFromCompleteMethodSignature(method);
                FileObject foDocument = MiscUtil.getFileObject(document);
                ClassPath compileCp = ClassPath.getClassPath(foDocument, ClassPath.COMPILE);
                Class<?> clazz;

                try {
                    clazz = compileCp.getClassLoader(true).loadClass(className);
                    List<Method> listMethods = Arrays.asList(clazz.getDeclaredMethods());
                    methodExists = listMethods.stream()
                            .anyMatch(declaredMethod -> declaredMethod.getName().equals(methodName));
                } catch (ClassNotFoundException ex) {
                    //TODO: NEW LOG; CHECK IF VALID
                    ExceptionManager.logException(ex);
                }
            }

            if (!methodExists) {
                int startPosition = MiscUtil.getStartPosition(fileContent, line, method);
                errors.add(new RoutesParsingError(RoutesErrorType.METHOD_DOES_NOT_EXISTS, Severity.ERROR, startPosition, startPosition + method.length()));
            }
        } catch (BadLocationException ex) {
            ExceptionManager.logException(ex);
        }

        return errors;
    }

    public static Optional<RoutesParsingError> validateMethodStartWithInvalidCharacter(
            String fileContent, String line, String method) {

        boolean methodStartWithInvalidChar = INVALID_CHARS_FOR_METHOD.chars()
                .anyMatch(ch -> method.startsWith(Character.toString((char) ch)));

        if (methodStartWithInvalidChar) {
            int startPosition = MiscUtil.getStartPosition(fileContent, line, method);
            return Optional.of(new RoutesParsingError(RoutesErrorType.METHOD_START_WITH_INVALID_CHAR, Severity.ERROR, startPosition, startPosition + method.length()));
        }

        return Optional.empty();
    }

}
