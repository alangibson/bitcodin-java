package com.bitmovin.bitcodin.api.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.bitmovin.bitcodin.api.BitcodinApi;
import com.bitmovin.bitcodin.api.input.HTTPInputConfig;
import com.bitmovin.bitcodin.api.input.Input;
import com.bitmovin.bitcodin.api.job.Job;
import com.bitmovin.bitcodin.api.job.JobConfig;
import com.bitmovin.bitcodin.api.job.JobConfigBuilder;
import com.bitmovin.bitcodin.api.job.JobDetails;
import com.bitmovin.bitcodin.api.job.JobExecutor;
import com.bitmovin.bitcodin.api.job.JobStatus;
import com.bitmovin.bitcodin.api.job.ManifestType;
import com.bitmovin.bitcodin.api.media.EncodingProfile;
import com.bitmovin.bitcodin.api.media.EncodingProfileConfig;
import com.bitmovin.bitcodin.api.media.Preset;
import com.bitmovin.bitcodin.api.media.Profile;

public class JobExecutorTest {

	/**
	 * Test concurrent execution of jobs with JobExecutor.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecutor() throws Exception {
		
		// Given
		
		BitcodinApi bitApi = Mockito.mock(BitcodinApi.class);
		
		Mockito.when(bitApi.createInput(Mockito.any(HTTPInputConfig.class))).thenAnswer(new Answer<Input>(){
			@Override
			public Input answer(InvocationOnMock invocation) throws Throwable {
				HTTPInputConfig arg = invocation.getArgument(0);
				Input input = new Input();
				input.inputId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
				input.url = arg.url;
				return input;
			};
		});
		
		Mockito.when(bitApi.createEncodingProfile(Mockito.any(EncodingProfileConfig.class))).thenAnswer(new Answer<EncodingProfile>(){
			@Override
			public EncodingProfile answer(InvocationOnMock invocation) throws Throwable {
				EncodingProfileConfig arg = invocation.getArgument(0);
				EncodingProfile encodingProfile = new EncodingProfile();
				encodingProfile.encodingProfileId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
				encodingProfile.name = arg.name;
				return encodingProfile;
			}
		});
		
		Map<Integer, Job> jobs = new HashMap<>();
		Mockito.when(bitApi.createJob(Mockito.any(JobConfig.class))).thenAnswer(new Answer<Job>(){
			@Override
			public Job answer(InvocationOnMock invocation) throws Throwable {
				JobConfig arg = invocation.getArgument(0);
				Job job = new Job();
				job.jobId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
				jobs.put(job.jobId, job);
				return job;
			}
		});
        
		Mockito.when(bitApi.getJobDetails(Mockito.anyInt())).thenAnswer(new Answer<JobDetails>() {
			@Override
			public JobDetails answer(InvocationOnMock invocation) throws Throwable {
				Integer arg = invocation.getArgument(0);
				Job job = jobs.get(arg);
				JobDetails jobDetails = new JobDetails();
				jobDetails.status = JobStatus.FINISHED;
				jobDetails.jobId = job.jobId;
				return jobDetails;
			}
		});
		
		List<String> urls = Arrays.asList(
			"http://somedomain.com/not.real.1.mkv",
			"http://somedomain.com/not.real.2.mkv",
			"http://somedomain.com/not.real.3.mkv",
			"http://somedomain.com/not.real.4.mkv",
			"http://somedomain.com/not.real.5.mkv"
		);
		
		// When

		// Create a job runner
		JobExecutor executor = new JobExecutor(bitApi, 2);
		
		// Create JobConfigs and run them
		for (String url : urls) {
			JobConfig jobConfig = new JobConfigBuilder()
				.addManifestType(ManifestType.MPEG_DASH_MPD)
				.createHTTPInputConfigBuilder()
					.withURL(url)
					.done()
				.createEncodingProfileConfigBuilder()
					.withName(url)
					.addVideoStreamConfigBuilder()
						.withBitrate(1 * 1024 * 1024)
						.withWidth(640)
						.withHeight(480)
						.withProfile(Profile.MAIN)
						.withPreset(Preset.PREMIUM)
						.done()
					.done()
				.build();
			executor.submit(jobConfig);
		}
		
		// Wait for jobs to finish and then count them up
		final AtomicInteger counter = new AtomicInteger(0); 
		executor.await(
			// Success handler
			(jd) -> {
				counter.incrementAndGet();
			},
			// Failure handler
			(e) -> {
				System.out.println("A exception occured. This test will fail.");
				System.out.println(e);
			}
		);
		
		// Then
		
		// Assert that we processed all of the jobs we submitted
		assertEquals(urls.size(), counter.intValue());
	}
}
