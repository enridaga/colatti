package enridaga.colatti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Colatti {
	private static final Logger L = LoggerFactory.getLogger(Colatti.class);

	static final class ByAttributeSetSize implements Iterator<Set<Concept>> {

		private Iterator<Concept> iterator;
		private int currentCardinality = -1;
		private Set<Concept> currentSet = null;
		private Concept waiting = null;

		public ByAttributeSetSize(Iterator<Concept> sortedByCardinality) {
			iterator = sortedByCardinality;
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

	static final class AttributesAscSorter implements Comparator<Concept> {

		public int compare(Concept o1, Concept o2) {
			if(o1.equals(o2)){
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

	public static final Comparator<Concept> attributesAscSorter = new AttributesAscSorter();

	class Lattice {
		private TreeSet<Concept> _concepts;
		private Map<Concept, Set<Concept>> _parents;
		private Map<Concept, Set<Concept>> _children;

		public Lattice() {
			_concepts = new TreeSet<Concept>(attributesAscSorter);
			_parents = new HashMap<Concept, Set<Concept>>();
			_children = new HashMap<Concept, Set<Concept>>();
			// Initialization with an empty concept
			Concept empty = new Concept();
			add(empty);
			setParents(empty);
			setChildren(empty);
		}

		public List<Concept> concepts() {
			return Collections.unmodifiableList(Arrays.asList(_concepts.toArray(new Concept[_concepts.size()])));
		}

		public Concept supremum() {
			return _concepts.first();
		}

		public Concept infimum() {
			return _concepts.last();
		}

		public Set<Concept> parents(Concept concept) {
			return Collections.unmodifiableSet(_parents.get(concept));
		}

		public Set<Concept> children(Concept concept) {
			return Collections.unmodifiableSet(_children.get(concept));
		}

		public boolean add(Concept concept) {
			boolean ret = _concepts.add(concept);
			if(!_parents.containsKey(concept)){
				_parents.put(concept, new HashSet<Concept>());
			}
			if(!_children.containsKey(concept)){
				_children.put(concept, new HashSet<Concept>());
			}
			return ret;
		}

		/**
		 * This method does *not* adjust children relationships.
		 * 
		 * @param concept
		 * @param parents
		 */
		public void setParents(Concept concept, Concept... parents) {
			_parents.put(concept, new HashSet<Concept>(Arrays.asList(parents)));
		}

		/**
		 * This method adjust children relationships as well.
		 * 
		 * @param concept
		 * @param parents
		 */
		public void addParents(Concept concept, Concept... parents) {
			if (!_parents.containsKey(concept)) {
				_parents.put(concept, new HashSet<Concept>());
			}
			_parents.get(concept).addAll(Arrays.asList(parents));
			for (Concept p : parents) {
				if (!_children.containsKey(p)) {
					_children.put(p, new HashSet<Concept>());
				}
				_children.get(p).add(concept);
			}
		}

		/**
		 * This method does *not* adjust parents relationships.
		 * 
		 * @param concept
		 * @param parents
		 */
		public void setChildren(Concept concept, Concept... children) {
			_children.put(concept, new HashSet<Concept>(Arrays.asList(children)));
		}

		/**
		 * This method adjust parents relationships as well.
		 * 
		 * @param concept
		 * @param parents
		 */
		public void addChildren(Concept concept, Concept... children) {
			if (!_children.containsKey(concept)) {
				_children.put(concept, new HashSet<Concept>());
			}
			_children.get(concept).addAll(Arrays.asList(children));
			for (Concept c : children) {
				if (!_parents.containsKey(c)) {
					_parents.put(c, new HashSet<Concept>());
				}
				_parents.get(c).add(concept);
			}
		}

		public boolean isParentOf(Concept concept, Concept parent) {
			if (!_parents.containsKey(concept))
				return false;
			return _parents.get(concept).contains(parent);
		}

		/**
		 * This method also adjust the inverse child relation.
		 * 
		 * @param concept
		 * @param parent
		 * @return boolean - if the change was effective
		 */
		public boolean removeParent(Concept concept, Concept parent) {
			if (!_parents.containsKey(concept)) {
				return false;
			}
			if (_parents.get(concept).remove(parent)) {
				// XXX If this throws NPE then there is something wrong ...
				if (!_children.get(parent).remove(concept)) {
					// If this is false there is something wrong
					L.warn("Removing a parent was successful. However no related child relation was found.");
				}
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Remove all occurrences of 'that' and replace them with occurrences of
		 * 'with'. This method does not adjust the coherence of parent/child
		 * relationships. These are inherited as they were.
		 * 
		 * @param that
		 * @param with
		 * @throws ColattiException
		 */
		public void replace(Concept that, Concept with) throws ColattiException {
			if (!_concepts.contains(that)) {
				throw new ColattiException("'that' concept is not in the list");
			}
			_concepts.remove(that);
			_concepts.add(with);
			Set<Concept> ppp = _parents.get(that);
			_parents.remove(that);
			_parents.put(with, ppp);
			Set<Concept> ccc = _children.get(that);
			_children.remove(that);
			_children.put(with, ccc);
		}
	}

	static class Concept {
		private Set<Object> objects;
		private Set<Object> attributes;
		private int hashCode;
		
		private Concept() {
			this(Collections.emptySet(), Collections.emptySet());
		}

		public final static Concept empty = new Concept();

		private Concept(Object[] objects, Object[] attributes) {
			this(new HashSet<Object>(Arrays.asList(objects)), new HashSet<Object>(Arrays.asList(attributes)));
		}

		private Concept(Set<Object> objects, Set<Object> attributes) {
			this.objects = objects;
			this.attributes = attributes;
			this.hashCode= new HashCodeBuilder().append(objects).append(attributes).toHashCode();
		}

		Set<Object> objects() {
			return Collections.unmodifiableSet(objects);
		}

		Set<Object> attributes() {
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

		public int hashCode(){
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

	public static class ColattiException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ColattiException(String msg) {
			super(msg);
		}

		public ColattiException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}

	private Lattice lattice;

	public Colatti() {
		lattice = new Lattice();
	}
	
	public Lattice lattice(){
		return lattice;
	}

	/**
	 * Returns true if a new concept is created/modified.
	 * 
	 * @param object
	 * @param attributes
	 * @return
	 * @throws ColattiException
	 */
	public boolean add(Object object, Object... attributes) throws ColattiException {
		L.trace("Called add({},{})",object, attributes);
		boolean created = false;
		// 1. Adjust sup(G) for new attributes
		// If lattice is empty
		if (lattice.supremum().equals(Concept.empty)) {
			L.trace("Supremum is empty, creating concept and replace supremum");
			Concept c = Concept.makeFromSingleObject(object, attributes);
			created = true;
			// This concept becomes sup(G)
			lattice.replace(lattice.supremum(), c);
		} else {
			// If there are new attributes
			if (!lattice.infimum().attributes().containsAll(Arrays.asList(attributes))) {
				L.trace("Infimum does not contain all object's attributes");
				// If the object set in the infimum is empty
				if (lattice.infimum().objects().isEmpty()) {
					L.trace("Infimum's object set is empty, replacing with a new infimum with all the new attributes as well.");
					lattice.replace(lattice.infimum(), Concept.makeAddAttributes(lattice.infimum(), attributes));
				} else {
					// The object set of the infimum is not empty.
					// Create a new Concept with empty objects and all the
					// attributes from the infimum plus the ones from this
					// object, and set it as a new infimum
					Concept newInfimum = Concept.makeJoinAttributes(Concept.empty,
							lattice.infimum().attributes().toArray(), attributes);
					created = true;
					Concept oldInfimum = lattice.infimum();
					L.trace("old infimum: {}", oldInfimum);
					L.trace("new infimum: {}", newInfimum);
					if(L.isDebugEnabled()){
						L.debug("Parents/Children of old infimum (before change): {} / {}", lattice.parents(oldInfimum), lattice.children(oldInfimum));
					}
					// Link old infimum to new one
					L.trace("Adding new infimum: {}", lattice.add(newInfimum));
					// Add new edge to new infimum
					lattice.addChildren(oldInfimum, newInfimum);
					if(L.isDebugEnabled()){
						L.debug("Parents/Children of old infimum: {} / {}", lattice.parents(oldInfimum), lattice.children(oldInfimum));
						L.debug("Parents/Children of new infimum: {} / {}", lattice.parents(newInfimum), lattice.children(newInfimum));
					}
				}
			}else{
				L.trace("Infimum contains all attributes.");
				if(lattice.supremum().objects().contains(object)){
					L.trace("Supremum contains the object. Exiting.");
					return false;
				}
			}
		}

		// 2. Get the Concepts grouped by attribute set cardinality (ordered
		// ascending)

			// We clone the concept set so we can modify the lattice while we
			// iterate over the old version
		ByAttributeSetSize iterator = new ByAttributeSetSize(new ArrayList<Concept>(lattice.concepts()).iterator());

		Map<Integer, Set<Concept>> collected = new HashMap<Integer, Set<Concept>>();
		L.trace("Iterating over all concepts from the top down");
		while (iterator.hasNext()) {
			Set<Concept> current = iterator.next();
			L.trace("Iterating on size {} attrs concept", iterator.attributeCardinality());
			collected.put(iterator.attributeCardinality(), new HashSet<Concept>());
			// Treat each set in ascending cardinality order
			// For each Concept
			for (Concept visiting : current) {
				L.trace("Visiting {}", visiting);
				// If the attributes of visiting is a subset of the attributes
				// of the new object
				if (Arrays.asList(attributes).containsAll(visiting.attributes())) {
					L.trace("Attributes of visiting is a subset of {}", attributes);
					// modified concept
					// add the new object to this concept
					Concept modified = Concept.makeAddObject(visiting, object);
					lattice.replace(visiting, modified);
					visiting = modified;
					collected.get(iterator.attributeCardinality()).add(modified);
					created = true;
				}

				// If the attributes of this visiting concept are the same as
				// the new one, just exit.
				if (visiting.attributes().containsAll(Arrays.asList(attributes))
						&& Arrays.asList(attributes).containsAll(visiting.attributes())) {
					L.trace("Attributes of visiting are the same as {}", attributes);
					L.trace("Exit.");
					return created;
				} else {
					// Old concept
					// Try a new intersection
					Set<Object> intersection = new HashSet<Object>();
					intersection.addAll(Arrays.asList(attributes));
					intersection.retainAll(visiting.attributes());
					L.trace("Trying intersection {}", intersection);
					/*
					 * If we already visited a Concept with the same attributes,
					 * then visiting is a generator. (assumption: intersection
					 * is smaller then current and we are traversing an ordered
					 * sequence)
					 */
					boolean isGenerator = true;
					Set<Concept> existsIn = collected.get(intersection.size());
					if (existsIn != null) {
						for (Concept c : existsIn) {
							if (c.attributes().containsAll(existsIn)) {
								L.trace("Visiting is not a generator");
								isGenerator = false;
								break;
							}
						}
					}

					if (isGenerator) {
						L.trace("Visiting is a generator");
						Set<Object> objects = new HashSet<Object>(visiting.objects());
						objects.add(object);
						Concept newConcept = Concept.make(objects.toArray(), intersection.toArray());
						L.trace("Generating new concept: {}", newConcept);
						lattice.add(newConcept);
						created = true;

						if(!collected.containsKey(newConcept.attributes().size())){
							collected.put(newConcept.attributes().size(), new HashSet<Concept>());
						}
						collected.get(newConcept.attributes().size()).add(newConcept);
						// Add edge from the new concept to the generator
						// The generator is a child of the new concept (the new
						// concept
						// has less or equal number of attributes).
						lattice.addChildren(newConcept, visiting);
						L.trace("New concept is a parent of the generator: {}", newConcept, visiting);
						// Now modifying edges
						for (int l = 0; l < intersection.size(); l++) {
							if (collected.containsKey(l)) {
								for (Concept ha : collected.get(l)) {
									// If the attribute set is a strict subset
									// of intersections
									// then this is a potential parent of the
									// new concept
									if (intersection.containsAll(ha.attributes())) {
										boolean isParent = true;
										// Lookup the children of the potential
										// parent
										// If a child's attribute set is also a
										// strict subset of intersections,
										// then this is not a parent.
										for (Concept c : lattice.children(ha)) {
											if (intersection.containsAll(c.attributes())
													&& intersection.size() > c.attributes().size()) {
												isParent = false;
												break;
											}
										}
										if (isParent) {
											// If the potential parent is a
											// parent of the generator
											// remove this edge
											if (lattice.isParentOf(ha, visiting)) {
												lattice.removeParent(ha, visiting);
											}
											// Add new parent relation
											lattice.addParents(ha, newConcept);
										}
									}
								}
							}
						}
						
						
						// If the intersection is actually the same as the set of attributes of the input object
						// then exit the algorithm
						if(intersection.containsAll(Arrays.asList(attributes))){
							L.trace("Intersection {} contains all {}", intersection, attributes);
							L.trace("Exit.");
							return created;
						}
					}
				}
			}
		}
		return created;
	}
}
