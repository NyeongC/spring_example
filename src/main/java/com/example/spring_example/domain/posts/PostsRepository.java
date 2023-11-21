package com.example.spring_example.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    // extends JpaRepository<Entity클래스, PK타입>
    // Repository와 Entity클래스는 같은 위치에 둔다.
    
    
    /*
    * SpringDataJpa 에서 제공하지 않는 메소드는 쿼리로 작성해도 됨
    * */
    @Query("SELECT p FROM Posts p ORDER BY p.id DESC") // 와 여기 Posts를 POSTS라고 대문자로쓰면 오류남
    List<Posts> findAllDesc();
}
