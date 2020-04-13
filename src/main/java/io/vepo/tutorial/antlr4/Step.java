package io.vepo.tutorial.antlr4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Step {
	public static class StepBuilder {
		private int index;
		private String plugin;
		private String name;
		private Map<String, Object> attributes;
		private List<Assertion<?>> assertions;

		private StepBuilder() {
			attributes = new HashMap<>();
			assertions = new ArrayList<>();
		}

		public StepBuilder index(int index) {
			this.index = index;
			return this;
		}

		public StepBuilder plugin(String plugin) {
			this.plugin = plugin;
			return this;
		}

		public StepBuilder name(String name) {
			this.name = name;
			return this;
		}

		public StepBuilder attribute(String key, Object value) {
			attributes.put(key, value);
			return this;
		}

		public StepBuilder assertion(Assertion<?> assertion) {
			assertions.add(assertion);
			return this;
		}

		public Step build() {
			return new Step(this);
		}

	}

	public static final StepBuilder builder() {
		return new StepBuilder();
	}

	private final int index;
	private final String plugin;
	private final String name;
	private final Map<String, Object> attributes;
	private final List<Assertion<?>> assertions;

	private Step(StepBuilder builder) {
		index = builder.index;
		plugin = builder.plugin;
		name = builder.name;
		attributes = builder.attributes;
		assertions = builder.assertions;
	}

	public int getIndex() {
		return index;
	}

	public String getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public List<Assertion<?>> getAssertions() {
		return assertions;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(assertions).append(attributes).append(index).append(name).append(plugin)
				.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Step other = (Step) obj;
		return new EqualsBuilder().append(assertions, other.assertions).append(attributes, other.attributes)
				.append(index, other.index).append(name, other.name).append(plugin, other.plugin).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("index", index).append("name", name)
				.append("plugin", plugin).append("attributes", attributes).append("assertions", assertions).toString();
	}

}