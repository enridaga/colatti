package enridaga.colatti;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.Colatti.Concept;

public class ConceptComparatorTest {

	private final static Logger log = LoggerFactory.getLogger(ConceptComparatorTest.class);

	@Rule
	public TestName name = new TestName();

	@Test
	public void sort() throws Exception {
		log.debug("{}", name.getMethodName());
		List<Concept> _concepts;
		_concepts = new ArrayList<Concept>();
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

		if (log.isDebugEnabled()) {
			log.debug("Unordered:");
			for (Concept c : _concepts) {
				log.debug(" - {}", c);
			}
		}
		// Sort
		_concepts.sort(Colatti.attributesAscSorter);

		// Test
		Assert.assertTrue(_concepts.indexOf(_1) == 0);
		Assert.assertTrue(_concepts.indexOf(_2) == 1);
		Assert.assertTrue(_concepts.indexOf(_3) == 2);
		Assert.assertTrue(_concepts.indexOf(_4) == 3);
		Assert.assertTrue(_concepts.indexOf(_5) == 4);

		if (log.isDebugEnabled()) {
			log.debug("Ordered:");
			for (Concept c : _concepts) {
				log.debug(" - {}", c);
			}
		}
	}
}