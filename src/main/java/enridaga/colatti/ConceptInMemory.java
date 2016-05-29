package enridaga.colatti;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * ConceptInMemory base class.
 * This is a fully in-memory implementation, implementing equals() and hashCode().
 * 
 * @author enridaga
 *
 */
public class ConceptInMemory implements Concept {
	private Set<Object> objects;
	private Set<Object> attributes;
	private int hashCode;

	public ConceptInMemory() {
		this(Collections.emptySet(), Collections.emptySet());
	}

	public ConceptInMemory(Object[] objects, Object[] attributes) {
		this(new HashSet<Object>(Arrays.asList(objects)), new HashSet<Object>(Arrays.asList(attributes)));
	}

	public ConceptInMemory(Set<Object> objects, Set<Object> attributes) {
		this.objects = objects;
		this.attributes = attributes;
		this.hashCode = new HashCodeBuilder().append(objects).append(attributes).toHashCode();
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Concept#objects()
	 */
	@Override
	public Set<Object> objects() {
		return Collections.unmodifiableSet(objects);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Concept#attributes()
	 */
	@Override
	public Set<Object> attributes() {
		return Collections.unmodifiableSet(attributes);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ConceptInMemory) && ((Concept) obj).attributes().equals(attributes)
				&& ((Concept) obj).objects().equals(objects);
	}

	public String toString() {
		return new StringBuilder().append(objects).append(attributes).toString();
	}

	public int hashCode() {
		return hashCode;
	}
}