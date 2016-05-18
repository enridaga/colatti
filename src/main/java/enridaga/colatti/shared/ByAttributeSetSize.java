package enridaga.colatti.shared;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import enridaga.colatti.Concept;

//
public class ByAttributeSetSize implements Iterator<Set<Concept>> {

	private Iterator<Concept> iterator;
	private int currentCardinality = -1;
	private Set<Concept> currentSet = null;
	private Concept waiting = null;

	public ByAttributeSetSize(Set<Concept> sortedByCardinality) {
		TreeSet<Concept> set = new TreeSet<Concept>(new AttributesAscSorter());
		iterator = set.iterator();
	}

	public int attributeCardinality() {
		return currentCardinality;
	}

	public Set<Concept> currentSet() {
		return currentSet;
	}

	public boolean hasNext() {
		return waiting != null || iterator.hasNext();
	}

	public Set<Concept> next() {
		currentSet = null;
		// Set the current item
		Set<Concept> set = new HashSet<Concept>();
		if (waiting != null) {
			currentCardinality = waiting.attributes().size();
			set.add(waiting);
			waiting = null;
		} else {
			// Waiting is null
			if (iterator.hasNext()) {
				Concept next = iterator.next();
				currentCardinality = next.attributes().size();
				set.add(next);
			} else {
				return null; // nothing!
			}
		}
		// Collect others until cardinality changes or iterator finishes ...
		while (iterator.hasNext()) {
			Concept more = iterator.next();
			int c = more.attributes().size();
			if (c == currentCardinality) {
				set.add(more);
			} else {
				waiting = more;
				break;
			}
		}
		currentSet = set;
		return set;
	}
}