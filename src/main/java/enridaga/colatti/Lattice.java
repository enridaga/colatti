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

	List<Concept> concepts();

	Concept supremum();

	Concept infimum();

	Set<Concept> parents(Concept concept);

	Set<Concept> children(Concept concept);

	boolean add(Concept concept);

	/**
	 * This method adjust children relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addParents(Concept concept, Concept... parents);

	/**
	 * This method adjust parents relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addChildren(Concept concept, Concept... children);

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
	void replace(Concept that, Concept with) throws ColattiException;

	int maxAttributeCardinality();

	Map<Integer, Set<Concept>> attributesSizeIndex();

	public ConceptFactory getConceptFactory();
}