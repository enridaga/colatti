package enridaga.colatti;

import java.util.Collection;

public interface ConceptFactory {

	Concept make(Collection<Object> objects, Collection<Object> attributes);

	ConceptInMemory make(Object[] objects, Object[] attributes);

	Concept empty();

	ConceptInMemory makeFromSingleObject(Object object, Object... attributes);

	ConceptInMemory makeAddAttributes(Concept concept, Object... attributesToAdd);

	ConceptInMemory makeAddObject(Concept concept, Object object);

	ConceptInMemory makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin);

}