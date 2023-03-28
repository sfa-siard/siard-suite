package ch.admin.bar.siardsuite.model.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldAcceptVisitor() {
        // given
        User user = new User("admin", "the database admin");
        TestUserVisitor testVisitor = new TestUserVisitor();

        // when
        user.accept(testVisitor);

        // then
        Assertions.assertEquals("admin", testVisitor.name);
        Assertions.assertEquals("the database admin", testVisitor.description);

    }

    private static class TestUserVisitor implements UserVisitor {
        String name;
        String description;


        @Override
        public Object visit(String name, String description) {
            this.name = name;
            this.description = description;
            return true;
        }
    }
}