package com.qualixium.playnb.filetype.scalatemplate.palette;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.text.ActiveEditorDrop;

public class InputDate implements ActiveEditorDrop {

    public String formVariableName;
    public String name;
    public String id;
    public String label;
    public String clazz;
    public int step;
    public boolean disabled;
    public boolean readOnly;
    public boolean required;
    public boolean autocomplete;

    private String createBody() {
        StringBuilder sbBody = new StringBuilder();
        if (HTMLPaletteUtilities.PREFIX_WITH_HELPER) {
            sbBody.append("@helper.inputDate(");
        } else {
            sbBody.append("@inputDate(");
        }
        sbBody.append(formVariableName).append("(\"").append(name).append("\")");
        
        List<String> args = new ArrayList<>();
        if (!id.isEmpty()) {
            args.add("'id -> \"" + id + "\"");
        }
        if (!clazz.isEmpty()) {
            args.add("'class -> \"" + clazz + "\"");
        }
        if (!label.isEmpty()) {
            args.add("'_label -> \"" + label + "\"");
        }
        if (step != 1) {
            args.add("'step -> \"" + step + "\"");
        }
        if (disabled) {
            args.add("'disabled -> \"disabled\"");
        }
        if (readOnly) {
            args.add("'readonly -> \"readonly\"");
        }
        if (required) {
            args.add("'required -> \"true\"");
        }
        if (!autocomplete) {
            args.add("'autocomplete -> \"off\"");
        }

        args.stream().forEach((arg) -> {
            sbBody.append(", ").append(arg);
        });
        sbBody.append(")");

        return sbBody.toString();
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {
        InputDateCustomizer c = new InputDateCustomizer(this, targetComponent);
        boolean accept = c.showDialog();
        if (accept) {
            String body = createBody();
            try {
                HTMLPaletteUtilities.insert(body, targetComponent);
            } catch (BadLocationException ble) {
                accept = false;
            }
        }
        return accept;
    }
}
