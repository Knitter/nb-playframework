package com.qualixium.playnb.unit.routes;

import com.qualixium.playnb.filetype.routes.RoutesLanguageHelper;
import com.qualixium.playnb.filetype.routes.parser.RoutesLineInfo;
import com.qualixium.playnb.util.MiscUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoutesFormattingUnitTest {

    static String goodRoutesFileContent;

    @BeforeClass
    public static void setUpClass() throws URISyntaxException, IOException {
        URI uri = RoutesFormattingUnitTest.class.getClassLoader().getResource("routes").toURI();
        goodRoutesFileContent = new String(Files.readAllBytes(Paths.get(uri)));
    }

    @Test
    public void formatCompleteFileTest() {
        int spacesBetweenParts = 5;

        String fileContentFormatted = RoutesLanguageHelper.formatFile(goodRoutesFileContent, spacesBetweenParts);
        List<String> listLines = MiscUtil.getLinesFromFileContent(fileContentFormatted);

        listLines.stream().forEach(lineFormatted -> {
            RoutesLineInfo lineParsedDTO = RoutesLanguageHelper.extractLineInfo(lineFormatted);
            if (lineParsedDTO.isCorrect()) {
                int httpMethodLocation = lineFormatted.indexOf(lineParsedDTO.getHttMethod());
                int urlLocation = lineFormatted.indexOf(lineParsedDTO.getPath());
                assertTrue(spacesBetweenParts >= urlLocation - (httpMethodLocation + lineParsedDTO.getHttMethod().length()));
            }
        });
    }

}
