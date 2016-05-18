package enridaga.colatti;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.Colatti.Concept;

public class ConceptTest {
	private final static Logger log = LoggerFactory.getLogger(Concept.class);

	@Test
	public void hashCodeTest() {
		Concept a = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Concept b = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Assert.assertTrue(a.hashCode() == b.hashCode());
	}
	
	@Test
	public void equalsTest() {
		Concept a = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Assert.assertTrue(a.equals(a));
		
		Concept b = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Assert.assertTrue(a.equals(b));
	}

	@Test
	public void orderOfObjectDoesNotCount() {
		Concept a = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Concept b = Concept.make(new Object[]{3,1,2},new Object[]{5,6,7});
		Assert.assertTrue(a.hashCode() == b.hashCode());
		Assert.assertTrue(a.equals(b));
	}

	@Test
	public void orderOfAttributesDoesNotCount() {
		Concept a = Concept.make(new Object[]{1,2,3},new Object[]{5,6,7});
		Concept b = Concept.make(new Object[]{1,2,3},new Object[]{6,7,5});
		Assert.assertTrue(a.hashCode() == b.hashCode());
		Assert.assertTrue(a.equals(b));
	}
}
