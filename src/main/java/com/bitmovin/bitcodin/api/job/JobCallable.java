package com.bitmovin.bitcodin.api.job;

import java.util.concurrent.Callable;

import com.bitmovin.bitcodin.api.BitcodinApi;
import com.bitmovin.bitcodin.api.exception.BitcodinApiException;
import com.bitmovin.bitcodin.api.input.Input;
import com.bitmovin.bitcodin.api.media.EncodingProfile;

/**
 * Does the actual work of processing a job through the bitcodin API. 
 * 
 * @author alangibson
 *
 */
public class JobCallable implements Callable<JobDetails> {

	private JobConfig jobConfig;
	private BitcodinApi bitApi;

	/**
	 * @param bitApi Configured BitcodinApi object.
	 * @param jobConfig Job config to be submitted for processing.
	 */
	public JobCallable(BitcodinApi bitApi, JobConfig jobConfig) {
		this.bitApi = bitApi;
		this.jobConfig = jobConfig;
	}
	
	/**
	 * Processes job via the bitcodin API and either returns a completed 
	 * JobDetails or throws and exception indicating job failure. 
	 */
	@Override
	public JobDetails call() throws BitcodinApiException, InterruptedException {

		Input input = bitApi.createInput(jobConfig.httpInputConfig);
		
		EncodingProfile encodingProfile = bitApi.createEncodingProfile(jobConfig.encodingProfileConfig);
		
		jobConfig.encodingProfileId = encodingProfile.encodingProfileId;
        jobConfig.inputId = input.inputId;
		Job job = bitApi.createJob(jobConfig);
		
		JobDetails jobDetails = bitApi.getJobDetails(job.jobId);

        do {
            jobDetails = bitApi.getJobDetails(job.jobId);
            if (jobDetails.status == JobStatus.ERROR) {
                throw new BitcodinApiException("Error during transcoding");
            }
            Thread.sleep(2000);
        } while (jobDetails.status != JobStatus.FINISHED);
		
		return jobDetails;
	}

}
