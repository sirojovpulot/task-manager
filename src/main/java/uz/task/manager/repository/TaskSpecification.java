package uz.task.manager.repository;

import org.springframework.data.jpa.domain.Specification;
import uz.task.manager.entity.Task;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.entity.enums.TaskStatus;

import java.time.LocalDate;


public class TaskSpecification {

    public static Specification<Task> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> contentContains(String content) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Task> categoryContains(String category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.toLowerCase() + "%");
    }

    public static Specification<Task> dueDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("dueDate"), startDate, endDate);
    }

    public static Specification<Task> priorityEqual(TaskPriority priority) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority.toString());
    }

    public static Specification<Task> statusEqual(TaskStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status.toString());
    }
}
