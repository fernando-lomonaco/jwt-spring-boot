package academy.lomonaco.course.endpoint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import academy.lomonaco.core.model.Course;
import academy.lomonaco.core.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseService {

	private final CourseRepository courseRepository;

	public Iterable<Course> list(Pageable page) {
		log.info("Listing all course");
		return courseRepository.findAll(page);
	}
}
