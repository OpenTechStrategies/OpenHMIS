//package org.pathways.openciss;
//
//import java.io.BufferedReader;
//import java.io.File;
////import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
////import java.io.IOException;
//import java.io.OutputStream;
////import java.nio.MappedByteBuffer;
////import java.nio.channels.FileChannel;
////import java.nio.charset.Charset;
////import java.text.ParseException;
////import java.text.SimpleDateFormat;
////import java.util.Date;
//import java.util.List;
//import javax.xml.bind.Marshaller;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
////import javax.xml.xpath.XPathExpression;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
////import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.xpath.XPathFactory;
//import com.google.appengine.api.files.FileService;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Agency;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Export;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Household;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Member;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Need;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Person;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.PersonHistorical;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SevenValDKRefused2HashingChoiceStatic;
////import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.SiteServiceParticipation;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Source;
//import org.pathways.openciss.info.hmis.schema._3_0.hud_hmis.Sources;
//import org.w3c.dom.Document;
////import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//
//
//
//public class HUDXPathTest {
//    FileService fileService;
//	List <Agency> agencyList;
//	List <Household> householdList;
//	List<Person> personList;
//	List <Source> sourceList;
//	JAXBContext jc;
//	DocumentBuilderFactory domFactory;
//	Document doc;
//	Document doc1;
//	XPathFactory factory;
//	static boolean b;
//	
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//    	b = false;
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void testXPath() {		
//		File f = new File("/home/eric/Desktop/TestClientsHUD3.0.xml");
//		//File f = new File("/home/eric/Desktop/test.xml");
//		//Integer headOfHouseholdID = 912909367;
//		//String sSNFamilyHeadCSV2 = null;
//		try {
//			// unmarshall XML as JAXB element
//			factory = XPathFactory.newInstance();
//			domFactory = DocumentBuilderFactory.newInstance();
//			//domFactory.setNamespaceAware(true);
//			doc = domFactory.newDocumentBuilder().parse(f);
//			domFactory.setNamespaceAware(true);
//			doc1 = domFactory.newDocumentBuilder().newDocument();
//
//			//Sources sources = new Sources();
//		    //FileReadChannel readChannel = fileService.openReadChannel(fileHUDXML, true);
//		    
//		    BufferedReader reader = new BufferedReader(new FileReader("/home/eric/Desktop/TestClientsHUD3.0.xml"));
//		    //jc = JAXBContext.newInstance(Sources.class);
//		    jc = JAXBContext.newInstance("org.pathways.openciss.info.hmis.schema._3_0.hud_hmis:org.pathways.openciss.info.hmis.schema._3_0.airs_3_0_mod");
//		    Unmarshaller unmarshaller = jc.createUnmarshaller();
//		    JAXBElement<Sources> sourcesJAXBElement = unmarshaller.unmarshal(new StreamSource(reader), Sources.class);
//		    
//		    // Sources is the root element 
//		    //Sources sourcesElement = (Sources)sourcesJAXBElement.getValue();
//		    // Loop through each Source within it
//		    //sourceList= sourcesElement.getSource();
//		    
//		    
//		    Marshaller m = jc.createMarshaller();
//		    m.setProperty(Marshaller.JAXB_FRAGMENT, true);
//		    //we should try to marshall person, not sources, since less data in subtree
//		  	try {
//		    m.marshal(sourcesJAXBElement, doc1);
//		    OutputStream os = new FileOutputStream( "/home/eric/Desktop/nosferatu.xml");
//		    m.marshal(sourcesJAXBElement, os );
//		  	} catch (JAXBException e ) {System.out.println("We had a JAXB exception");}
//		  	XPath xpath = factory.newXPath();
//		  	String hhid = "912909367";
//		  	NodeList list = (NodeList) xpath.evaluate(
//		  	  "/Sources/Source/Export/Person[/Sources/Source/Export/Person/PersonID/IDNum/text() = " + hhid + "]/SiteServiceParticipation/PersonHistorical/PersonAddress/Line1/text()", doc, XPathConstants.NODESET); 
//		  			  	System.out.println("Number of doc nodes is: " + list.getLength());
//				try {
//				for (int i = 0; i < list.getLength(); i++)
//					System.out.println("xpath Matches are: " + list.item(i).getTextContent());
//					b = true;
//			} catch (Exception e){}//System.out.println("no matches for headOfHouseholdID: " + headOfHouseholdID + " in Person");};
//        } catch (Exception e) {
//        	 b = false;
//        	 e.printStackTrace();;
//         }
//		assertTrue("Result", b);
//		
//	}
//	
////	private static String readFile(String path) throws IOException {
////		  FileInputStream stream = new FileInputStream(new File(path));
////		  try {
////		    FileChannel fc = stream.getChannel();
////		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
////		    /* Instead of using default, pass in a decoder. */
////		    return Charset.defaultCharset().decode(bb).toString();
////		  }
////		  finally {
////		    stream.close();
////		  }
////	}
//
//}