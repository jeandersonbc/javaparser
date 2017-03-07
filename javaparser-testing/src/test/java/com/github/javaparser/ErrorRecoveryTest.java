package com.github.javaparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.javaparser.ast.stmt.BlockStmt;

@RunWith(Parameterized.class)
public class ErrorRecoveryTest {

    static class TestParam {
        List<Range> problemsLocations;
        String stringSource;

        public TestParam(String stringSource, List<Range> problemsLocations) {
            this.problemsLocations = problemsLocations;
            this.stringSource = stringSource;
        }
    }

    @Parameter
    public TestParam testParam;

    @Parameters
    public static Collection<TestParam> data() {
        List<TestParam> testParameters = new ArrayList<>();

        Map<String, List<Range>> values = new HashMap<>();
        values.put("{int x 2; int;}", Arrays.asList(Range.range(1, 8, 1, 8), Range.range(1, 14, 1, 14)));
        values.put("{int; int x 2;}", Arrays.asList(Range.range(1, 14, 1, 14), Range.range(1, 8, 1, 8)));

        for (String source : values.keySet()) {
            testParameters.add(new TestParam(source, values.get(source)));
        }
        return testParameters;
    }

    @Test
    public void checkExpectedProblems() {
        Provider provider = Providers.provider(testParam.stringSource);
        ParseResult<BlockStmt> result = (new JavaParser()).parse(ParseStart.BLOCK, provider);

        // Sanity check
        assertFalse("Expected to not parse successfully the source", result.isSuccessful());
        assertEquals(testParam.problemsLocations.size(), result.getProblems().size());

        // TODO: Check location
    }

}
