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
	private TreeSet<ConceptInMemory> _concepts;
	private Map<ConceptInMemory, Set<ConceptInMemory>> _parents;
	private Map<ConceptInMemory, Set<ConceptInMemory>> _children;
	private Map<Integer, Set<ConceptInMemory>> attributeSizeIndex = new HashMap<Integer, Set<ConceptInMemory>>();
	private ConceptFactory factory = new ConceptFactory();

	public LatticeInMemory() {
		_concepts = new TreeSet<ConceptInMemory>(new AttributesAscSorter());
		_parents = new HashMap<ConceptInMemory, Set<ConceptInMemory>>();
		_children = new HashMap<ConceptInMemory, Set<ConceptInMemory>>();
		// Initialization with an empty concept
		ConceptInMemory empty = new ConceptInMemory();
		add(empty);
		setParents(empty);
		setChildren(empty);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#concepts()
	 */
	public List<ConceptInMemory> concepts() {
		return Collections.unmodifiableList(Arrays.asList(_concepts.toArray(new ConceptInMemory[_concepts.size()])));
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
	public ConceptInMemory infimum() {
		return _concepts.last();
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#parents(enridaga.colatti.ConceptInMemory)
	 */
	public Set<ConceptInMemory> parents(Concept concept) {
		if (_parents.get(concept) == null){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(_parents.get(concept));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#children(enridaga.colatti.ConceptInMemory)
	 */
	public Set<ConceptInMemory> children(Concept concept) {
		if (_children.get(concept) == null){
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(_children.get(concept));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#add(enridaga.colatti.ConceptInMemory)
	 */
	public boolean add(ConceptInMemory concept) {
		boolean ret = _concepts.add(concept);
		if (!_parents.containsKey(concept)) {
			_parents.put(concept, new HashSet<ConceptInMemory>());
		}
		if (!_children.containsKey(concept)) {
			_children.put(concept, new HashSet<ConceptInMemory>());
		}
		if (!attributeSizeIndex.containsKey(concept.attributes().size())) {
			attributeSizeIndex.put(concept.attributes().size(), new HashSet<ConceptInMemory>());
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
	private void setParents(ConceptInMemory concept, ConceptInMemory... parents) {
		_parents.put(concept, new HashSet<ConceptInMemory>(Arrays.asList(parents)));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#addParents(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
	 */
	public void addParents(ConceptInMemory concept, ConceptInMemory... parents) {
		if (!_parents.containsKey(concept)) {
			_parents.put(concept, new HashSet<ConceptInMemory>());
		}
		_parents.get(concept).addAll(Arrays.asList(parents));
		for (ConceptInMemory p : parents) {
			if (!_children.containsKey(p)) {
				_children.put(p, new HashSet<ConceptInMemory>());
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
	private void setChildren(ConceptInMemory concept, ConceptInMemory... children) {
		_children.put(concept, new HashSet<ConceptInMemory>(Arrays.asList(children)));
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#addChildren(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
	 */
	public void addChildren(ConceptInMemory concept, ConceptInMemory... children) {
		if (!_children.containsKey(concept)) {
			_children.put(concept, new HashSet<ConceptInMemory>());
		}
		_children.get(concept).addAll(Arrays.asList(children));
		for (ConceptInMemory c : children) {
			if (!_parents.containsKey(c)) {
				_parents.put(c, new HashSet<ConceptInMemory>());
			}
			_parents.get(c).add(concept);
		}
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#isParentOf(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
	 */
	public boolean isParentOf(Concept concept, Concept parent) {
		if (!_parents.containsKey(concept))
			return false;
		return _parents.get(concept).contains(parent);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#removeParent(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
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
	 * @see enridaga.colatti.Lattice#removeChild(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
	 */
	public boolean removeChild(Concept concept, Concept child) {
		return removeParent(child, concept);
	}

	/* (non-Javadoc)
	 * @see enridaga.colatti.Lattice#replace(enridaga.colatti.ConceptInMemory, enridaga.colatti.ConceptInMemory)
	 */
	public void replace(Concept that, ConceptInMemory with) throws ColattiException {
		if (!_concepts.contains(that)) {
			throw new ColattiException("'that' concept is not in the list");
		}
		_concepts.remove(that);
		add(with);
		Set<ConceptInMemory> ppp = _parents.get(that);
		_parents.remove(that);
		_parents.put(with, ppp);
		Set<ConceptInMemory> ccc = _children.get(that);
		_children.remove(that);
		_children.put(with, ccc);

		if (attributeSizeIndex.get(that.attributes().size()) != null)
			attributeSizeIndex.get(that.attributes().size()).remove(that);

		if (!attributeSizeIndex.containsKey(with.attributes().size())) {
			attributeSizeIndex.put(with.attributes().size(), new HashSet<ConceptInMemory>());
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
	public Map<Integer, Set<ConceptInMemory>> attributesSizeIndex() {
		return Collections.unmodifiableMap(attributeSizeIndex);
	}
	
	public ConceptFactory getConceptFactory(){
		return factory;
	}
}
