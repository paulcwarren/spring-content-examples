package com.emc.spring.content.examples;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.emc.spring.content.examples.AbstractSpringContentTests;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jConfiguration;
import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@Ginkgo4jConfiguration(threads=1)
@SpringApplicationConfiguration(classes = com.emc.spring.content.examples.Application.class)   
public class ClaimTest extends AbstractSpringContentTests {

}
