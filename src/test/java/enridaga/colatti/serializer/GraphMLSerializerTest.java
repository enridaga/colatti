package enridaga.colatti.serializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.ColattiException;
import enridaga.colatti.InsertObject;
import enridaga.colatti.Lattice;
import enridaga.colatti.LatticeInMemory;

public class GraphMLSerializerTest {
	private final static Logger log = LoggerFactory.getLogger(GraphMLSerializerTest.class);

	@Rule
	public TestName name = new TestName();

	@Before
	public void before() {
		log.info("{}", name.getMethodName());
	}

	@Test
	public void test() throws ColattiException {
		Lattice lattice = new LatticeInMemory();
		GraphmlSerializer ser = new GraphmlSerializer();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		log.info("{}",ser.serialize(lattice));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		log.info("{}",ser.serialize(lattice));
		Assert.assertTrue(colatti.perform("C", "e", "f", "g"));
		log.info("{}",ser.serialize(lattice));
	}
}
