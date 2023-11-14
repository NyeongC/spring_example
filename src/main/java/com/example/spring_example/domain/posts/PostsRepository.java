package com.example.spring_example.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    // extends JpaRepository<Entity클래스, PK타입>
    // Repository와 Entity클래스는 같은 위치에 둔다.
}
