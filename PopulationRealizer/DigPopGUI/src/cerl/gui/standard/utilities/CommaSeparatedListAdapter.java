/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cerl.gui.standard.utilities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CommaSeparatedListAdapter extends XmlAdapter<String, List<String>> {

    @Override
    public ArrayList<String> unmarshal(final String string) {
        final ArrayList<String> strings = new ArrayList<>();

        for (final String s : string.split(",")) {
            final String trimmed = s.trim();

            if (trimmed.length() > 0) {
                strings.add(trimmed);
            }
        }

        return strings;
    }

    @Override
    public String marshal(final List<String> strings) {
        final StringBuilder sb = new StringBuilder();

        strings.stream().forEach((string) -> {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(string);
        });

        return sb.toString();
    }
    
    public String marshal(final ArrayList<String> strings) {
        final StringBuilder sb = new StringBuilder();

        strings.stream().forEach((string) -> {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(string);
        });

        return sb.toString();
    }
}