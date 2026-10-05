package dev.trexzo.cleanroom.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.trexzo.cleanroom.core.module.Module;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClientCoreTest {
    @Test
    void ownsCoreServicesAndShutsDownInDeterministicReverseOrder() {
        List<String> order = new ArrayList<>();
        ClientCore core = new ClientCore();

        TestModule alpha = new TestModule("a.alpha", order);
        TestModule beta = new TestModule("b.beta", order);
        core.modules().register(alpha);
        core.modules().register(beta);

        core.own(() -> order.add("resource-1"));
        core.own(() -> order.add("resource-2"));

        alpha.setEnabled(true);
        beta.setEnabled(true);
        order.clear();

        core.start();
        core.close();

        assertEquals(
                List.of("disable:b.beta", "disable:a.alpha", "resource-2", "resource-1"),
                order);
        assertEquals(ClientCore.State.CLOSED, core.state());

        core.close();
        assertEquals(4, order.size());
    }

    @Test
    void startIsSingleTransition() {
        ClientCore core = new ClientCore();
        core.start();

        assertThrows(IllegalStateException.class, core::start);

        core.close();
    }

    private static final class TestModule extends Module {
        private final List<String> order;

        private TestModule(String id, List<String> order) {
            super(id, id, Category.MISC);
            this.order = order;
        }

        @Override
        protected void onDisable() {
            order.add("disable:" + id());
        }
    }
}
