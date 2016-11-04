package com.bitmovin.bitcodin.api.media;

import com.bitmovin.bitcodin.api.FluentBuilder;

/**
 * @see FluentBuilder for API documentation.
 * @author alangibson
 *
 */
public class VideoStreamConfigBuilder implements FluentBuilder<EncodingProfileConfigBuilder, VideoStreamConfig> {

	private VideoStreamConfig videoStreamConfig;
	private EncodingProfileConfigBuilder encodingProfileConfigBuilder;
	
	public VideoStreamConfigBuilder(EncodingProfileConfigBuilder encodingProfileConfigBuilder) {
		this.encodingProfileConfigBuilder = encodingProfileConfigBuilder;
		videoStreamConfig = new VideoStreamConfig();
	}

	@Override
	public VideoStreamConfig build() {
		encodingProfileConfigBuilder.addVideoStreamConfig(videoStreamConfig);
		return videoStreamConfig;
	}
	
	@Override
	public EncodingProfileConfigBuilder done() {
		encodingProfileConfigBuilder.addVideoStreamConfig(videoStreamConfig);
		return encodingProfileConfigBuilder;
	}

	// Fluent builder API methods
	
	public VideoStreamConfigBuilder withBitrate(int bitrate) {
		this.videoStreamConfig.bitrate = bitrate;
		return this;
	}

	public VideoStreamConfigBuilder withWidth(int width) {
		this.videoStreamConfig.width = width;
		return this;
	}

	public VideoStreamConfigBuilder withHeight(int height) {
		this.videoStreamConfig.height = height;
		return this;
	}

	public VideoStreamConfigBuilder withProfile(Profile profile) {
		this.videoStreamConfig.profile = profile;
		return this;
	}

	public VideoStreamConfigBuilder withPreset(Preset preset) {
		this.videoStreamConfig.preset = preset;
		return this;
	}

}
