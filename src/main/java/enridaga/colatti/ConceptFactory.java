package enridaga.colatti;

import java.util.Collection;

public interface ConceptFactory {

	Concept make(Collection<Object> objects, Collection<Object> attributes);

	Concept make(Object[] objects, Object[] attributes);

	Concept empty();

	Concept makeFromSingleObject(Object object, Object... attributes);

	Concept makeAddAttributes(Concept concept, Object... attributesToAdd);

	Concept makeAddObject(Concept concept, Object object);

	Concept makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin);

}