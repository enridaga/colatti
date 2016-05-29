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

	interface Rule {
		Object[] body();

		Object[] head();

		double confidence();

		double support();

		double relativeConfidence();

	}

	class RuleImpl implements Rule {
		private Object[] body;
		private Object[] head;
		private double confidence;
		private double support;
		private double relativeConfidence;

		public void body(Object[] body) {
			this.body = body;
		}

		public void head(Object[] head) {
			this.head = head;
		}

		public void confidence(double confidence) {
			this.confidence = confidence;
		}

		public void support(double support) {
			this.support = support;
		}

		public void relativeConfidence(double confidence) {
			this.relativeConfidence = confidence;
		}

		@Override
		public Object[] body() {
			return body;
		}

		@Override
		public Object[] head() {
			return head;
		}

		@Override
		public double confidence() {
			return confidence;
		}

		@Override
		public double support() {
			return support;
		}

		@Override
		public double relativeConfidence() {
			return relativeConfidence;
		}

		@Override
		public String toString() {
			StringBuilder ts = new StringBuilder();
			boolean first = true;
			for (Object o : head) {
				if (first) {
					first = false;
				} else {
					ts.append(',');
				}
				ts.append(o);
			}
			ts.append("<-");
			first = true;
			for (Object o : body) {
				if (first) {
					first = false;
				} else {
					ts.append(',');
				}
				ts.append(o);
			}
			ts.append(' ');
			// measures
			ts.append('(');
			ts.append(support);
			ts.append(',');
			ts.append(confidence);
			ts.append(',');
			ts.append(relativeConfidence);
			ts.append(')');
			return ts.toString();
		}
	}

	private Lattice lattice;

	public Rules(Lattice lattice) {
		this.lattice = lattice;
	}

	public Rule[] rules(Object[] inHead, Object[] inBody) {
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
					if (o.attributes().size() == b.size() && CollectionUtils.removeAll(o.attributes(), b).isEmpty()) {
						L.debug("Adjust confidence");
						confidence = ((double) r.support()) / ((double) o.objects().size());
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
