package net.silthus.template.identity;

import java.util.UUID;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.template.AssertionHelper.assertNPE;
import static net.silthus.template.identity.Identity.identity;
import static org.assertj.core.api.Assertions.assertThat;

class IdentityTests {

    private static final @NotNull TextComponent DISPLAY_NAME = text("Bob");
    private static final String NAME = "Test";

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_identity_with_null_id_throws_npe() {
        assertNPE(() -> identity((UUID) null));
    }

    @Test
    void given_identity_with_id_then_identity_has_id() {
        final UUID id = randomUUID();
        final Identity identity = identity(id);
        assertThat(identity.getUniqueId()).isEqualTo(id);
    }

    @Test
    void given_identity_with_same_id_then_identities_are_equal() {
        final UUID id = randomUUID();
        final Identity identity1 = identity(id);
        final Identity identity2 = identity(id, NAME);
        assertThat(identity1).isEqualTo(identity2);
        assertThat(identity1).isNotSameAs(identity2);
    }

    @Nested class given_nil_identity {
        private Identity identity;

        @BeforeEach
        void setUp() {
            identity = Identity.nil();
        }

        @Test
        void then_properties_match_nil_properties() {
            assertThat(identity)
                .extracting(
                    Identity::getUniqueId,
                    Identity::getName,
                    Identity::getDisplayName
                ).contains(
                    Identity.NIL_IDENTITY_ID,
                    "",
                    empty()
                );
        }
    }

    @Nested class given_identity_with_name {
        private Identity identity;

        @BeforeEach
        void setUp() {
            identity = identity(NAME);
        }

        @Test
        void then_has_name() {
            assertThat(identity.getName()).isEqualTo(NAME);
        }

        @Test
        void then_display_name_is_name() {
            assertThat(identity.getDisplayName()).isEqualTo(text(NAME));
        }

        @Nested class given_display_name {
            @BeforeEach
            void setUp() {
                identity = Identity.identity(NAME, DISPLAY_NAME);
            }

            @Test
            void then_uses_custom_display_name() {
                assertThat(identity.getDisplayName()).isEqualTo(DISPLAY_NAME);
            }
        }

        @Nested class given_dynamic_display_name {
            private @NotNull TextComponent displayName;

            @BeforeEach
            void setUp() {
                displayName = DISPLAY_NAME;
                identity = Identity.identity(NAME, () -> displayName);
            }

            @Test
            void then_uses_display_name() {
                assertThat(identity.getDisplayName()).isEqualTo(DISPLAY_NAME);
            }

            @Test
            void when_display_name_changes_then_display_name_is_updated() {
                displayName = text("Bobby");
                assertThat(identity.getDisplayName()).isEqualTo(displayName);
            }
        }
    }
}