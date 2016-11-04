package com.bitmovin.bitcodin.api.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.bitmovin.bitcodin.api.BitcodinApi;

/**
 * Asynchronously executes Jobs.
 * 
 * @author alangibson
 *
 */
public class JobExecutor {

	private List<JobCallable> callables = new ArrayList<>();
	private BitcodinApi bitApi;
	private final ExecutorService pool;
	private final ExecutorCompletionService<JobDetails> completionService; 
	
	
	/**
	 * Creates a ExecutorCompletionService with a new thread pool.
	 *  
	 * @param bitApi Configured BitcodinApi object.
	 * @param threads Number of threads to start in thread pool.
	 */
	public JobExecutor(BitcodinApi bitApi, int threads) {
		this.bitApi = bitApi;
		pool = Executors.newFixedThreadPool(threads);
		completionService = new ExecutorCompletionService<>(pool);
	}
	
	/**
	 * Submit a job for asynchronous processing.
	 * 
	 * There are 2 options for waiting on job completion:
	 * - Poll the returned Future manually
	 * - Pass success and error callbacks to subscribe()  
	 * 
	 * @param jobConfig Job to execute.
	 * @return Future that will return JobDetails when job is complete.
	 */
	public Future<JobDetails> submit(JobConfig jobConfig) {
		JobCallable jobCallable = new JobCallable(bitApi, jobConfig);
		callables.add(jobCallable);
		Future<JobDetails> future = completionService.submit(jobCallable);
		return future;
	}
	
	/**
	 * Wait for jobs to be completed by cloud service. 
	 * 
	 * @param success Called once for every job that is successfully processed. 
	 * @param failure Called once for every job that fails.
	 */
	public void await(Consumer<JobDetails> success, Consumer<Exception> failure) {
		for(int i = 0; i < callables.size(); ++i) {
			try {
				Future<JobDetails> future = completionService.take();
				final JobDetails content = future.get();
				success.accept(content);
			} catch (InterruptedException | ExecutionException e) {
				failure.accept(e);
			}
		}
	}
	
}
