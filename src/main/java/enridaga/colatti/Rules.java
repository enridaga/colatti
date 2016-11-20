package enridaga.colatti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rules {

	private static final Logger L = LoggerFactory.getLogger(Rules.class);

	private Lattice lattice;

	public Rules(Lattice lattice) {
		this.lattice = lattice;
	}

	public Rule[] rules(Object[] inHead, Object[] inBody) throws ColattiException {
		L.debug("[start seeking rules] inHead={} inBody={}", inHead, inBody);
		// Pick the portion of inHead in the lattice (A FIFO list)
		List<Concept> C = new ArrayList<Concept>();
		Map<Set<Object>,Rule> rulesByHead = new HashMap<Set<Object>,Rule>();
		C.add(lattice.infimum());
		while (!C.isEmpty()) {
			Concept c = C.remove(0);
			Collection<Object> H = CollectionUtils.retainAll(c.attributes(), Arrays.asList(inHead));
			if (!H.isEmpty() && !CollectionUtils.retainAll(c.attributes(), Arrays.asList(inBody)).isEmpty()) {
				L.debug(" -- concept: {} -- ", c);
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
				L.trace(" -- support={}/{} {} -- ",
						new Object[] { c.objects().size(), lattice.supremum().objects().size(), support });

				r.support(support);
				if (!(r.support() > 0)) {
					continue;
				}
				// Confidence is the support divided by the number of items only
				// satisfying b.
				double confidence = 1;
				Set<Object> objectsSatisfyingB = new HashSet<Object>();
				for (Concept o : parents) {
					// IF Intent(p) = b. There exists a set of objects only
					// satisfying b (parent’s intent will be shorter then
					// Intent(c) )
					// We look for concepts matching all the body of the rule but not the head, and we collect the objects.
					if (o.attributes().containsAll(b) && ! CollectionUtils.containsAny(o.attributes(), H)) {
						objectsSatisfyingB.addAll(o.objects());
					}
				}
				if(objectsSatisfyingB.size() == 0){
					confidence = 1;
				}else{
					confidence = ((double) c.objects().size()) / ((double) objectsSatisfyingB.size());	
				}
				L.debug(" -- itemsOf(head U body)={} itemsOf(body)={} -- ", c.objects(), objectsSatisfyingB); 
				r.confidence(confidence);
				// We can add relative confidence.
				// Relative confidence is defined as the
				// difference between the size of b^inBody and the size of
				// inBody (how much the rule matches the input body)
				r.relativeConfidence(((double) CollectionUtils.retainAll(b, Arrays.asList(inBody)).size())
						/ ((double) b.size()));
				L.debug(" -- rule={} -- ", r);
				Set<Object> headSet = Stream.of(r.head()).collect(Collectors.toSet());
				
				// Is this rule better then the one already selected?
				boolean use = true;
				if(rulesByHead.containsKey(headSet)){
					Rule old = rulesByHead.get(headSet);
					if(r.relativeConfidence() < old.relativeConfidence()){
						use = false;
					}else if (r.relativeConfidence() == old.relativeConfidence()){
						// Check confidence
						if(r.confidence() < old.confidence()){
							use = false;
						}else if (r.confidence() == old.confidence()){
							// Check support
							if(r.support() < old.support()){
								use = false;
							}
						}	
					}
				}
				if(use){
					L.debug(" -- use={} -- ", r);
					rulesByHead.put(headSet, r);
				}else{
					L.debug(" -- skip={} -- ", r);
				}
			} else {
				L.debug(" -- ignore={} -- ", c);
			}
		}
		L.debug("[end seeking rules] number={}", rulesByHead.values().size());
		return rulesByHead.values().toArray(new Rule[rulesByHead.values().size()]);
	}
}
