import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class HtmlProcessor {
	public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java HtmlProc <-type> <file>");
            return;
        }
        String type = args[0];
		String sourceUrlString = args[1];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		MicrosoftTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		String title=getTitle(source);
		String description=getMetaValue(source,"description");
		String keywords=getMetaValue(source,"keywords");
		List<Element> linkElements=source.getAllElements(HTMLElementName.A);

        if (type.equals("-title")) {
            System.out.println(title==null ? "" : title);
        } else if (type.equals("-meta")) {
            System.out.println(description==null ? "" : description);
            System.out.println(keywords==null ? "" : keywords);
        } else if (type.equals("-links")) {
            for (Element linkElement : linkElements) {
                String href=linkElement.getAttributeValue("href");
                if (href==null) continue;
                System.out.println(href);
            }
        } else {
            System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString());
        }
    }

	private static String getTitle(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}

	private static String getMetaValue(Source source, String key) {
		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,"name",key,false);
			if (startTag==null) return null;
			if (startTag.getName()==HTMLElementName.META)
				return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
			pos=startTag.getEnd();
		}
		return null;
	}
}
