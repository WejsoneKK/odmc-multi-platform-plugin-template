package net.silthus.template.pointer;

import java.util.function.Supplier;
import net.silthus.template.AssertionHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.template.AssertionHelper.assertUnmodifiable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PointersTest {
    @Test
    public void ofPointers() {
        final Pointer<String> pointer = Pointer.pointer(String.class, "test");

        assertFalse(Pointers.empty().supports(pointer));

        final Pointers p0 = Pointers.pointers()
            .withStatic(pointer, null)
            .create();
        assertTrue(p0.supports(pointer));
        assertFalse(p0.get(pointer).isPresent());

        final Pointers p1 = Pointers.pointers()
            .withStatic(pointer, "test")
            .create();
        assertTrue(p1.supports(pointer));
        assertTrue(p1.get(pointer).isPresent());
        assertEquals("test", p1.get(pointer).get());
        assertEquals("test", p1.get(pointer).get()); // make sure the value doesn't change

        final StringBuilder s = new StringBuilder("test");
        final Supplier<String> supplier = () -> {
            final String result = s.toString();
            s.reverse();
            return result;
        };
        final Pointers p2 = Pointers.pointers()
            .withDynamic(pointer, supplier)
            .create();
        assertTrue(p2.supports(pointer));
        assertEquals("test", p2.getOrDefault(pointer, null));
        assertEquals("tset", p2.getOrDefault(pointer, null)); // make sure the value does change
    }

    @Nested class given_empty_pointers {
        private @NotNull Pointers pointers;
        private @NotNull Pointer<String> pointer;

        @BeforeEach
        void setUp() {
            pointers = Pointers.empty();
            pointer = Pointer.pointer(String.class, "test");
        }

        @Test
        void then_always_returns_empty() {
            assertThat(pointers.get(pointer)).isEmpty();
            assertThat(pointers.getPointers()).isEmpty();
            assertThat(pointers.supports(pointer)).isFalse();
            assertUnmodifiable(pointers.getPointers(), () -> pointer);
        }
    }
}