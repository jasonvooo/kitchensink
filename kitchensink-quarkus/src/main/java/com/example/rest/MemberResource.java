package com.example.rest;/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.example.model.Member;
import com.example.service.MemberService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/api/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {

    private static final Logger LOG = Logger.getLogger(MemberService.class);

    @Inject
    Validator validator;

    @Inject
    MemberService memberService;

    @GET
    public List<Member> listAllMembers() {
        return memberService.getAllMembers();
    }

    @GET
    @Path("/{id:[0-9]+}")
    public Member lookupMemberById(@PathParam("id") long id) {
        return memberService.findById(id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }


    @POST
    @Transactional
    public Response createMember(@Valid Member member) {
        try {
            memberService.register(member);

            // Create an "ok" response with the persisted member
            return Response.status(Response.Status.CREATED)
                    .entity(member)
                    .build();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            return createViolationResponse(ce.getConstraintViolations()).build();
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return Response.status(Response.Status.CONFLICT).entity(responseObj).build();
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(responseObj).build();
        }
    }

    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        LOG.info("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
}
