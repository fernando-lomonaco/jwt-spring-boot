package academy.lomonaco.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import academy.lomonaco.core.model.Course;

public interface CourseRepository extends PagingAndSortingRepository<Course, Long>  {

}
