package enridaga.colatti;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.shared.AttributesAscSorter;

/**
 * In memory implementation of the Lattice interface.
 * 
 * @author enridaga
 *
 */
public class LatticeInMemory implements Lattice {
	private final Logger log = LoggerFactory.getLogger(LatticeInMemory.class);
	private TreeSet<Concept> _concepts;
	private Map<Concept, Set<Concept>> _parents;
	private Map<Concept, Set<Concept>> _children;
	private Map<Integer, Set<Concept>> attributeSizeIndex = new HashMap<Integer, Set<Concept>>();

	public LatticeInMemory() {
		_concepts = new TreeSet<Concept>(new AttributesAscSorter());
		_parents = new HashMap<Concept, Set<Concept>>();
		_children = new HashMap<Concept, Set<Concept>>();
		// Initialization with an empty concept
		Concept empty = new Concept();
		add(empty);
		setParents(empty);
		setChildren(empty);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#concepts()
	 */
	public List<Concept> concepts() {
		return Collections.unmodifiableList(Arrays.asList(_concepts.toArray(new Concept[_concepts.size()])));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#supremum()
	 */
	public Concept supremum() {
		return _concepts.first();
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#infimum()
	 */
	public Concept infimum() {
		return _concepts.last();
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#parents(enridaga.colatti.Concept)
	 */
	public Set<Concept> parents(Concept concept) {
		return Collections.unmodifiableSet(_parents.get(concept));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#children(enridaga.colatti.Concept)
	 */
	public Set<Concept> children(Concept concept) {
		return Collections.unmodifiableSet(_children.get(concept));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#add(enridaga.colatti.Concept)
	 */
	public boolean add(Concept concept) {
		boolean ret = _concepts.add(concept);
		if (!_parents.containsKey(concept)) {
			_parents.put(concept, new HashSet<Concept>());
		}
		if (!_children.containsKey(concept)) {
			_children.put(concept, new HashSet<Concept>());
		}
		if (!attributeSizeIndex.containsKey(concept.attributes().size())) {
			attributeSizeIndex.put(concept.attributes().size(), new HashSet<Concept>());
		}
		attributeSizeIndex.get(concept.attributes().size()).add(concept);
		return ret;
	}

	/**
	 * This method does *not* adjust children relationships.
	 * 
	 * @param concept
	 * @param parents
	 */
	private void setParents(Concept concept, Concept... parents) {
		_parents.put(concept, new HashSet<Concept>(Arrays.asList(parents)));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#addParents(enridaga.colatti.Concept, enridaga.colatti.Concept)
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
	private void setChildren(Concept concept, Concept... children) {
		_children.put(concept, new HashSet<Concept>(Arrays.asList(children)));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#addChildren(enridaga.colatti.Concept, enridaga.colatti.Concept)
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

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#isParentOf(enridaga.colatti.Concept, enridaga.colatti.Concept)
	 */
	public boolean isParentOf(Concept concept, Concept parent) {
		if (!_parents.containsKey(concept))
			return false;
		return _parents.get(concept).contains(parent);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#removeParent(enridaga.colatti.Concept, enridaga.colatti.Concept)
	 */
	public boolean removeParent(Concept concept, Concept parent) {
		if (!_parents.containsKey(concept)) {
			return false;
		}
		if (_parents.get(concept).remove(parent)) {
			// XXX If this throws NPE then there is something wrong ...
			if (!_children.get(parent).remove(concept)) {
				// If this is false there is something wrong
				log.warn("Removing a parent was successful. However no related child relation was found.");
			}
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#removeChild(enridaga.colatti.Concept, enridaga.colatti.Concept)
	 */
	public boolean removeChild(Concept concept, Concept child) {
		return removeParent(child, concept);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#replace(enridaga.colatti.Concept, enridaga.colatti.Concept)
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

		if (attributeSizeIndex.get(that.attributes().size()) != null)
			attributeSizeIndex.get(that.attributes().size()).remove(that);

		if (!attributeSizeIndex.containsKey(with.attributes().size())) {
			attributeSizeIndex.put(with.attributes().size(), new HashSet<Concept>());
		}
		attributeSizeIndex.get(with.attributes().size()).add(with);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#maxAttributeCardinality()
	 */
	public int maxAttributeCardinality() {
		return infimum().attributes().size();
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#attributesSizeIndex()
	 */
	public Map<Integer, Set<Concept>> attributesSizeIndex() {
		return Collections.unmodifiableMap(attributeSizeIndex);
	}
}
