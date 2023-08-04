package side.mimi.mdd.restApi.Disk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.mimi.mdd.restApi.Disk.repository.DiskRepository;

@Service
@RequiredArgsConstructor
public class DiskService {
	private final DiskRepository diskRepository;
}
