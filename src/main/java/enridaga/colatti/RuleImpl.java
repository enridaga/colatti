package enridaga.colatti;

class RuleImpl implements Rule {
	private Object[] body;
	private Object[] head;
	private double confidence;
	private double support;
	private double relativeConfidence;

	public void body(Object[] body) {
		this.body = body;
	}

	public void head(Object[] head) {
		this.head = head;
	}

	public void confidence(double confidence) {
		this.confidence = confidence;
	}

	public void support(double support) {
		this.support = support;
	}

	public void relativeConfidence(double confidence) {
		this.relativeConfidence = confidence;
	}

	@Override
	public Object[] body() {
		return body;
	}

	@Override
	public Object[] head() {
		return head;
	}

	@Override
	public double confidence() {
		return confidence;
	}

	@Override
	public double support() {
		return support;
	}

	@Override
	public double relativeConfidence() {
		return relativeConfidence;
	}

	@Override
	public String toString() {
		StringBuilder ts = new StringBuilder();
		boolean first = true;
		for (Object o : head) {
			if (first) {
				first = false;
			} else {
				ts.append(',');
			}
			ts.append(o);
		}
		ts.append("<-");
		first = true;
		for (Object o : body) {
			if (first) {
				first = false;
			} else {
				ts.append(',');
			}
			ts.append(o);
		}
		ts.append(' ');
		// measures
		ts.append('(');
		ts.append(support);
		ts.append(',');
		ts.append(confidence);
		ts.append(',');
		ts.append(relativeConfidence);
		ts.append(')');
		return ts.toString();
	}
}