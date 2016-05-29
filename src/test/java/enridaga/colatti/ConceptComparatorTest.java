package enridaga.colatti;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.shared.AttributesAscSorter;

public class ConceptComparatorTest {

	private final static Logger log = LoggerFactory.getLogger(ConceptComparatorTest.class);

	@Rule
	public TestName name = new TestName();

	@Before
	public void before() {
		log.info("{}", name.getMethodName());
	}

	@Test
	public void sort() throws Exception {
		List<ConceptInMemory> _concepts;
		_concepts = new ArrayList<ConceptInMemory>();
		ConceptInMemory _5 = new ConceptInMemory(new String[] {}, new String[] { "a", "b", "c", "d" });
		ConceptInMemory _4 = new ConceptInMemory(new String[] { "A" }, new String[] { "a", "b", "c" });
		ConceptInMemory _3 = new ConceptInMemory(new String[] { "C" }, new String[] { "a", "c" });
		ConceptInMemory _2 = new ConceptInMemory(new String[] { "A", "B" }, new String[] { "a", "d" });
		ConceptInMemory _1 = new ConceptInMemory(new String[] { "A", "B", "C" }, new String[] {});

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
		_concepts.sort(new AttributesAscSorter());

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
