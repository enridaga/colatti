package enridaga.colatti.shared;

import java.util.Comparator;

import enridaga.colatti.Concept;

public class AttributesAscSorter implements Comparator<Concept> {

	public int compare(Concept o1, Concept o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		if (o1.objects().size() < o2.objects().size()) {
			return 1;
		} else if (o1.objects().size() > o2.objects().size()) {
			return -1;
		}
		if (o1.attributes().size() > o2.attributes().size()) {
			return 1;
		} else if (o1.attributes().size() < o2.attributes().size()) {
			return -1;
		}
		return o1.hashCode() > o2.hashCode() ? 1 : -1;
	}
}