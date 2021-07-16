package egovframework.datagrid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedhatDatagrid {

	@Autowired
	DatagridRepository repository;

	@RequestMapping(value = "/datagrid/get/{id}", method = RequestMethod.GET)
	public Map<Object, Object> getCache(@PathVariable("id") int id) {

		Map<Object, Object> map = new HashMap<Object, Object>();

		long start = System.currentTimeMillis();

		String result = repository.getData(id);

		long end = System.currentTimeMillis();

		int elapsed_time = (int) (end - start);

		map.put("result", result);
		map.put("elapsed_time", elapsed_time);

		return map;
	}

	@RequestMapping(value = "/datagrid/evict/{id}", method = RequestMethod.GET)
	public Map<Object, Object> evictCache(@PathVariable("id") int id) {

		Map<Object, Object> map = new HashMap<Object, Object>();

		long start = System.currentTimeMillis();

		repository.evictCache(id);

		long end = System.currentTimeMillis();

		int elapsed_time = (int) (end - start);

		map.put("result", "success");
		map.put("elapsed_time", elapsed_time);

		return map;
	}

	@RequestMapping(value = "/datagrid/evict", method = RequestMethod.GET)
	public Map<Object, Object> evictCache() {
		Map<Object, Object> map = new HashMap<Object, Object>();

		long start = System.currentTimeMillis();

		repository.evictCache();

		long end = System.currentTimeMillis();

		int elapsed_time = (int) (end - start);

		map.put("result", "success");
		map.put("elapsed_time", elapsed_time);

		return map;
	}

}
