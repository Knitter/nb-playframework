package eu.sergiolopes.nbp.filetype.sbt.completion;

import eu.sergiolopes.nbp.filetype.sbt.SBTLanguage;
import eu.sergiolopes.nbp.filetype.sbt.helper.SBTLanguageHelper;
import eu.sergiolopes.nbp.util.ExceptionManager;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

@MimeRegistration(mimeType = SBTLanguage.MIME_TYPE, service = CompletionProvider.class)
public class SBTCompletionProvider implements CompletionProvider {

    private static final char CHAR_TO_START_COMPLETION = ' ';

    @Override
    public CompletionTask createTask(int queryType, JTextComponent jtc) {
        if (queryType != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }

        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            @Override
            protected void query(CompletionResultSet crs, Document dcmnt, int caretOffset) {
                String filter;
                int startOffset;
                try {
                    String fileContent = dcmnt.getText(dcmnt.getStartPosition().getOffset(),
                            dcmnt.getEndPosition().getOffset());
                    final StyledDocument bDoc = (StyledDocument) dcmnt;
                    final int lineStartOffset = getRowFirstNonArroba(bDoc, caretOffset);
                    final int length = caretOffset - lineStartOffset;
                    final char[] line = bDoc.getText(lineStartOffset, length <= 0 ? 0 : length).toCharArray();
                    final int charToStartOffSet = indexOfChartToStartCompletion(line);
                    filter = new String(line, charToStartOffSet + 1, line.length - charToStartOffSet - 1);
                    if (charToStartOffSet > 0) {
                        startOffset = lineStartOffset + charToStartOffSet + 1;
                    } else {
                        startOffset = lineStartOffset;
                    }

                    List<String> items = SBTLanguageHelper.getKeyWords();
                    items.addAll(SBTLanguageHelper.getVariables(fileContent));

                    for (String itemName : items) {
                        if (itemName.startsWith(filter)) {
                            crs.addItem(new SBTCompletionItem(itemName, startOffset, caretOffset));
                        }
                    }

                } catch (BadLocationException ex) {
                    ExceptionManager.logException(ex);
                }
                crs.finish();
            }

        }, jtc);
    }

    @Override
    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        return 0;

    }

    static int getRowFirstNonArroba(StyledDocument doc, int offset) throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start, 1).charAt(0) != CHAR_TO_START_COMPLETION) {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException("calling getText(" + start + ", " + (start + 1) + ") on doc of length: " + doc.getLength(), start).initCause(ex);
            }
            start++;
        }
        return start;
    }

    static int indexOfChartToStartCompletion(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (c == CHAR_TO_START_COMPLETION) {
                return i;
            }
        }
        return -1;
    }

}