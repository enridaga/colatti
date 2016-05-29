package enridaga.colatti;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.shared.ByAttributeSetSize;

public class ByAttributeSetSizeTest {
	private final static Logger log = LoggerFactory.getLogger(ByAttributeSetSizeTest.class);

	@Rule
	public TestName name = new TestName();

	@Before
	public void before() {
		log.info("{}", name.getMethodName());
	}

	@Test
	public void test() {
		Set<Concept> _concepts = new HashSet<Concept>();
		Concept _5 = new Concept(new String[] {}, new String[] { "a", "b", "c", "d" });
		Concept _4 = new Concept(new String[] { "A" }, new String[] { "a", "b", "c" });
		Concept _3 = new Concept(new String[] { "C" }, new String[] { "a", "c" });
		Concept _2 = new Concept(new String[] { "A", "B" }, new String[] { "a", "d" });
		Concept _1 = new Concept(new String[] { "A", "B", "C" }, new String[] {});

		// Add unordered
		_concepts.add(_4);
		_concepts.add(_5);
		_concepts.add(_2);
		_concepts.add(_3);
		_concepts.add(_1);

		ByAttributeSetSize it = new ByAttributeSetSize(_concepts);
		while (it.hasNext()) {
			log.debug("{} {}", it.next(), it.attributeCardinality());
		}
	}
}
