package enridaga.colatti;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConceptFactoryInMemory implements ConceptFactory {

	private final static Concept empty = new ConceptInMemory();

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#make(java.util.Collection, java.util.Collection)
	 */
	@Override
	public final Concept make(Collection<Object> objects, Collection<Object> attributes) {
		return new ConceptInMemory(objects.toArray(), attributes.toArray());
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#make(java.lang.Object[], java.lang.Object[])
	 */
	@Override
	public final ConceptInMemory make(Object[] objects, Object[] attributes) {
		return new ConceptInMemory(objects, attributes);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#empty()
	 */
	@Override
	public final Concept empty() {
		return empty;
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#makeFromSingleObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final ConceptInMemory makeFromSingleObject(Object object, Object... attributes) {
		return new ConceptInMemory(new Object[] { object }, attributes);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#makeAddAttributes(enridaga.colatti.Concept, java.lang.Object)
	 */
	@Override
	public final ConceptInMemory makeAddAttributes(Concept concept, Object... attributesToAdd) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		attributes.addAll(Arrays.asList(attributesToAdd));
		return new ConceptInMemory(concept.objects(), attributes);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#makeAddObject(enridaga.colatti.Concept, java.lang.Object)
	 */
	@Override
	public final ConceptInMemory makeAddObject(Concept concept, Object object) {
		Set<Object> objects = new HashSet<Object>();
		objects.addAll(concept.objects());
		objects.add(object);
		return new ConceptInMemory(objects, concept.attributes());
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.ConceptFactory#makeJoinAttributes(enridaga.colatti.Concept, java.lang.Object)
	 */
	@Override
	public final ConceptInMemory makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		for (Object[] attributesToAdd : attributeSetsToJoin) {
			attributes.addAll(Arrays.asList(attributesToAdd));
		}
		return new ConceptInMemory(concept.objects(), attributes);
	}
}
