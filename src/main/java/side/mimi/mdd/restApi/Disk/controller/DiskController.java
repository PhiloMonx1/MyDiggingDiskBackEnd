package side.mimi.mdd.restApi.Disk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.mimi.mdd.restApi.Disk.service.DiskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disks")
public class DiskController {
	private final DiskService diskService;
}
