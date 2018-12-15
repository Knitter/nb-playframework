package eu.sergiolopes.nbp.unit;

import eu.sergiolopes.nbp.util.MiscUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class MiscUtilTest {

    @Test
    public void testMinimumIndex() {
        String fileContent = "this is a content( .";
        int expected = 17;

        int actual = MiscUtil.getMinimumIndex(fileContent, 14, "(.").get();

        assertEquals(expected, actual);
    }

    @Test
    public void testMinimumIndexDoNotTakeInAccountCharBeforeIndex() {
        String fileContent = "this is( a content .";
        int expected = 19;

        int actual = MiscUtil.getMinimumIndex(fileContent, 14, "(.").get();

        assertEquals(expected, actual);
    }

}
