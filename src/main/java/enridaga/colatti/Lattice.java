package enridaga.colatti;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author enridaga
 *
 */
public interface Lattice {

	List<ConceptInMemory> concepts();

	Concept supremum();

	ConceptInMemory infimum();

	Set<ConceptInMemory> parents(Concept concept);

	Set<ConceptInMemory> children(Concept concept);

	boolean add(ConceptInMemory concept);

	/**
	 * This method adjust children relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addParents(ConceptInMemory concept, ConceptInMemory... parents);

	/**
	 * This method adjust parents relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addChildren(ConceptInMemory concept, ConceptInMemory... children);

	boolean isParentOf(Concept concept, Concept parent);

	/**
	 * This method also adjust the inverse child relation.
	 * 
	 * @param concept
	 * @param parent
	 * @return boolean - if the change was effective
	 */
	boolean removeParent(Concept concept, Concept parent);

	boolean removeChild(Concept concept, Concept child);

	/**
	 * Remove all occurrences of 'that' and replace them with occurrences of
	 * 'with'. This method does not adjust the coherence of parent/child
	 * relationships. These are inherited as they were.
	 * 
	 * @param that
	 * @param with
	 * @throws ColattiException
	 */
	void replace(Concept that, ConceptInMemory with) throws ColattiException;

	int maxAttributeCardinality();

	Map<Integer, Set<ConceptInMemory>> attributesSizeIndex();

	public ConceptFactory getConceptFactory();
}