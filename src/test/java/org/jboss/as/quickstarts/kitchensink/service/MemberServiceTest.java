package org.jboss.as.quickstarts.kitchensink.service;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MemberServiceTest {

    @Inject
    MemberService memberService;

    @Test
    @TestTransaction
    public void testRegister() {
        Member newMember = new Member();
        newMember.setName("John Doe");
        newMember.setEmail("john1412@example.com");
        newMember.setPhoneNumber("1234567890");

        memberService.register(newMember);

        assertNotNull(newMember.getId());
        assertTrue(newMember.getId() > 0);
    }

    @Test
    @TestTransaction
    public void testInvalidRegister() {
        Member newMember = new Member();
        newMember.setName("John123"); // Invalid name with numbers
        newMember.setEmail("not-an-email");
        newMember.setPhoneNumber("invalid");

        assertThrows(ConstraintViolationException.class, () -> {
            memberService.register(newMember);
        });
    }

    @Test
    @TestTransaction
    public void testDuplicateEmail() {
        // Register first member
        Member member1 = new Member();
        member1.setName("John Doe");
        member1.setEmail("john@example.com");
        member1.setPhoneNumber("1234567890");
        memberService.register(member1);

        // Try to register second member with same email
        Member member2 = new Member();
        member2.setName("Jane Doe");
        member2.setEmail("john@example.com"); // Same email
        member2.setPhoneNumber("1234567891");

        assertThrows(RuntimeException.class, () -> {
            memberService.register(member2);
        });
    }

    @Test
    @TestTransaction
    public void testFindById() {
        // Create a member
        Member newMember = new Member();
        newMember.setName("Find Me");
        newMember.setEmail("findme@example.com");
        newMember.setPhoneNumber("1234567890");
        memberService.register(newMember);

        // Find the member
        Member found = memberService.findById(newMember.getId())
                .orElseThrow();

        assertEquals("Find Me", found.getName());
        assertEquals("findme@example.com", found.getEmail());
    }
}
