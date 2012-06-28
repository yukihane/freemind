/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tests.freemind;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.Iterator;
import java.util.Vector;

import freemind.main.FreeMindSecurityManager;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapMapModel;

/**
 * @author foltin
 * @date 30.06.2011
 */
public class ToolsTests extends FreeMindTestBase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.freemind.FreeMindTestBase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testArgsToUrlConversion() throws Exception {
		String[] args = new String[] { "/home/bla", "--quiet", "c:\\test.mm" };
		String arrayToUrls = Tools.arrayToUrls(args);
		Vector urlVector = Tools.urlStringToUrls(arrayToUrls);
		assertEquals(args.length, urlVector.size());
		for (Iterator it = urlVector.iterator(); it.hasNext();) {
			URL urli = (URL) it.next();
			System.out.println(urli);
		}
	}

	public void testRichContentConversion() throws Exception {
		String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><map version=\"0.9.0\">"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->"
				+ "<node CREATED=\"1320424144875\" ID=\"ID_984089046\" MODIFIED=\"1320424283250\" TEXT=\"GREEK LETTERS&#x391;&#x392;&#x393;&#x394;&#x395;&#x396;&#x397;&#x398;&#x399;&#x39a;&#x39b;&#x39c;&#x39d;&#x39e;&#x39f;&#x3a0;&#x3a1;&#x3a3;&#x3a4;&#x3a5;&#x3a6;&#x3a7;&#x3a8;&#x3a9; &#x3b1;&#x3b2;&#x3b3;&#x3b4;&#x3b5;&#x3b6;&#x3b7;&#x3b8;&#x3b9;&#x3ba;&#x3bb;&#x3bc;&#x3bd;&#x3be;&#x3bf;&#x3c0;&#x3c1;&#x3c3;&#x3c4;&#x3c5;&#x3c6;&#x3c7;&#x3c8;&#x3c9; &#x3ac;&#x3ad;&#x3ae;&#x3af;&#x3cc;&#x3cd;&#x3ce;\">"
				+ "<node CREATED=\"1320424155937\" ID=\"ID_1884129484\" MODIFIED=\"1320424262562\" POSITION=\"right\">"
				+ "<richcontent TYPE=\"NODE\"><html>"
				+ "  <head>"
				+ "    "
				+ "  </head>"
				+ "  <body>"
				+ "    <p>"
				+ "      &#x391;&#x392;&#x393;&#x394;&#x395;&#x396;&#x397;&#x398;&#x399;&#x39a;&#x39b;&#x39c;&#x39d;&#x39e;&#x39f;&#x3a0;&#x3a1;&#x3a3;&#x3a4;&#x3a5;&#x3a6;&#x3a7;&#x3a8;&#x3a9;"
				+ "    </p>"
				+ "    <p>"
				+ "      &#x3b1;&#x3b2;&#x3b3;&#x3b4;&#x3b5;&#x3b6;&#x3b7;&#x3b8;&#x3b9;&#x3ba;&#x3bb;&#x3bc;&#x3bd;&#x3be;&#x3bf;&#x3c0;&#x3c1;&#x3c3;&#x3c4;&#x3c5;&#x3c6;&#x3c7;&#x3c8;&#x3c9; &#x3ac;&#x3ad;&#x3ae;&#x3af;&#x3cc;&#x3cd;&#x3ce;"
				+ "    </p>"
				+ "  </body>"
				+ "</html>"
				+ "</richcontent>"
				+ "</node>" + "</node>" + "</map>";
		Reader updateReader = Tools.getUpdateReader(new StringReader(input),
				MindMapMapModel.FREEMIND_VERSION_UPDATER_XSLT, mFreeMindMain);
		String result = Tools.getFile(updateReader);
		result = HtmlTools.unicodeToHTMLUnicodeEntity(result, true);
		System.out.println(result);
		assertEquals("Correct conversion", input,
				result.replaceAll("&#xd;$", "").trim());
	}

	public void testUrlConversion() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/Ja\u0308nstra\u00dfe 270c.pdf");
		System.out.println("input file " + input);
		URL url = Tools.fileToUrl(input);
		String externalForm = HtmlTools.unicodeToHTMLUnicodeEntity(
				url.toExternalForm(), false);
		System.out.println("External form: " + externalForm);
		// convert back:
		String unescapeHTMLUnicodeEntity = HtmlTools
				.unescapeHTMLUnicodeEntity(externalForm);
		File urlToFile = Tools.urlToFile(new URL(unescapeHTMLUnicodeEntity));
		assertEquals("Forth and back should give the same",
				input.getAbsolutePath(), urlToFile.getAbsolutePath());

	}

	public void testRelativeUrls() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../downloads/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File("/Users/foltin/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	public void testRelativeUrls2() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/subdir1/subdir2/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../downloads/subdir1/subdir2/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File("/Users/foltin/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	public void testRelativeUrls3() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../../../downloads/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File("/Users/foltin/tmp/subdir1/subdir2/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	public void testRelativeUrls4() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File("/Users/foltin/downloads/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	public void testRelativeUrlsSpaces() throws Exception {
		File input = new File(
				"/Users/foltin/downloads/subd ir1/subdi r2/Ja\u0308nstra\u00dfe 270c.pdf");
		String expected = "../downloads/subd%20ir1/subdi%20r2/Ja\u0308nstra\u00dfe%20270c.pdf";
		File mapFile = new File("/Users/foltin/tmp/im.mm");
		testCorrectRelativism(input, expected, mapFile);

	}

	protected void testCorrectRelativism(File input, String expected,
			File mapFile) throws MalformedURLException {
		String relative = Tools.fileToRelativeUrlString(input, mapFile);
		assertEquals("Correct relative result", expected, relative);
		URL u = new URL(Tools.fileToUrl(mapFile), relative);
		URL e = Tools.fileToUrl(input);
		assertEquals("Correct absolute  result", e.toExternalForm(),
				u.toExternalForm());
	}

	public void testOccurrences() throws Exception {
		assertEquals("Correct amount", 5,
				Tools.countOccurrences("abababaa", "a"));
		assertEquals("Correct amount", 3,
				Tools.countOccurrences("abababaa", "ab"));
	}

	public void testUpdate() throws FileNotFoundException, IOException {
		doUpdate();
	}

	public void testUpdateWithSecurityManager() throws FileNotFoundException, IOException {
		/** Due to a java bug (in version 7 update 4), setting a security manager
		 * (this is normally done in FreeMind)
		 * breaks the update. This is tested here. */
		System.setSecurityManager(new FreeMindSecurityManager());
		doUpdate();
	}
	
	protected void doUpdate() throws IOException {
		String input = "<map version=\"0.9.0\">"
				+ "<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->"
				+ "<node CREATED=\"1337970913625\" ID=\"ID_1753131052\" MODIFIED=\"1337970913625\" TEXT=\"Neue Mindmap\"/>"
				+ "</map>";
		Reader updateReader = Tools.getUpdateReader(new StringReader(input),
				MindMapMapModel.FREEMIND_VERSION_UPDATER_XSLT, getFrame());
		String output = Tools.getFile(updateReader);
		assertEquals("Correct output",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + input,
				output.trim());
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ToolsTests.class);
	}

}
