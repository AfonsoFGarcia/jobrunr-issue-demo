package com.example.demo

import org.jobrunr.storage.StorageProvider
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
class PostgreSQLErrorTest {

	@Autowired
	private lateinit var storageProvider: StorageProvider

	companion object {
		@Container
		@JvmStatic
		private val POSTGRESQL_CONTAINER = PostgreSQLContainer(DockerImageName.parse("postgres").withTag("15.2-alpine"))
			.withDatabaseName("test-database")
			.withUsername("username")
			.withPassword("password")

		@DynamicPropertySource
		@JvmStatic
		private fun postgresProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl)
			registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername)
			registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword)
		}
	}

	/**
	 * While the expectation is that this method returns false, when running under PostgreSQL it throws the following exception:
	 *
	 * org.jobrunr.storage.StorageException: org.postgresql.util.PSQLException: ERROR: syntax error at or near ")"
	 *   Position: 51
	 *
	 * This is caused by the following invalid query:
	 *
	 * select count(*) from jobrunr_jobs where state in () AND recurringJobId = :recurringJobId
	 */
	@Test
	fun `StorageProvider should return false when asking for a recurring job that does not exist in any state`() {
		assertFalse(storageProvider.recurringJobExists("it-s-me-hi"), "The job does not exist, this should be false")
	}
}
