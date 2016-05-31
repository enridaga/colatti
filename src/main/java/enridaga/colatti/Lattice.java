package enridaga.colatti;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 
 * @author enridaga
 *
 */
public interface Lattice {

	// List<Concept> concepts();

	Concept supremum() throws ColattiException;

	Concept infimum() throws ColattiException;

	Set<Concept> parents(Concept concept) throws ColattiException;

	Set<Concept> children(Concept concept) throws ColattiException;

	boolean add(Concept concept) throws ColattiException;

	/**
	 * This method adjust children relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addParents(Concept concept, Concept... parents) throws ColattiException;

	/**
	 * This method adjust parents relationships as well.
	 * 
	 * @param concept
	 * @param parents
	 */
	void addChildren(Concept concept, Concept... children) throws ColattiException;

	boolean isParentOf(Concept concept, Concept parent) throws ColattiException;

	/**
	 * This method also adjust the inverse child relation.
	 * 
	 * @param concept
	 * @param parent
	 * @return boolean - if the change was effective
	 * @throws ColattiException 
	 */
	boolean removeParent(Concept concept, Concept parent) throws ColattiException;

	boolean removeChild(Concept concept, Concept child) throws ColattiException;

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

	int maxAttributeCardinality() throws ColattiException;

	// Map<Integer, Set<Concept>> attributesSizeIndex();

	public ConceptFactory getConceptFactory();

	Set<Concept> getConceptsWithAttributesSize(int x) throws ColattiException;

	int size() throws ColattiException;

	default void bottomUp(Function<Concept, Boolean> function) throws ColattiException {
		List<Concept> stack = new ArrayList<Concept>();
		stack.add(this.infimum());
		while (!stack.isEmpty()) {
			Concept current = stack.remove(0);
			boolean follows = function.apply(current);
			if (follows) {
				for (Concept c : parents(current)) {
					if (!stack.contains(c)) {
						stack.add(c);
					}
				}
			}
		}
	}

	default void topDown(Function<Concept, Boolean> function) throws ColattiException {
		List<Concept> stack = new ArrayList<Concept>();
		stack.add(this.supremum());
		while (!stack.isEmpty()) {
			Concept current = stack.remove(0);
			boolean follows = function.apply(current);
			if (follows) {
				for (Concept c : children(current)) {
					if (!stack.contains(c)) {
						stack.add(c);
					}
				}
			}
		}
	}

}