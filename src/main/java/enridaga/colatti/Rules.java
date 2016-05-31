package enridaga.colatti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rules {

	private static final Logger L = LoggerFactory.getLogger(Rules.class);

	private Lattice lattice;

	public Rules(Lattice lattice) {
		this.lattice = lattice;
	}

	public Rule[] rules(Object[] inHead, Object[] inBody) throws ColattiException{
		L.debug("Seeking rules inHead: {} inBody: {}", inHead, inBody);
		// Pick the portion of inHead in the lattice
		// A FIFO list
		List<Concept> C = new ArrayList<Concept>();
		Set<Rule> R = new HashSet<Rule>();
		C.add(lattice.infimum());
		while (!C.isEmpty()) {
			Concept c = C.remove(0);
			Collection<Object> H = CollectionUtils.retainAll(c.attributes(), Arrays.asList(inHead));
			if (!H.isEmpty() && !CollectionUtils.retainAll(c.attributes(), Arrays.asList(inBody)).isEmpty()) {
				L.debug("Traverse: {}", c);
				// Add all parents of c to C
				Set<Concept> parents = lattice.parents(c);
				for(Concept p: parents){
					if(!C.contains(p)){
						C.add(p);
					}
				}
				Collection<Object> b = CollectionUtils.removeAll(c.attributes(), H);
				RuleImpl r = new RuleImpl();
				r.head(H.toArray());
				r.body(b.toArray());
				// Support is the size of the extent of the concept divided by
				// the nb of all objects
				double support = ((double) c.objects().size()) / ((double) lattice.supremum().objects().size());
				L.trace("Support: {}/{} {}",
						new Object[] { c.objects().size(), lattice.supremum().objects().size(), support });

				r.support(support);
				if (!(r.support() > 0)) {
					continue;
				}
				// Confidence is the support divided by the number of items only
				// satisfying b.
				double confidence = 1;
				for (Concept o : parents) {
					// IF Intent(p) = b. There exists a set of objects only
					// satisfying b (parent’s intent will be shorter then
					// Intent(c) )
					if (o.attributes().containsAll(b) && ! CollectionUtils.containsAny(o.attributes(), H)) {
						L.debug("Set confidence: {}/{}",  ((double) c.objects().size()) , ((double) o.objects().size()));
						confidence = ((double) c.objects().size()) / ((double) o.objects().size());
						break;
					}
				}
				r.confidence(confidence);
				// We can add relative confidence.
				// Relative confidence is defined as the
				// difference between the size of b^inBody and the size of
				// inBody (how much the rule matches the input body)
				r.relativeConfidence(((double) CollectionUtils.retainAll(b, Arrays.asList(inBody)).size())
						/ ((double) b.size()));
				L.debug("Rule: {}", r);
				R.add(r);
			} else {
				L.debug("Ignore: {}", c);
			}
		}
		return R.toArray(new Rule[R.size()]);
	}
}
