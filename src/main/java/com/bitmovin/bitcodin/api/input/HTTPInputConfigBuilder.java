package com.bitmovin.bitcodin.api.input;

import com.bitmovin.bitcodin.api.FluentBuilder;
import com.bitmovin.bitcodin.api.job.JobConfigBuilder;

/**
 * @see FluentBuilder for API documentation.
 * @author alangibson
 *
 */
public class HTTPInputConfigBuilder implements FluentBuilder<JobConfigBuilder, HTTPInputConfig> {

	private JobConfigBuilder jobConfigBuilder;
	private HTTPInputConfig httpInputConfig;
	
	public HTTPInputConfigBuilder(JobConfigBuilder jobConfigBuilder) {
		this.jobConfigBuilder = jobConfigBuilder;
		this.httpInputConfig = new HTTPInputConfig();
	}

	@Override
	public HTTPInputConfig build() {
		jobConfigBuilder.setHTTPInputConfig(httpInputConfig);
		return httpInputConfig;
	}
	
	@Override
	public JobConfigBuilder done() {
		jobConfigBuilder.setHTTPInputConfig(httpInputConfig);
		return jobConfigBuilder;
	}
	
	// Fluent builder API methods
	
	public HTTPInputConfigBuilder withURL(String url) {
		httpInputConfig.url = url;
		return this;
	}

}
