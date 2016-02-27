package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;



/**
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public class GISio extends RGIS implements Serializable {
    private NumberFormat  intform;
    private DecimalFormat decform;
    private int           d;

    public GISio(int digits) {
        super();
        d = digits;
        intform = NumberFormat.getInstance(Locale.US);
        decform = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decform.setMaximumIntegerDigits(12);
        decform.setMinimumIntegerDigits(1);
        decform.setMaximumFractionDigits(d - 2);
        decform.setMinimumFractionDigits(d - 2);
    }

    public NumberFormat getIntegerForm() {
        return intform;
    }

    public DecimalFormat getDecimalForm() {
        return decform;
    }

    public int getDigits() {
        return d;
    }

    public void setDigits(int digits) {
        d = digits;
    }

    public void printBuffered(PrintWriter out, String s) {
        out.print(s);
        for (int i = s.length(); i < getDigits(); ++i)
            out.print(" ");
    }

    public void printBuffered(PrintStream out, String s) {
        out.print(s);
        for (int i = s.length(); i < getDigits(); ++i)
            out.print(" ");
    }

    public void printBuffered(String s) {
        System.out.print(s);
        for (int i = s.length(); i < getDigits(); ++i)
            System.out.print(" ");
    }

    /**
     * Extract first token from a string.  Tokens are seperated by whitespace.
     */
    public static String thisNumber(StringBuffer s) {
        int j;
        char c;

        s.insert(0, " ");
        for (j = 0; j < s.length(); j++) {
            c = s.charAt(j);
            if (Character.isWhitespace(c)) {
                break;
            }
        }
        for (; j < s.length(); j++) {
            c = s.charAt(j);
            if (!Character.isWhitespace(c)) {
                break;
            }
        }
        int startN = j;
        int endN = s.length();
        for (; j < s.length(); j++) {
            if (Character.isWhitespace(s.charAt(j))) {
                endN = j;
                break;
            }
        }
        String ss = new String(s);
        return (ss.substring(startN, endN));
    }

    /**
     * Delete first token from a StringBuffer, and return second.
     */
    public static String nextNumber(StringBuffer s) {
        int j;
        char c;
        boolean doneNumber = false;

        for (j = 0; j < s.length(); j++) {
            c = s.charAt(j);
            if (Character.isWhitespace(c)) {
                break;
            }
        }
        for (; j < s.length(); j++) {
            c = s.charAt(j);
            if (!Character.isWhitespace(c)) {
                break;
            }
        }
        int endN = s.length() - j;
        int i=0;
        for (; j < s.length(); j++) {
            c = s.charAt(j);
            s.setCharAt(i, c);
            i++;
            if (!doneNumber && Character.isWhitespace(s.charAt(j))) {
                doneNumber = true;
                endN = i - 1;
            }
        }
        s.setLength(i);
        String ss = new String(s);
        return (ss.substring(0, endN));
    }

    /**
     * Delete all commas from a string.
     */
    public static String dropCommas(String s) {
        int i = 0;
        String pre = "";

        while ((i = s.indexOf(',')) > -1) {
            pre = s.substring(0, i);
            s = pre + s.substring(i + 1, s.length());
        }
        return (s);
    }
}
