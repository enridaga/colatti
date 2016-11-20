package enridaga.colatti.serializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.ColattiException;
import enridaga.colatti.Concept;
import enridaga.colatti.Lattice;

public class GraphmlSerializer implements LatticeSerializer{
	private final static Logger log = LoggerFactory.getLogger(GraphmlSerializer.class);

	public final String serialize(Lattice lattice) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<graphml "
				+ "xmlns=\"http://graphml.graphdrawing.org/xmlns\" "
				+ "xmlns:java=\"http://www.yworks.com/xml/yfiles-common/1.0/java\" "
				+ "xmlns:sys=\"http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0\" "
				+ "xmlns:x=\"http://www.yworks.com/xml/yfiles-common/markup/2.0\" "
				+ "xmlns:yed=\"http://www.yworks.com/xml/yed/3\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:y=\"http://www.yworks.com/xml/graphml\" "
				+ "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd \" >\n");
		sb.append("  <key for=\"port\" id=\"d0\" yfiles.type=\"portgraphics\"/>\n" + 
				"  <key for=\"port\" id=\"d1\" yfiles.type=\"portgeometry\"/>\n" + 
				"  <key for=\"port\" id=\"d2\" yfiles.type=\"portuserdata\"/>\n" + 
				"  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d3\"/>\n" + 
				"  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d4\"/>\n" + 
				"  <key for=\"node\" id=\"d5\" yfiles.type=\"nodegraphics\"/>\n" + 
				"  <key for=\"graphml\" id=\"d6\" yfiles.type=\"resources\"/>\n" + 
				"  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d7\"/>\n" + 
				"  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>\n" + 
				"  <key for=\"edge\" id=\"d9\" yfiles.type=\"edgegraphics\"/>\n" + 
				"   \n");
		sb.append("<graph edgedefault=\"directed\" id=\"Lattice\"> \n");
		List<Concept> l = new ArrayList<Concept>();
		Set<Concept> done = new HashSet<Concept>();
		try {
			l.add(lattice.supremum());
			while (!l.isEmpty()) {
				Concept c = l.remove(0);
				log.debug("{}", c);
				sb.append("<node id=\"n").append(c.hashCode()).append("\" >");
				sb.append("<data key=\"d5\">");
				sb.append(""
						+ " <y:ShapeNode>\n" + 
						"          <y:Geometry height=\"30.0\" width=\"30.0\" x=\"-15.0\" y=\"-15.0\"/>\n" + 
						"          <y:Fill color=\"#FFFFFF\" transparent=\"false\"/>\n" + 
						"          <y:BorderStyle color=\"#FFFFFF\" raised=\"false\" type=\"line\" width=\"0.0\"/>\n" + 
						"          <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"18.1328125\" horizontalTextPosition=\"center\" iconTextGap=\"4\" modelName=\"custom\" textColor=\"#000000\" verticalTextPosition=\"bottom\" visible=\"true\" width=\"26.4765625\" x=\"1.76171875\" y=\"5.93359375\">"
						);
				sb.append(c);
				sb.append("<y:LabelModel>\n" + 
						"              <y:SmartNodeLabelModel distance=\"4.0\"/>\n" + 
						"            </y:LabelModel>\n" + 
						"            <y:ModelParameter>\n" + 
						"              <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\n" + 
						"            </y:ModelParameter>\n" + 
						"          </y:NodeLabel>\n" + 
						"          <y:Shape type=\"ellipse\"/>\n" + 
						"        </y:ShapeNode>"
						+ "");
				sb.append("</data>");
				sb.append("</node>\n");
				sb.append("<!-- ").append(c.hashCode()).append(" ").append(c).append(" -->");
				done.add(c);
				log.debug("size of l {}", l.size());
				log.debug("size of children {}", lattice.children(c).size());
				for (Concept con : lattice.children(c)) {
					sb.append("<edge id=\"e").append(c.hashCode()).append("_").append(con.hashCode()).append("\" ")
							.append("source=\"n").append(c.hashCode()).append("\" target=\"n").append(con.hashCode())
							.append("\" />\n");
					if (!done.contains(con) && !l.contains(con)) {
						log.debug("adding {}", con);
						l.add(con);
					}
				}
			}
		} catch (ColattiException e) {
			// Lattice empty?
		}
		// o.add("concepts", cc);
		sb.append("</graph>\n</graphml>");
		return sb.toString();
	}
}
