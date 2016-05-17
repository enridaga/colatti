package enridaga.colatti;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColattiTest {
	private final static Logger log = LoggerFactory.getLogger(ColattiTest.class);

	@Rule
	public TestName name = new TestName();

	@Test
	public void addReturnBooleanCorrectly() throws Exception {
		log.debug("{}", name.getMethodName());
		Colatti colatti = new Colatti();
		boolean ret = colatti.add("A", "a", "b", "c");
		Assert.assertTrue(ret);
		ret = colatti.add("A", "a", "b", "c");
		Assert.assertTrue(!ret);
	}
}
