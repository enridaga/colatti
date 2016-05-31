package enridaga.colatti;

interface Rule {
	Object[] body();

	Object[] head();

	double confidence();

	double support();

	double relativeConfidence();

}