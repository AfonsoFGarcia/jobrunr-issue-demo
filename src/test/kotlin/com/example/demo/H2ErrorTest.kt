package com.example.demo

import org.awaitility.kotlin.await
import org.jobrunr.scheduling.JobScheduler
import org.jobrunr.scheduling.cron.Cron
import org.jobrunr.storage.StorageProvider
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.Calendar
import java.util.concurrent.TimeUnit

@SpringBootTest
class H2ErrorTest {

	@Autowired
	private lateinit var storageProvider: StorageProvider

	@Autowired
	private lateinit var jobScheduler: JobScheduler

	/**
	 * Stupidly, H2 is very happy to run this...
	 */
	@Test
	fun `StorageProvider should return false when asking for a recurring job that does not exist in any state`() {
		assertFalse(storageProvider.recurringJobExists("it-s-me-hi"), "The job does not exist, this should be false")
	}

	/**
	 * This test should pass as the recurring job was scheduled and, as such, exists.
	 *
	 * However, it is false as there are no jobs in jobrunr_jobs until an actual scheduled run is executed.
	 */
	@Test
	fun `StorageProvider should return true when asking for a recurring job that exists but has not been executed`() {
		assertFalse(storageProvider.recurringJobExists("i-m-the-problem-it-s-me"), "The job is not scheduled, this should be false")

		jobScheduler.scheduleRecurrently("i-m-the-problem-it-s-me", Cron.daily(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 3)) {}

		assertTrue(storageProvider.recurringJobExists("i-m-the-problem-it-s-me"), "I just scheduled the job, this should be true")
	}

	/**
	 * This test should pass as the recurring job was scheduled and executed.
	 *
	 * However, it is false as H2 is considering "where state in ()" to always be false (which makes sense, as we're asking it to be in an empty list).
	 */
	@Test
	fun `StorageProvider should return true when asking for a recurring job that exists and has been executed`() {
		assertFalse(storageProvider.recurringJobExists("i-m-the-problem-it-s-me"), "The job is not scheduled, this should be false")

		jobScheduler.scheduleRecurrently("i-m-the-problem-it-s-me", Cron.every15seconds()) {
			println("At tea time everybody agrees")
		}

		await.atMost(30, TimeUnit.SECONDS).until {
			storageProvider.recurringJobExists("i-m-the-problem-it-s-me")
		}
	}
}
