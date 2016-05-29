package enridaga.colatti;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConceptFactory {

	private final static Concept empty = new Concept();

	public final Concept make(Collection<Object> objects, Collection<Object> attributes) {
		return new Concept(objects.toArray(), attributes.toArray());
	}

	public final Concept make(Object[] objects, Object[] attributes) {
		return new Concept(objects, attributes);
	}

	public final Concept empty() {
		return empty;
	}

	public final Concept makeFromSingleObject(Object object, Object... attributes) {
		return new Concept(new Object[] { object }, attributes);
	}

	public final Concept makeAddAttributes(Concept concept, Object... attributesToAdd) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		attributes.addAll(Arrays.asList(attributesToAdd));
		return new Concept(concept.objects(), attributes);
	}

	public final Concept makeAddObject(Concept concept, Object object) {
		Set<Object> objects = new HashSet<Object>();
		objects.addAll(concept.objects());
		objects.add(object);
		return new Concept(objects, concept.attributes());
	}

	public final Concept makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		for (Object[] attributesToAdd : attributeSetsToJoin) {
			attributes.addAll(Arrays.asList(attributesToAdd));
		}
		return new Concept(concept.objects(), attributes);
	}
}
