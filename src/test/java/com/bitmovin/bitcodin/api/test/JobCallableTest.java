package com.bitmovin.bitcodin.api.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.bitmovin.bitcodin.api.BitcodinApi;
import com.bitmovin.bitcodin.api.input.HTTPInputConfig;
import com.bitmovin.bitcodin.api.input.Input;
import com.bitmovin.bitcodin.api.job.Job;
import com.bitmovin.bitcodin.api.job.JobCallable;
import com.bitmovin.bitcodin.api.job.JobConfig;
import com.bitmovin.bitcodin.api.job.JobConfigBuilder;
import com.bitmovin.bitcodin.api.job.JobDetails;
import com.bitmovin.bitcodin.api.job.JobStatus;
import com.bitmovin.bitcodin.api.job.ManifestType;
import com.bitmovin.bitcodin.api.media.EncodingProfile;
import com.bitmovin.bitcodin.api.media.EncodingProfileConfig;
import com.bitmovin.bitcodin.api.media.Preset;
import com.bitmovin.bitcodin.api.media.Profile;

public class JobCallableTest {

	@Test
	public void testJobCallable() throws Exception {

		// Given
		
		BitcodinApi bitApi = Mockito.mock(BitcodinApi.class);
		
		// To be set by bitApi.createJob mock method invocation.
		final List<Job> jobs = new ArrayList<>();
		
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
		
		Mockito.when(bitApi.createJob(Mockito.any(JobConfig.class))).thenAnswer(new Answer<Job>(){
			@Override
			public Job answer(InvocationOnMock invocation) throws Throwable {
				Job job = new Job();
				job.jobId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
				jobs.add(job);
				return job;
			}
		});
        
		Mockito.when(bitApi.getJobDetails(Mockito.anyInt())).thenAnswer(new Answer<JobDetails>() {
			@Override
			public JobDetails answer(InvocationOnMock invocation) throws Throwable {
				Integer arg = invocation.getArgument(0);
				JobDetails jobDetails = new JobDetails();
				Job job = jobs.get(0);
				jobDetails.status = JobStatus.FINISHED;
				jobDetails.jobId = job.jobId;
				return jobDetails;
			}
		});
		
		JobConfig jobConfig = new JobConfigBuilder()
			.addManifestType(ManifestType.MPEG_DASH_MPD)
			.createHTTPInputConfigBuilder()
				.withURL("http://somedomain.com/not.real.1.mkv")
				.done()
			.createEncodingProfileConfigBuilder()
				.withName("not.real.1")
				.addVideoStreamConfigBuilder()
					.withBitrate(1 * 1024 * 1024)
					.withWidth(640)
					.withHeight(480)
					.withProfile(Profile.MAIN)
					.withPreset(Preset.PREMIUM)
					.done()
				.done()
			.build();
		
		// When
		
		JobCallable jobCallable = new JobCallable(bitApi, jobConfig);
		
		JobDetails jobDetails = jobCallable.call();
		
		// Then
		
		assertEquals(jobs.get(0).jobId, jobDetails.jobId);
		assertEquals(JobStatus.FINISHED, jobDetails.status);
	}
	
}
