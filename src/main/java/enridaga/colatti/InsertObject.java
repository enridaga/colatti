package enridaga.colatti;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the Godin algorithm for incremental Lattice construction.
 * The new Object must not already exist.
 * 
 * @author enridaga
 *
 */
public class InsertObject {
	private static final Logger L = LoggerFactory.getLogger(InsertObject.class);

	private Lattice lattice;

	public InsertObject(Lattice lattice) {
		this.lattice = lattice;
	}

	public Lattice lattice() {
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
	public boolean perform(Object object, Object... attributes) throws ColattiException {
		L.trace("Called add({},{})", object, attributes);

		/**
		 * This algorithm only works if object is not yet in the lattice.
		 */
		if (lattice.supremum().objects().contains(object)) {
			L.warn("Ignored. Cannot add the same object twice!");
			return false;
		}

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
					if (L.isDebugEnabled()) {
						L.debug("Parents/Children of old infimum (before change): {} / {}", lattice.parents(oldInfimum),
								lattice.children(oldInfimum));
					}
					// Link old infimum to new one
					L.trace("Adding new infimum: {}", lattice.add(newInfimum));
					// Add new edge to new infimum
					lattice.addChildren(oldInfimum, newInfimum);
					if (L.isDebugEnabled()) {
						L.debug("Parents/Children of old infimum: {} / {}", lattice.parents(oldInfimum),
								lattice.children(oldInfimum));
						L.debug("Parents/Children of new infimum: {} / {}", lattice.parents(newInfimum),
								lattice.children(newInfimum));
					}
				}
			} else {
				// This is dead code, as the check is performed at the beginning
				L.trace("Infimum contains all attributes.");
				if (lattice.supremum().objects().contains(object)) {
					L.trace("Supremum contains the object. Exiting.");
					return false;
				}
			}
		}

		// 2. Get the Concepts grouped by attribute set cardinality (ordered
		// ascending)
		Map<Integer, Set<Concept>> collected = new HashMap<Integer, Set<Concept>>();
		L.trace("Iterating over all concepts from the top down");
		for (int x = 0; x <= lattice.maxAttributeCardinality(); x++) {
			// while (iterator.hasNext()) {
			Set<Concept> current = lattice.attributesSizeIndex().get(x); // iterator.next();
			if (current == null)
				continue;
			L.trace("Iterating on size {} attrs concept", x);
			collected.put(x, new HashSet<Concept>());
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
					collected.get(x).add(modified);
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
							if (c.attributes().containsAll(intersection)) {
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

						if (!collected.containsKey(newConcept.attributes().size())) {
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
											lattice.addParents(newConcept, ha);
										}
									}
								}
							}
						}

						// If the intersection is actually the same as the set
						// of attributes of the input object
						// then exit the algorithm
						if (intersection.containsAll(Arrays.asList(attributes))) {
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
