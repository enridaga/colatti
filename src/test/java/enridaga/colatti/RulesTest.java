package enridaga.colatti;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesTest {

	private static final Logger l = LoggerFactory.getLogger(RulesTest.class);

	@Rule
	public TestName name = new TestName();

	private Rules test(Lattice lattice, Object[] inHead, Object[] inBody) throws ColattiException {
		l.info("Testing: {} {}", inHead, inBody);
		Rules rules = new Rules(lattice);
		enridaga.colatti.Rule[] r = rules.rules(inHead, inBody);
		for (enridaga.colatti.Rule x : r) {
			l.trace("Generated {}", x);
		}
		return rules;
	}

	@Test
	public void test() throws ColattiException {
		Lattice lattice = new LatticeInMemory();
		InsertObject insert = new InsertObject(lattice);
		insert.perform("A", 1, 2, 3, 4, 'a');
		insert.perform("B", 2, 3, 4, 5, 'a');
		insert.perform("C", 3, 4, 5, 6, 'a','b','c');
		insert.perform("D", 7, 8, 9, 10, 'b');
		insert.perform("E", 11, 12, 13, 14, 15, 'c');
		insert.perform("F", 10, 11, 12, 16, 17, 'd');
		insert.perform("G", 16, 17, 18, 19, 20, 'c');
		insert.perform("H", 1, 2, 3, 4, 5, 6, 'e');
		insert.perform("I", 3, 4, 5, 6, 7, 8, 'f');
		Object[] vocabulary = objects('a', 'b', 'c', 'd', 'e', 'f');
//		test(lattice, vocabulary, objects(2, 3, 7, 8, 9));
		test(lattice, vocabulary, objects(2, 3));
//		test(lattice, vocabulary, objects(7, 8, 9));
	}

	private static Object[] objects(Object... objects) {
		return objects;
	}
}
