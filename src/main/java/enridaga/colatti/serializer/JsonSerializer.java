package enridaga.colatti.serializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import enridaga.colatti.ColattiException;
import enridaga.colatti.Concept;
import enridaga.colatti.Lattice;

public class JsonSerializer implements LatticeSerializer {
	private final static Logger log = LoggerFactory.getLogger(JsonSerializer.class);

	public final String serialize(Lattice lattice) {
		JsonObject o = new JsonObject();
		JsonArray cc = new JsonArray();
		List<Concept> l = new ArrayList<Concept>();
		Set<Concept> done = new HashSet<Concept>();
		try {
			l.add(lattice.infimum());
			while (!l.isEmpty()) {
				Concept c = l.iterator().next();
				l.remove(c);
				log.info("{}", c);
				if (done.contains(c)) {
					continue;
				}
				cc.add(toJSON(c, lattice));
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
		o.add("concepts", cc);
		return new Gson().toJson(o);
	}

	private final JsonObject toJSON(Concept concept, Lattice lattice) {
		JsonObject o = new JsonObject();
		o.addProperty("id", concept.hashCode());
		o.add("objects", new Gson().toJsonTree(concept.objects()));
		o.add("attributes", new Gson().toJsonTree(concept.attributes()));
		JsonArray p = new JsonArray();
		try {
			for (Concept pc : lattice.parents(concept)) {
				p.add(pc.hashCode());
			}
		} catch (ColattiException e) {
			log.error("No parents?", e);
		}
		o.add("parents", p);
		p = new JsonArray();
		try {
			for (Concept pc : lattice.children(concept)) {
				p.add(pc.hashCode());
			}
		} catch (ColattiException e) {
			log.error("No children?", e);
		}
		o.add("children", p);
		return o;
	}
}
