package com.my.interrior.three;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ThreeRestController {

	private final ThreeService threeService;

	@PostMapping("/save/project")
	public ResponseEntity<?> saveProject(@RequestPart("jsonData") SaveProjectRequest request,
			@RequestPart("file") MultipartFile thumbnail, HttpSession session) throws IOException {
		String userId = (String) session.getAttribute("UId");

		log.info("request DATA: {}, request FILE: {}", request, thumbnail);

		threeService.saveData(request, thumbnail, userId);

		return ResponseEntity.ok().build();
	}

	// filter : 빈 문자열, sort : 문자열, flag : 정수형 Request
	// response : status : success, fail || response: 200, errorcode || data:
	// {projects:[], flag_num : 0 => 0~11, 1=> 12 ~23}
	@GetMapping("/get/projects")
	public ResponseEntity<GetProjectsResponse> getProjects(@ModelAttribute("params") GetProjectsRequest request, HttpSession session) {
	    String userId = (String) session.getAttribute("UId");
	    String filter = request.getFilter();
	    String sort = request.getSort();
	    int page = request.getFlag(); // 페이지
	    int size = 12; // 한 페이지에 가져올 데이터 수

	    // 해당 사용자의 프로젝트 갯수 구하기(페이징 조건)
	    int count = threeService.getCounts(userId, filter, sort);

	    // 해당 사용자의 프로젝트 구하기
	    // 사용자가 없으면(얼리 리턴 패턴)
	    if (userId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    // 만약 데이터베이스에 가지고 있는 데이터가 없으면
	    if (count == 0) {
	        GetProjectsResponse response = new GetProjectsResponse();
	        response.setStatus("success");
	        response.setResponse(200);
	        GetProjectsResponse.GetProjectDataResponse data = new GetProjectsResponse.GetProjectDataResponse();
	        data.setProjects(List.of()); // 빈 리스트 설정
	        data.setFlagNum(-1); // 데이터 없음 플래그
	        response.setData(data);
	        return ResponseEntity.ok(response);
	    }

	    // 데이터 가져오기
	    Page<ThreeEntity> pageResult = threeService.getTopData(userId, filter, page, size, sort);

	    // 다음 페이지를 위한 flag 계산
	    int nextFlag = pageResult.hasNext() ? 1 : -1;

	    // 데이터가 없을 경우
	    if (pageResult.getContent().isEmpty()) {
	        GetProjectsResponse response = new GetProjectsResponse();
	        response.setStatus("success");
	        response.setResponse(200);
	        GetProjectsResponse.GetProjectDataResponse data = new GetProjectsResponse.GetProjectDataResponse();
	        data.setProjects(List.of()); // 빈 리스트 설정
	        data.setFlagNum(-1); // 데이터 없음 플래그
	        response.setData(data);
	        return ResponseEntity.ok(response);
	    }

	    // GetProjectsResponse 객체 설정
	    GetProjectsResponse response = new GetProjectsResponse();
	    response.setStatus("success");
	    response.setResponse(200);

	    GetProjectsResponse.GetProjectDataResponse data = new GetProjectsResponse.GetProjectDataResponse();
	    data.setProjects(pageResult.getContent().stream().map(this::mapToProjectData).collect(Collectors.toList()));
	    data.setFlagNum(nextFlag);
	    response.setData(data);

	    log.info("3D_RESPONSE!!!!!!!! : {}", response);
	    
	    return ResponseEntity.ok(response);
	}

	// ThreeEntity를 GetProjectsResponse.GetProjectsData로 매핑하는 메서드
	private GetProjectsResponse.GetProjectsData mapToProjectData(ThreeEntity entity) {
	    GetProjectsResponse.GetProjectsData data = new GetProjectsResponse.GetProjectsData();
	    data.setProjectId(entity.getProjectId());
	    data.setThumbnail(entity.getThumbnail());
	    data.setTitle(entity.getTitle());
	    data.setSrc(entity.getSrc());
	    data.setRegDt(entity.getRegDate());
	    return data;
	}
}
