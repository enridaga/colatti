package enridaga.colatti;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptTest {
	private final static Logger log = LoggerFactory.getLogger(Concept.class);

	@Rule
	public TestName name = new TestName();

	@Before
	public void before() {
		log.info("{}", name.getMethodName());
	}

	@Test
	public void hashCodeTest() {
		Concept a = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Concept b = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Assert.assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsTest() {
		Concept a = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Assert.assertTrue(a.equals(a));

		Concept b = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Assert.assertTrue(a.equals(b));
	}

	@Test
	public void orderOfObjectsDoesNotCount() {
		Concept a = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Concept b = new Concept(new Object[] { 3, 1, 2 }, new Object[] { 5, 6, 7 });
		Assert.assertTrue(a.hashCode() == b.hashCode());
		Assert.assertTrue(a.equals(b));
	}

	@Test
	public void orderOfAttributesDoesNotCount() {
		Concept a = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 5, 6, 7 });
		Concept b = new Concept(new Object[] { 1, 2, 3 }, new Object[] { 6, 7, 5 });
		Assert.assertTrue(a.hashCode() == b.hashCode());
		Assert.assertTrue(a.equals(b));
	}
}
