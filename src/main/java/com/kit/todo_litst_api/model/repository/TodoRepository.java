package com.kit.todo_litst_api.model.repository;

import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<TodoResponse> findByUserId(Long userId, Pageable pageable);
}
