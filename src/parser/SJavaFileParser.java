package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;


public class SJavaFileParser {
    //Constants
    private static final String END_LINE = "^(?!\\s*\\n)(?!.*\\n.*[{};]).*[{};]\\s*$";
    private static final String INVALID_COMMENT =
            "//(?!\\s*$).*[^\\w\\s].*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*\\/|\\/\\*\\*.*\\*+\\/";
    public static int INVALID_LINES =0;

    /**
     *
     * @param filePath
     * @return List of lines in the file that is relevant lines
     * @throws IOException
     */
    public static List<String> readFileToList(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
               if(relevantLine(line))
               {lines.add(deleteEscape(line));}
            }
        }
        return lines;
    }

    /**
     * Check if the line is relevant
     * @param line
     * @return true if the line is relevant
     */
    private static boolean relevantLine(String line) {
        if (line.isEmpty()) return false;
        if (line.startsWith("//")) return false;
        if (line.matches(INVALID_COMMENT)) {
            INVALID_LINES++;
            return false;
        }
        if (!line.matches(END_LINE)) {
            INVALID_LINES++;
            return false;
        }

        return true;
    }

    /**
     * Delete escape characters from the line
     * @param line
     * @return line without escape characters
     */
    private static String deleteEscape(String line) {
        return line.replaceAll("\\s+", " ");    }


}
