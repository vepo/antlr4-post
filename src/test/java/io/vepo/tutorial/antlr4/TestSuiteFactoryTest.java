package io.vepo.tutorial.antlr4;

import static io.vepo.tutorial.antlr4.TestSuiteFactory.parseSuite;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Suite Factory Tests")
public class TestSuiteFactoryTest {

	@Test
	@DisplayName("It SHOULD accept an empty Suite")
	public void emptySuiteTest() {
		assertEquals(Suite.builder().index(0).name("MyFirstSuite").build(), parseSuite("Suite MyFirstSuite {}"));
	}

	@Test
	@DisplayName("It SHOULD accept a Suite with a Step")
	public void suiteWithStepTest() {
		assertEquals(Suite.builder().index(0).name("MyFirstSuite")
				.step(Step.builder().index(0).name("MyFirstStep").plugin("Command").attribute("attribute", "value")
						.assertion(new Assertion<>("response", "Contains", "SOME RESPONSE CODE"))
						.assertion(new Assertion<>("executionTime", "Equals", 100)).build())
				.build(),

				parseSuite("Suite MyFirstSuite {\n" + //
						"    Command MyFirstStep {\n" + //
						"       attribute: \"value\"\n" + //
						"       assert response Contains \"SOME RESPONSE CODE\"\n" + //
						"       assert executionTime Equals 100\n" + //
						"    }\n" + //
						"}"));
	}

	@Test
	@DisplayName("It SHOULD accept inner Suites with inner Steps")
	public void innerSuiteWithStepTest() {
		assertEquals(
				Suite.builder().index(0).name("MyFirstSuite")
						.step(Step.builder().index(0).name("MyFirstStep").plugin("Command")
								.attribute("attribute", "value")
								.assertion(new Assertion<>("response", "Contains", "SOME RESPONSE CODE"))
								.assertion(new Assertion<>("executionTime", "Equals", 100)).build())
						.suite(Suite.builder().index(1).name("SomeInnerSuite")
								.step(Step.builder().index(0).name("SomeInnerStep").plugin("Command")
										.attribute("innerAttribute", "inner value")
										.attribute("otherInnerAttribute", 500).build())
								.build())
						.build(),

				parseSuite("Suite MyFirstSuite {\n" + //
						"    Command MyFirstStep {\n" + //
						"        attribute: \"value\"\n" + //
						"        assert response Contains \"SOME RESPONSE CODE\"\n" + //
						"        assert executionTime Equals 100\n" + //
						"    }\n" + //
						"    Suite SomeInnerSuite {\n" + //
						"        Command SomeInnerStep {\n" + //
						"            innerAttribute: \"inner value\"\n" + //
						"            otherInnerAttribute: 500\n" + //
						"        }\n" + //
						"    }\n" + //
						"}"));
	}

	@Test
	@DisplayName("It SHOULD accept order in elements")
	public void orderTest() {
		Suite suite = Suite.builder().index(0).name("MyFirstSuite")
				.step(Step.builder().index(0).name("Step1").plugin("Command").build())
				.step(Step.builder().index(1).name("Step2").plugin("Command").build())
				.suite(Suite.builder().index(2).name("Suite3")
						.step(Step.builder().index(0).name("Step3.1").plugin("Command").build()).build())
				.step(Step.builder().index(3).name("Step4").plugin("Command").build()).build();
		assertEquals(suite, parseSuite("Suite MyFirstSuite {\n" + //
				"    Command Step1 {\n" + //
				"    }\n" + //
				"    Command Step2 {\n" + //
				"    }\n" + //
				"    Suite Suite3 {\n" + //
				"        Command Step3.1 {\n" + //
				"        }\n" + //
				"    }\n" + //
				"    Command Step4 {\n" + //
				"    }\n" + //
				"}"));
		AtomicInteger counter = new AtomicInteger(0);
		suite.forEachOrdered(subSuite -> assertEquals(counter.getAndIncrement(), subSuite.getIndex()),
				step -> assertEquals(counter.getAndIncrement(), step.getIndex()));
	}
}
