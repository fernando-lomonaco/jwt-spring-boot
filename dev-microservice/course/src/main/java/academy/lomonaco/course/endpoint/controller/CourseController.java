package academy.lomonaco.course.endpoint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.lomonaco.core.model.Course;
import academy.lomonaco.course.endpoint.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("v1/admin/course")
@Api(value ="Endpoint curso")
public class CourseController {

	private final CourseService courseService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Lista todos cursos", response = Course[].class)
	public ResponseEntity<Iterable<Course>> list(Pageable page) {
		log.info("return API");
		// return new ResponseEntity<>(courseService.list(page), HttpStatus.OK);
		return ResponseEntity.ok(courseService.list(page));

	}

}
