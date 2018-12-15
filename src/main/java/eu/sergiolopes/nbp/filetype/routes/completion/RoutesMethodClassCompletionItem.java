package eu.sergiolopes.nbp.filetype.routes.completion;

import eu.sergiolopes.nbp.util.ExceptionManager;
import static eu.sergiolopes.nbp.util.ui.AutoCompletionUtil.getFontColorToUse;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.ImageUtilities;

public class RoutesMethodClassCompletionItem implements CompletionItem {

    private final String text;
    private static final ImageIcon fieldIcon = new ImageIcon(ImageUtilities.loadImage("eu/sergiolopes/nbp/class_icon.png"));
    private final int startOffset;
    private final int caretOffset;

    public RoutesMethodClassCompletionItem(String text, int startOffset, int caretOffset) {
        this.text = text;
        this.startOffset = startOffset;
        this.caretOffset = caretOffset;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
//Here we remove the characters starting at the start offset 
//and ending at the point where the caret is currently found: 
            doc.remove(startOffset, caretOffset - startOffset);
            doc.insertString(startOffset, text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            ExceptionManager.logException(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke) {

    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(text, null, graphics, font);
    }

    @Override
    public void render(Graphics graphics, Font font, Color color, Color color1,
            int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(fieldIcon, text, null, graphics, font,
                getFontColorToUse(selected), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jtc) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

}
