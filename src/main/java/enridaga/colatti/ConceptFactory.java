package enridaga.colatti;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConceptFactory {

	private final static Concept empty = new ConceptInMemory();

	public final Concept make(Collection<Object> objects, Collection<Object> attributes) {
		return new ConceptInMemory(objects.toArray(), attributes.toArray());
	}

	public final ConceptInMemory make(Object[] objects, Object[] attributes) {
		return new ConceptInMemory(objects, attributes);
	}

	public final Concept empty() {
		return empty;
	}

	public final ConceptInMemory makeFromSingleObject(Object object, Object... attributes) {
		return new ConceptInMemory(new Object[] { object }, attributes);
	}

	public final ConceptInMemory makeAddAttributes(Concept concept, Object... attributesToAdd) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		attributes.addAll(Arrays.asList(attributesToAdd));
		return new ConceptInMemory(concept.objects(), attributes);
	}

	public final ConceptInMemory makeAddObject(Concept concept, Object object) {
		Set<Object> objects = new HashSet<Object>();
		objects.addAll(concept.objects());
		objects.add(object);
		return new ConceptInMemory(objects, concept.attributes());
	}

	public final ConceptInMemory makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		for (Object[] attributesToAdd : attributeSetsToJoin) {
			attributes.addAll(Arrays.asList(attributesToAdd));
		}
		return new ConceptInMemory(concept.objects(), attributes);
	}
}
