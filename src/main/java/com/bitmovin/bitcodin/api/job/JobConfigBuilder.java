package com.bitmovin.bitcodin.api.job;

import com.bitmovin.bitcodin.api.FluentBuilder;
import com.bitmovin.bitcodin.api.input.HTTPInputConfig;
import com.bitmovin.bitcodin.api.input.HTTPInputConfigBuilder;
import com.bitmovin.bitcodin.api.media.EncodingProfileConfig;
import com.bitmovin.bitcodin.api.media.EncodingProfileConfigBuilder;

/**
 * @see FluentBuilder for API documentation.
 * @author alangibson
 *
 */
public class JobConfigBuilder implements FluentBuilder<JobConfig, JobConfig> {

	private JobConfig jobConfig;
	private EncodingProfileConfigBuilder encodingProfileConfigBuilder;
	private HTTPInputConfigBuilder httpInputConfigBuilder;

	public JobConfigBuilder() {
		this.jobConfig = new JobConfig();
	}
	
	@Override
	public JobConfig build() {
		return jobConfig;
	}

	@Override
	public JobConfig done() {
		return jobConfig;
	}
	
	// Fluent builder API methods
	
	public EncodingProfileConfigBuilder createEncodingProfileConfigBuilder() {
		encodingProfileConfigBuilder = new EncodingProfileConfigBuilder(this);
		return encodingProfileConfigBuilder;
	}
	
	public HTTPInputConfigBuilder createHTTPInputConfigBuilder() {
		httpInputConfigBuilder = new HTTPInputConfigBuilder(this);
		return httpInputConfigBuilder;
	}
	
	public JobConfigBuilder addManifestType(ManifestType mpegDashMpd) {
		this.jobConfig.manifestTypes.addElement(mpegDashMpd);
		return this;
	}
	
	// Setters for use by nested builders
	
	public void setHTTPInputConfig(HTTPInputConfig httpInputConfig) {
		if (jobConfig.httpInputConfig == null) {
			jobConfig.httpInputConfig = new HTTPInputConfig();
		}
		jobConfig.httpInputConfig = httpInputConfig;
	}

	public void setEncodingProfileConfig(EncodingProfileConfig encodingProfileConfig) {
		if (jobConfig.encodingProfileConfig == null) {
			jobConfig.encodingProfileConfig = new EncodingProfileConfig();
		}
		jobConfig.encodingProfileConfig = encodingProfileConfig;
	}

}
