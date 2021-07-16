package egovframework.datagrid;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class DatagridRepository {

	@Cacheable(cacheNames = "testCache", key = "#id")
	public String getData(int id) {
		UUID uuid = UUID.randomUUID();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "test-data-" + uuid;
	}

	@CacheEvict(cacheNames = "testCache", key = "#id")
	public void evictCache(int id) {
		System.out.println("delete cache " + id);
	}

	@CacheEvict(cacheNames = "testCache", allEntries = true)
	public void evictCache() {
		System.out.println("delete cache all");
	}
}
