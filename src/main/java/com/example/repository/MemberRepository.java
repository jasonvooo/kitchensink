package com.example.repository;

import com.example.model.Member;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MemberRepository implements PanacheMongoRepository<Member> {


    public Member findById(String id) {
        return this.findById(new ObjectId(id));
    }

    public Optional<Member> findByEmail(String email) {
        return find(new BsonDocument("email", new BsonString(email))).stream().findFirst();
    }

    public List<Member> findAllOrderedByName() {
        return listAll(Sort.by("name")).stream().toList();
    }
}
