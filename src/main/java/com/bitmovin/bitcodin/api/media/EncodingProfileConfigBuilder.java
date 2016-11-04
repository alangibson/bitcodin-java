package com.bitmovin.bitcodin.api.media;

import java.util.ArrayList;
import java.util.List;

import com.bitmovin.bitcodin.api.FluentBuilder;
import com.bitmovin.bitcodin.api.job.JobConfigBuilder;

/**
 * @see FluentBuilder for API documentation.
 * @author alangibson
 *
 */
public class EncodingProfileConfigBuilder implements FluentBuilder<JobConfigBuilder, EncodingProfileConfig> {

	private EncodingProfileConfig encodingProfileConfig;
	private JobConfigBuilder jobConfigBuilder;
	private List<VideoStreamConfigBuilder> videoStreamConfigBuilders = new ArrayList<>();
	
	public EncodingProfileConfigBuilder(JobConfigBuilder jobConfigBuilder) {
		this.jobConfigBuilder = jobConfigBuilder;
		this.encodingProfileConfig = new EncodingProfileConfig();
	}

	@Override
	public EncodingProfileConfig build() {
		jobConfigBuilder.setEncodingProfileConfig(encodingProfileConfig);
		return encodingProfileConfig;
	}

	@Override
	public JobConfigBuilder done() {
		jobConfigBuilder.setEncodingProfileConfig(encodingProfileConfig);
		return jobConfigBuilder;
	}

	// Fluent builder API methods
	
	public EncodingProfileConfigBuilder withName(String name) {
		this.encodingProfileConfig.name = name;
		return this;
	}
	
	public VideoStreamConfigBuilder addVideoStreamConfigBuilder() {
		VideoStreamConfigBuilder vscb = new VideoStreamConfigBuilder(this);
		this.videoStreamConfigBuilders.add(vscb);
		return vscb;
	}

	public void addVideoStreamConfig(VideoStreamConfig videoStreamConfig) {
		encodingProfileConfig.videoStreamConfigs.add(videoStreamConfig);
	}

}
