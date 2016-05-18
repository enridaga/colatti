package enridaga.colatti;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.Colatti.Concept;

public class ColattiTest {
	private final static Logger log = LoggerFactory.getLogger(ColattiTest.class);

	@Rule
	public TestName name = new TestName();

	@Before
	public void before() {
		log.info("{}", name.getMethodName());
	}

	@Test
	public void addReturnBooleanCorrectly() throws Exception {
		Colatti colatti = new Colatti();
		boolean ret = colatti.add("A", "a", "b", "c");
		Assert.assertTrue(ret);
		ret = colatti.add("A", "a", "b", "c");
		Assert.assertTrue(!ret);
	}

	@Test
	public void testWithTwoObjects() throws Exception {
		Colatti colatti = new Colatti();
		Assert.assertTrue(colatti.add("A", "a", "b", "c"));
		Assert.assertTrue(colatti.add("B", "b", "c", "d"));
		log.debug("Supremum: {}", colatti.lattice().supremum());
		log.debug("Infimum: {}", colatti.lattice().infimum());
		Assert.assertTrue(colatti.lattice().concepts().size() == 4);

		Concept supremumTest = Concept.make(new String[] { "A", "B" }, new String[] { "b", "c" });
		Concept infimumTest = Concept.make(new String[] {}, new String[] { "a", "b", "c", "d" });

		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		for (Concept c : colatti.lattice().concepts()) {
			log.debug(" > {}", c);
		}
	}
}
