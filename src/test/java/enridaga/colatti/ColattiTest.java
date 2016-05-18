package enridaga.colatti;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		boolean ret = colatti.addObject("A", "a", "b", "c");
		Assert.assertTrue(ret);
		ret = colatti.addObject("A", "a", "b", "c");
		Assert.assertTrue(!ret);
	}

	@Test
	public void testWithTwoObjects() throws Exception {
		Colatti colatti = new Colatti();
		Assert.assertTrue(colatti.addObject("A", "a", "b", "c"));
		Assert.assertTrue(colatti.addObject("B", "b", "c", "d"));
		log.debug("Supremum: {}", colatti.lattice().supremum());
		log.debug("Infimum: {}", colatti.lattice().infimum());
		Assert.assertTrue(colatti.lattice().concepts().size() == 4);

		Concept supremumTest = Concept.make(new String[] { "A", "B" }, new String[] { "b", "c" });
		Concept infimumTest = Concept.make(new String[] {}, new String[] { "a", "b", "c", "d" });

		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		for (Concept c : colatti.lattice().concepts()) {
			log.debug(" > {} :: {} / {}",
					new Object[] { c, colatti.lattice().parents(c), colatti.lattice().children(c) });
		}
	}

	@Test
	public void testWithThreeObjects() throws Exception {
		Colatti colatti = new Colatti();
		Assert.assertTrue(colatti.addObject("A", "a", "b", "c"));
		Assert.assertTrue(colatti.addObject("B", "b", "c", "d"));
		Assert.assertTrue(colatti.addObject("D", "e", "f"));
		Assert.assertTrue(colatti.lattice().concepts().size() == 6);

		if (log.isDebugEnabled()) {
			log.debug("Supremum: {}", colatti.lattice().supremum());
			log.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = Concept.make(new String[] { "D", "A", "B" }, new String[] {});
		Concept infimumTest = Concept.make(new String[] {}, new String[] { "e", "f", "a", "b", "c", "d" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (log.isDebugEnabled())
			for (Concept c : colatti.lattice().concepts()) {
				log.debug(" > {}", c);
			}
	}

	@Test
	public void testAddExistingObjectDoesNotChangeLattice() throws Exception {
		Colatti colatti = new Colatti();
		Assert.assertTrue(colatti.addObject("A", "a", "b", "c"));
		Assert.assertTrue(colatti.addObject("B", "b", "c", "d"));
		Assert.assertTrue(colatti.addObject("D", "e", "f"));
		log.info("{}",colatti.lattice().concepts());
		Assert.assertTrue(colatti.lattice().concepts().size() == 6);

		if (log.isDebugEnabled()) {
			log.debug("Supremum: {}", colatti.lattice().supremum());
			log.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = Concept.make(new String[] { "D", "A", "B" }, new String[] {});
		Concept infimumTest = Concept.make(new String[] {}, new String[] { "e", "f", "a", "b", "c", "d" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (log.isDebugEnabled())
			for (Concept c : colatti.lattice().concepts()) {
				log.debug(" << {}", c);
			}

		boolean affects = colatti.addObject("A", "a", "b", "c");

		if (log.isDebugEnabled())
			for (Concept c : colatti.lattice().concepts()) {
				log.debug(" >> {}", c);
			}

		Assert.assertFalse(affects);
	}

	/**
	 * This test cannot work as the algorithm does not accept the same object to
	 * be added more then once with different attribute set.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testAddObjectTwiceData() throws Exception {
		/*
		 * This is a negative proof of the fact that only new objects can be
		 * specified.
		 */
		Colatti colatti = new Colatti();
		Assert.assertTrue(colatti.addObject("A", "a", "b", "c"));
		Assert.assertTrue(colatti.addObject("B", "b", "c", "d"));
		Assert.assertTrue(colatti.addObject("D", "e", "f"));

		Colatti colatti2 = new Colatti();
		Assert.assertTrue(colatti2.addObject("A", "a"));
		Assert.assertTrue(colatti2.addObject("B", "b", "c", "d"));
		Assert.assertTrue(colatti2.addObject("D", "f"));
		Assert.assertTrue(colatti2.addObject("D", "e"));
		Assert.assertTrue(colatti2.addObject("A", "b"));
		Assert.assertTrue(colatti2.addObject("A", "c"));
		Assert.assertTrue(colatti2.lattice().concepts().size() == colatti.lattice().concepts().size());
		Assert.assertTrue(colatti.lattice().concepts().equals(colatti2.lattice().concepts()));
	}

	@Test
	public void test1000RandomObjects() throws ColattiException {
		Colatti colatti = new Colatti();
		String attributes = "abcdefghijklmnopqrstuvwxyz0987654321";
		Random rand = new Random();
		int min = 1;
		int max = 1000;
		for (int x = min; x < max; x++) {
			Object object = x;
			Set<Object> attrs = new HashSet<Object>();
			int attrsize = rand.nextInt(attributes.length());
			while (attrsize >= 0) {
				attrsize--;
				int a = attributes.charAt(rand.nextInt(attributes.length()));
				attrs.add(a);
			}
			log.trace(" - {} {}", object, attrs);
			colatti.addObject(object, attrs);
		}
		log.debug("Added 1000 objects");
		log.debug("Concepts: {}", colatti.lattice().concepts().size());
	}
}
