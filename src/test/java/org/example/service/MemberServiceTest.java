package org.example.service;

import com.example.model.Member;
import com.example.service.MemberService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@Transactional
public class MemberServiceTest {

    @Inject
    MemberService memberService;

    @Test
    public void testRegister() {
        Member newMember = new Member();
        newMember.setName("John Doe");
        newMember.setEmail("john1412@example.com");
        newMember.setPhoneNumber("1234567890");

        memberService.register(newMember);

        assertNotNull(newMember.getName());
    }

    @Test
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
    public void testDuplicateEmail() {
        // Register first member
        Member member1 = new Member();
        member1.setName("John Doe");
        member1.setEmail("john2@example.com");
        member1.setPhoneNumber("1234567890");
        memberService.register(member1);

        // Try to register second member with same email
        Member member2 = new Member();
        member2.setName("Jane Doe");
        member2.setEmail("john2@example.com"); // Same email
        member2.setPhoneNumber("1234567891");

        assertThrows(RuntimeException.class, () -> {
            memberService.register(member2);
        });
    }

    @Test
    public void testFindById() {
        // Create a member
        Member newMember = new Member();
        newMember.setName("Find Me");
        newMember.setEmail("findme@example.com");
        newMember.setPhoneNumber("1234567890");
        memberService.register(newMember);

        // Find the member
        List<Member> members = memberService.getAllMembers();

        assertTrue(members.stream().anyMatch(f -> f.getName().equals("Find Me") && f.getEmail().equals("findme@example.com")));
    }
}
