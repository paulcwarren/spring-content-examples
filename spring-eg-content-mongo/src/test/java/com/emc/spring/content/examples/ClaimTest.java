package com.emc.spring.content.examples;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.emc.spring.content.examples.AbstractSpringContentTests;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@ContextConfiguration(classes = { ClaimTestConfig.class })
public class ClaimTest extends AbstractSpringContentTests {

}
