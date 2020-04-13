package io.vepo.tutorial.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.tutorial.antlr4.generated.TestSuiteLexer;
import io.vepo.tutorial.antlr4.generated.TestSuiteParser;
import io.vepo.tutorial.antlr4.parser.SuiteCreator;

public class TestSuiteFactory {
	private static final Logger logger = LoggerFactory.getLogger(TestSuiteFactory.class);

	public static Suite parseSuite(String contents) {
		logger.debug("Parsing: {}", contents);
		TestSuiteParser parser = new TestSuiteParser(
				new CommonTokenStream(new TestSuiteLexer(CharStreams.fromString(contents))));
		ParseTreeWalker walker = new ParseTreeWalker();
		SuiteCreator creator = new SuiteCreator();
		walker.walk(creator, parser.suite());
		Suite suite = creator.getTestSuite();
		logger.debug("Parsed: {}", suite);
		return suite;
	}

}
