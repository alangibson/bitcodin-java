package com.bitmovin.bitcodin.api.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bitmovin.bitcodin.api.job.JobConfig;
import com.bitmovin.bitcodin.api.job.JobConfigBuilder;
import com.bitmovin.bitcodin.api.job.ManifestType;
import com.bitmovin.bitcodin.api.media.Preset;
import com.bitmovin.bitcodin.api.media.Profile;

public class FluentBuilderTest {

	/**
	 * Test fluent builder API.
	 */
	@Test
	public void testBuilders() {
		
		// Given
		
		ManifestType manifestType = ManifestType.MPEG_DASH_MPD;
		String name = "Sintel (2010)";
		String url = "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/Sintel.2010.720p.mkv";
		Profile profile = Profile.MAIN;
		Profile profile2 = Profile.HIGH;
		
		// When
		
		JobConfig jobConfig = new JobConfigBuilder()
			.addManifestType(manifestType)
			.createHTTPInputConfigBuilder()
				.withURL(url)
				.done()
			.createEncodingProfileConfigBuilder()
				.withName(name)
				.addVideoStreamConfigBuilder()
					.withBitrate(1 * 1024 * 1024)
					.withWidth(640)
					.withHeight(480)
					.withProfile(profile)
					.withPreset(Preset.PREMIUM)
					.done()
				.addVideoStreamConfigBuilder()
					.withProfile(Profile.HIGH)
					.done()
				.done()
			.build();
		
		// Then
		
		assertEquals(manifestType, jobConfig.manifestTypes.get(0));
		assertEquals(name, jobConfig.encodingProfileConfig.name);
		assertEquals(url, jobConfig.httpInputConfig.url);
		assertEquals(profile, jobConfig.encodingProfileConfig.videoStreamConfigs.get(0).profile);
		assertEquals(profile2, jobConfig.encodingProfileConfig.videoStreamConfigs.get(1).profile);
	}
	
}
