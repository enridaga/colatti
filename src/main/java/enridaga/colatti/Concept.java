package enridaga.colatti;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Concept base class.
 * This is a fully in-memory implementation, implementing equals() and hashCode().
 * 
 * @author enridaga
 *
 */
public class Concept {
	private Set<Object> objects;
	private Set<Object> attributes;
	private int hashCode;

	Concept() {
		this(Collections.emptySet(), Collections.emptySet());
	}

	public final static Concept empty = new Concept();

	protected Concept(Object[] objects, Object[] attributes) {
		this(new HashSet<Object>(Arrays.asList(objects)), new HashSet<Object>(Arrays.asList(attributes)));
	}

	private Concept(Set<Object> objects, Set<Object> attributes) {
		this.objects = objects;
		this.attributes = attributes;
		this.hashCode = new HashCodeBuilder().append(objects).append(attributes).toHashCode();
	}

	public Set<Object> objects() {
		return Collections.unmodifiableSet(objects);
	}

	public Set<Object> attributes() {
		return Collections.unmodifiableSet(attributes);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Concept) && ((Concept) obj).attributes().equals(attributes)
				&& ((Concept) obj).objects().equals(objects);
	}

	public String toString() {
		return new StringBuilder().append(objects).append(attributes).toString();
	}

	public int hashCode() {
		return hashCode;
	}

	/*
	 * STATIC
	 */

	public static final Concept make(Collection<Object> objects, Collection<Object> attributes) {
		return new Concept(objects.toArray(), attributes.toArray());
	}

	public static final Concept make(Object[] objects, Object[] attributes) {
		return new Concept(objects, attributes);
	}

	public static final Concept makeFromSingleObject(Object object, Object... attributes) {
		return new Concept(new Object[] { object }, attributes);
	}

	public static final Concept makeAddAttributes(Concept concept, Object... attributesToAdd) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		attributes.addAll(Arrays.asList(attributesToAdd));
		return new Concept(concept.objects(), attributes);
	}

	public static final Concept makeAddObject(Concept concept, Object object) {
		Set<Object> objects = new HashSet<Object>();
		objects.addAll(concept.objects());
		objects.add(object);
		return new Concept(objects, concept.attributes());
	}

	public static final Concept makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin) {
		Set<Object> attributes = new HashSet<Object>();
		attributes.addAll(concept.attributes());
		for (Object[] attributesToAdd : attributeSetsToJoin) {
			attributes.addAll(Arrays.asList(attributesToAdd));
		}
		return new Concept(concept.objects(), attributes);
	}
}