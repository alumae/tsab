package ee.ioc.phon.tsab.trs;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ee.ioc.phon.tsab.common.TsabException;

public class TRSParser {

  Hashtable<String, String> speakers = new Hashtable<String, String>();
  
  Hashtable<String, String> topics = new Hashtable<String, String>();

  Vector<Entry> entries = new Vector<Entry>();

  private String fn = null;

  public TRSParser() {
  }

  public TRSParser(String filename) throws Exception {
    loadXML(filename);
  }

  public TRSParser(File file) throws TsabException {
    loadXML(file);
  }

  /*
  <Speakers>
  <Speaker id="spk1" name="Margit Kilumets" check="yes" type="female" dialect="native" accent="" scope="global"/>
  <Speaker id="spk2" name="Kärt Tomingas" check="yes" type="female" dialect="native" accent="" scope="global"/>
  </Speakers>
  */

  public void loadSpeakers(Node node) {

    if (node.hasChildNodes()) {

      NodeList nl = node.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {

        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Speaker"
            && nl.item(i).hasAttributes()) {

          String id = nl.item(i).getAttributes().getNamedItem("id").getNodeValue();
          String name = nl.item(i).getAttributes().getNamedItem("name").getNodeValue();
          //String check = nl.item(i).getAttributes().getNamedItem("check").getNodeValue();
          //String type = nl.item(i).getAttributes().getNamedItem("type").getNodeValue();
          //String dialect = nl.item(i).getAttributes().getNamedItem("dialect").getNodeValue();
          //String accent = nl.item(i).getAttributes().getNamedItem("accent").getNodeValue();
          //String scope = nl.item(i).getAttributes().getNamedItem("scope").getNodeValue();

          speakers.put(id, name);
          //					System.out.println("New speaker: "+name);

        }
      }
    }

  }

  public void loadTopics(Node node) {

    if (node.hasChildNodes()) {

      NodeList nl = node.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {

        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Topic"
            && nl.item(i).hasAttributes()) {

          String id = nl.item(i).getAttributes().getNamedItem("id").getNodeValue();
          String desc = nl.item(i).getAttributes().getNamedItem("desc").getNodeValue();

          topics.put(id, desc);

        }
      }
    }

  }

  public void loadEpisode(Node episode) {

    if (episode.hasChildNodes()) {

      NodeList nl = episode.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {

        // <Section type="report" startTime="245.571" endTime="1635.76">
        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Section"
            && nl.item(i).hasAttributes()) {

          String type = nl.item(i).getAttributes().getNamedItem("type").getNodeValue();
          String startTime = nl.item(i).getAttributes().getNamedItem("startTime").getNodeValue();
          String endTime = nl.item(i).getAttributes().getNamedItem("endTime").getNodeValue();

          Node topicNode = nl.item(i).getAttributes().getNamedItem("topic");
          
          String topic = null; 
          if (topicNode!=null) {
            topic = topicNode.getNodeValue();
          }

          if (type.equals("filler")) {
            addFiller(startTime, endTime, topic);
          } else if (type.equals("report")) {
            loadReport(nl.item(i), topic);
          }

        }
      }
    }
  }

  private void loadReport(Node item, String topic) {

    if (item.hasChildNodes()) {

      NodeList nl = item.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {

        // <Turn speaker="spk1" mode="planned" fidelity="high" channel="studio" startTime="245.571" endTime="265.802">
        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Turn"
            && nl.item(i).hasAttributes()) {

          String speaker = nl.item(i).getAttributes().getNamedItem("speaker").getNodeValue();
          //String mode = nl.item(i).getAttributes().getNamedItem("mode").getNodeValue();
          //String fidelity = nl.item(i).getAttributes().getNamedItem("fidelity").getNodeValue();
          //String channel = nl.item(i).getAttributes().getNamedItem("channel").getNodeValue();
          //String startTime = nl.item(i).getAttributes().getNamedItem("startTime").getNodeValue();
          //String endTime = nl.item(i).getAttributes().getNamedItem("endTime").getNodeValue();

          //System.out.println("Turn: "+speaker);

          loadTurn(speaker, nl.item(i), topic);
        }

      }

    }

  }

  private void loadTurn(String spk, Node item, String topic) {

    String text = "";
    String time = "";

    if (item.hasChildNodes()) {

      NodeList nl = item.getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {

        if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
          text += (text.length() > 0 ? " " : "") + nl.item(i).getNodeValue().trim();
        }

        if (!text.equals("")
            && ((nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Sync" && nl.item(i)
                .hasAttributes()) || i == nl.getLength() - 1)) {
          entries.add(new Entry(toMs(time), speakers.get(spk), text, topic));

          //					System.out.println(speakers.get(spk)+", "+time+": "+text);
        }

        // <Turn speaker="spk1" mode="planned" fidelity="high" channel="studio" startTime="245.571" endTime="265.802">
        if (nl.item(i).getNodeType() == Node.ELEMENT_NODE && nl.item(i).getNodeName() == "Sync"
            && nl.item(i).hasAttributes()) {

          time = nl.item(i).getAttributes().getNamedItem("time").getNodeValue();
          text = "";

        }
      }

    }

  }

  private void addFiller(String startTime, String endTime, String topic) {

    entries.add(new Entry(toMs(startTime), "-", "", topic));
    //		System.out.println("filler, "+startTime);

  }

  private int toMs(String startTime) {

    return (int) (Double.parseDouble(startTime) * 1000);
  }

  public void loadXML(String filename) throws Exception {

    loadXML(new File(filename));
  }

  public void loadXML(File file) throws TsabException {

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    Document doc;

    try {
      db = dbf.newDocumentBuilder();

      db.setEntityResolver(new EntityResolver() {

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
          return new InputSource(new StringReader(""));
        }
      });

      doc = db.parse(file);
    } catch (Exception e) {
      throw new TsabException("Unable to parse XML file!", e);
    }

    doc.getDocumentElement().normalize();

    NodeList myData = doc.getElementsByTagName("Trans");
    NodeList elements = myData.item(0).getChildNodes();

    this.setFn(myData.item(0).getAttributes().getNamedItem("audio_filename").getNodeValue());

    //		String id = nl.item(i).getAttributes().getNamedItem("id").getNodeValue();

    //		<Trans scribe="Martin" audio_filename="jutusaade20090524" version="2" version_date="090918">

    for (int i = 0; i < elements.getLength(); i++) {
      Node currentNode = elements.item(i);

      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

        //System.out.println(currentNode.getNodeName());

        if (currentNode.getNodeName().equals("Speakers")) {

          loadSpeakers(currentNode);

        } else if (currentNode.getNodeName().equals("Topics")) {

          loadTopics(currentNode);

        } else if (currentNode.getNodeName().equals("Episode")) {

          loadEpisode(currentNode);

        }
      }
    }

  }

  public void setFn(String code) {
    this.fn = code;
  }

  public String getFn() {
    return fn;
  }

  public List<Entry> getFragments() {
    return entries;
  }

  public Hashtable<String, String> getTopics() {
    return topics;
  }
}
