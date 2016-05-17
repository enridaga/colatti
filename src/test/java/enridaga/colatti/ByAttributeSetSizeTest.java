package enridaga.colatti;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.Colatti.ByAttributeSetSize;
import enridaga.colatti.Colatti.Concept;

public class ByAttributeSetSizeTest {
	private final static Logger log = LoggerFactory.getLogger(ByAttributeSetSizeTest.class);

	@Rule
	public TestName name = new TestName();

	@Test
	public void test() {
		Set<Concept> _concepts = new TreeSet<Concept>(Colatti.attributesAscSorter);
		Concept _5 = Concept.make(new String[] {}, new String[] { "a", "b", "c", "d" });
		Concept _4 = Concept.make(new String[] { "A" }, new String[] { "a", "b", "c" });
		Concept _3 = Concept.make(new String[] { "C" }, new String[] { "a", "c" });
		Concept _2 = Concept.make(new String[] { "A", "B" }, new String[] { "a", "d" });
		Concept _1 = Concept.make(new String[] { "A", "B", "C" }, new String[] {});

		// Add unordered
		_concepts.add(_4);
		_concepts.add(_5);
		_concepts.add(_2);
		_concepts.add(_3);
		_concepts.add(_1);

		ByAttributeSetSize it = new ByAttributeSetSize(_concepts.iterator());
		while (it.hasNext()) {
			log.debug("{} {}", it.next(), it.attributeCardinality());
		}
	}
}
