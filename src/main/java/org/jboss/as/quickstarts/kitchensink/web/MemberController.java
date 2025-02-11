package org.jboss.as.quickstarts.kitchensink.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class MemberController {

    @Inject
    Template index;

    @Inject
    MemberService memberService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showMembers() {
        return index.data("members", memberService.getAllMembers())
                .data("errors", Collections.emptyMap())
                .data("formData", Collections.emptyMap())
                .data("success", "");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createMember(@FormParam("name") String name,
                                 @FormParam("email") String email,
                                 @FormParam("phoneNumber") String phoneNumber) {

        Map<String, String> formData = Map.of(
                "name", name,
                "email", email,
                "phoneNumber", phoneNumber
        );
        try {
            Member member = new Member();
            member.setName(name);
            member.setEmail(email);
            member.setPhoneNumber(phoneNumber);

            memberService.register(member);
            return Response.ok(index.data("members", memberService.getAllMembers())
                    .data("errors", Collections.emptyMap())
                    .data("formData", Collections.emptyMap())
                    .data("success", "true")).build();
        } catch (ConstraintViolationException ce) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            return Response.ok(
                    index.data("members", memberService.getAllMembers())
                            .data("errors", errors)
                            .data("formData", formData)
                            .data("success", "")
            ).status(400).build();
        } catch (ValidationException e) {
            return Response.ok(
                    index.data("members", memberService.getAllMembers())
                            .data("errors", Map.of("globalError", e.getMessage()))
                            .data("formData", formData)
                            .data("success", "")
            ).status(400).build();
        }
    }
}