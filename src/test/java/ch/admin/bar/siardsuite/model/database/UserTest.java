package ch.admin.bar.siardsuite.model.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldAcceptVisitor() {
        // given
        User user = new User("admin", "the database admin");
        TestUserVisitor testVisitor = new TestUserVisitor();

        // when
        user.accept(testVisitor);

        // then
        assertEquals("admin", testVisitor.name);
        assertEquals("the database admin", testVisitor.description);

    }

    private static class TestUserVisitor implements UserVisitor {
        String name;
        String description;


        @Override
        public void visit(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}