package io.vepo.tutorial.antlr4.parser;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

import java.util.Deque;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.tutorial.antlr4.Assertion;
import io.vepo.tutorial.antlr4.Step;
import io.vepo.tutorial.antlr4.Step.StepBuilder;
import io.vepo.tutorial.antlr4.Suite;
import io.vepo.tutorial.antlr4.Suite.SuiteBuilder;
import io.vepo.tutorial.antlr4.generated.TestSuiteListener;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser.AssertionContext;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser.AttributeContext;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser.StepContext;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser.SuiteContext;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser.ValueContext;

public class SuiteCreator implements TestSuiteListener {
	private static final Logger logger = LoggerFactory.getLogger(SuiteCreator.class);
	private SuiteBuilder mainSuite;
	private Deque<SuiteBuilder> suiteQueue;
	private StepBuilder currentStepBuilder;

	public SuiteCreator() {
		mainSuite = null;
		suiteQueue = new LinkedList<>();
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		logger.debug("Visit Terminal: {}", node);
	}

	@Override
	public void visitErrorNode(ErrorNode node) {
		logger.debug("Visit Error Node: {}", node);
	}

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		logger.debug("Enter Every Rule: {}", ctx);
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		logger.debug("Exit Every Rule: {}", ctx);
	}

	@Override
	public void enterSuite(SuiteContext ctx) {
		logger.debug("Enter Suite: {}", ctx);

		int nextIndex;
		if (suiteQueue.isEmpty()) {
			nextIndex = 0;
		} else {
			nextIndex = suiteQueue.peekLast().nextIndex();
		}
		logger.info("Creating suite: " + ctx.IDENTIFIER().getText() + " lastIndex=" + nextIndex);
		suiteQueue.addLast(Suite.builder().index(nextIndex).name(ctx.IDENTIFIER().getText()));

		if (isNull(mainSuite)) {
			mainSuite = suiteQueue.peekLast();
		}
	}

	@Override
	public void exitSuite(SuiteContext ctx) {
		logger.debug("Exit Suite: {}", ctx);

		Suite builtSuite = suiteQueue.pollLast().build();

		if (!suiteQueue.isEmpty()) {
			suiteQueue.peekLast().suite(builtSuite);
		}
	}

	public Suite getTestSuite() {
		return mainSuite.build();
	}

	@Override
	public void enterStep(StepContext ctx) {
		logger.debug("Enter Step: {}", ctx);

		int lastIndex = suiteQueue.peekLast().nextIndex();
		if (ctx.IDENTIFIER().size() == 2) {
			logger.debug("Creating step: " + ctx.IDENTIFIER(1) + " lastIndex=" + lastIndex);
			currentStepBuilder = Step.builder().index(lastIndex).plugin(ctx.IDENTIFIER(0).getText())
					.name(ctx.IDENTIFIER(1).getText());
		} else {
			logger.warn("Could not intantiate Step: context={}", ctx);
		}
	}

	@Override
	public void exitStep(StepContext ctx) {
		logger.debug("Exit Step: {}", ctx);
		suiteQueue.peekLast().step(currentStepBuilder.build());
		currentStepBuilder = null;
	}

	@Override
	public void enterAttribute(AttributeContext ctx) {
		logger.debug("Enter Attribute: {}", ctx);
	}

	@Override
	public void exitAttribute(AttributeContext ctx) {
		logger.debug("Exit Attribute: {}", ctx);

		if (nonNull(ctx.value().STRING())) {
			currentStepBuilder.attribute(ctx.IDENTIFIER().getText(), processString(ctx.value().getText()));
		} else if (nonNull(ctx.value().NUMBER())) {
			currentStepBuilder.attribute(ctx.IDENTIFIER().getText(), Integer.valueOf((ctx.value().getText())));
		} else {
			logger.warn("Invalid value! ctx={}", ctx);
		}
	}

	private String processString(String text) {
		return unescapeJava(text.substring(1, text.length() - 1));
	}

	@Override
	public void enterValue(ValueContext ctx) {
		logger.debug("Enter Value: {}", ctx);
	}

	@Override
	public void exitValue(ValueContext ctx) {
		logger.debug("Exit Value: {}", ctx);
	}

	@Override
	public void enterAssertion(AssertionContext ctx) {
		logger.debug("Enter Assertion: {}", ctx);
	}

	@Override
	public void exitAssertion(AssertionContext ctx) {
		logger.debug("Enter Assertion: {}", ctx);

		if (nonNull(ctx.value().STRING())) {
			currentStepBuilder.assertion(new Assertion<>(ctx.IDENTIFIER().getText(), ctx.VERB().getText(),
					processString(ctx.value().getText())));
		} else if (nonNull(ctx.value().NUMBER())) {
			currentStepBuilder.assertion(new Assertion<>(ctx.IDENTIFIER().getText(), ctx.VERB().getText(),
					Integer.valueOf((ctx.value().getText()))));
		} else {
			logger.warn("Invalid value! ctx={}", ctx);
		}
	}

}
