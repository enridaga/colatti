package enridaga.colatti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

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
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		boolean ret = colatti.perform("A", "a", "b", "c");
		Assert.assertTrue(ret);
		ret = colatti.perform("A", "a", "b", "c");
		Assert.assertTrue(!ret);
	}

	@Test
	public void testWithTwoObjects() throws Exception {
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		log.debug("Supremum: {}", colatti.lattice().supremum());
		log.debug("Infimum: {}", colatti.lattice().infimum());
		Assert.assertTrue(colatti.lattice().size() == 4);

		Concept supremumTest = new ConceptInMemory(new String[] { "A", "B" }, new String[] { "b", "c" });
		Concept infimumTest = new ConceptInMemory(new String[] {}, new String[] { "a", "b", "c", "d" });

		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));

		List<Concept> layer = new ArrayList<Concept>();
		layer.add(lattice.infimum());
		while (!layer.isEmpty()) {
			Concept current = layer.remove(0);
			log.debug(" > {} :: {} / {}",
					new Object[] { current, colatti.lattice().parents(current), colatti.lattice().children(current) });
			for (Concept c : colatti.lattice().parents(current)) {
				if (!layer.contains(c)) {
					layer.add(c);
				}
			}
		}
	}

	@Test
	public void testWithThreeObjects() throws Exception {
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti.perform("D", "e", "f"));
		Assert.assertTrue(colatti.lattice().size() == 6);

		if (log.isDebugEnabled()) {
			log.debug("Supremum: {}", colatti.lattice().supremum());
			log.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = new ConceptInMemory(new String[] { "D", "A", "B" }, new String[] {});
		Concept infimumTest = new ConceptInMemory(new String[] {}, new String[] { "e", "f", "a", "b", "c", "d" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (log.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);
	}
	
	private static final Function<Concept,Boolean> debugConcepts = new Function<Concept, Boolean>() {
		@Override
		public Boolean apply(Concept c) {
			log.debug("> {}", c);
			return true;
		};
	};

	@Test
	public void testAddExistingObjectDoesNotChangeLattice() throws Exception {
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti.perform("D", "e", "f"));
		// log.info("{}",colatti.lattice().concepts());
		Assert.assertTrue(colatti.lattice().size() == 6);

		if (log.isDebugEnabled()) {
			log.debug("Supremum: {}", colatti.lattice().supremum());
			log.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = new ConceptInMemory(new String[] { "D", "A", "B" }, new String[] {});
		Concept infimumTest = new ConceptInMemory(new String[] {}, new String[] { "e", "f", "a", "b", "c", "d" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (log.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);

		boolean affects = colatti.perform("A", "a", "b", "c");

		if (log.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);

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
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti.perform("D", "e", "f"));

		Lattice lattice2 = new LatticeInMemory();
		InsertObject colatti2 = new InsertObject(lattice2);
		Assert.assertTrue(colatti2.perform("A", "a"));
		Assert.assertTrue(colatti2.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti2.perform("D", "f"));
		Assert.assertTrue(colatti2.perform("D", "e"));
		Assert.assertTrue(colatti2.perform("A", "b"));
		Assert.assertTrue(colatti2.perform("A", "c"));
		Assert.assertTrue(colatti2.lattice().size() == colatti.lattice().size());
	}

	@Test
	public void test1000RandomObjects() throws ColattiException {
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
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
			colatti.perform(object, attrs);
		}
		log.debug("Added 1000 objects");
		log.debug("Concepts: {}", colatti.lattice().size());
	}
}
