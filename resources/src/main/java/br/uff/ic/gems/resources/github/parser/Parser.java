package br.uff.ic.gems.resources.github.parser;

import java.util.List;

/**
 *
 * @author Gleiph
 */
public class Parser {

    public static String getLink(List<String> output) {
        String BEGIN_LINK = "Link: <";
        String END_LINK = ">;";

        for (String line : output) {
            if (line.startsWith(BEGIN_LINK)) {
                line = line.replaceFirst(BEGIN_LINK, "");
                String[] split = line.split(END_LINK);

                if (!line.contains("rel=\"next\"")) {
                    return null;
                } else {
                    return split[0];
                }
            }
        }

        return null;
    }

    public static String getContent(String line) {
        String[] split = line.split("\":");
        if (split.length > 1) {
            split = split[1].split("\"");

            if (split.length > 1) {
                return split[1];
            } else if (split.length == 1) {
                return split[0].replaceAll(",","");
            }
        }
        return null;
    }

}
