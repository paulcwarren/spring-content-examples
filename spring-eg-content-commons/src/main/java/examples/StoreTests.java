package examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;

public class StoreTests {

    @Autowired private DocumentStore store;

    private Resource r;

    {
        Describe("Store", () -> {
            Context("given a resource", () -> {
                BeforeEach(() -> {
                    r = store.getResource("/examples/example-resource");
                });
                It("should be able to store content", () -> {

                });
            });
        });
    }
}
