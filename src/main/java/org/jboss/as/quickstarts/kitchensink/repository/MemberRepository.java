package org.jboss.as.quickstarts.kitchensink.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<Member> {

    public Optional<Member> findById(long id) {
        return find("id", id).firstResultOptional();
    }
    
    public Optional<Member> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<Member> findAllOrderedByName() {
        return listAll(Sort.by("name"));
    }
}