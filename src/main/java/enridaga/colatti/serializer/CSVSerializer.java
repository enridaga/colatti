package enridaga.colatti.serializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.ColattiException;
import enridaga.colatti.Concept;
import enridaga.colatti.Lattice;

public class CSVSerializer implements LatticeSerializer {
	private final static Logger log = LoggerFactory.getLogger(CSVSerializer.class);
  private PrintStream O;
  public CSVSerializer(PrintStream ps){
    O = ps;
  }
	public final String serialize(Lattice lattice) {
		List<Concept> l = new ArrayList<Concept>();
		Set<Concept> done = new HashSet<Concept>();
    //StringBuilder sb = new StringBuilder();
		try {
			l.add(lattice.infimum());
			while (!l.isEmpty()) {
				Concept c = l.iterator().next();
				l.remove(c);
				log.info("{}", c);
				if (done.contains(c)) {
					continue;
				}
				O.print(toRow(c, lattice));
				done.add(c);
				for (Concept con : lattice.parents(c)) {
					if (!done.contains(con)) {
						l.add(con);
					}
				}
			}
		} catch (ColattiException e) {
			// Lattice empty?
		}
		return "";
	}

	private final String toRow(Concept concept, Lattice lattice) {
    StringBuilder sb = new StringBuilder();
		sb.append(concept.hashCode());
    sb.append(",");
    boolean first = true;
		for(Object c : concept.objects()){
      if(first){
        first = false;
      }else{
        sb.append("|");
      }
      sb.append(c);
    }
    first = true;
    sb.append(",");

    first = true;
		for(Object c : concept.attributes()){
      if(first){
        first = false;
      }else{
        sb.append("|");
      }
      sb.append(c);
    }
    first = true;
    sb.append(",");

    try {
      first = true;
  		for(Concept c : lattice.parents(concept)){
        if(first){
          first = false;
        }else{
          sb.append("|");
        }
        sb.append(c.hashCode());
      }
    } catch (ColattiException e) {
      log.error("No parents?", e);
    }
    sb.append(",");
    try {
      first = true;
  		for(Concept c : lattice.children(concept)){
        if(first){
          first = false;
        }else{
          sb.append("|");
        }
        sb.append(c.hashCode());
      }
    } catch (ColattiException e) {
      log.error("No children?", e);
    }
    first = true;
    sb.append("\n");
		return sb.toString();
	}
}
