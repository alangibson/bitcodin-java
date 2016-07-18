/*
 * BitcodinApi.java
 *****************************************************************************
 * Copyright (C) 2015, bitmovin, All Rights Reserved
 *
 * Created on: Jun 17, 2015
 * Author: Christopher Mueller <christopher.mueller@bitmovin.net>
 *
 * This source code and its use and distribution, is subject to the terms
 * and conditions of the applicable license agreement.
 *****************************************************************************/

package com.bitmovin.bitcodin.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.bitmovin.bitcodin.api.billing.InvoiceInformation;
import com.bitmovin.bitcodin.api.exception.BitcodinApiException;
import com.bitmovin.bitcodin.api.input.*;
import com.bitmovin.bitcodin.api.job.Job;
import com.bitmovin.bitcodin.api.job.JobConfig;
import com.bitmovin.bitcodin.api.job.JobDetails;
import com.bitmovin.bitcodin.api.job.JobList;
import com.bitmovin.bitcodin.api.manifest.VttHls;
import com.bitmovin.bitcodin.api.manifest.VttHlsConfig;
import com.bitmovin.bitcodin.api.manifest.VttMpd;
import com.bitmovin.bitcodin.api.manifest.VttMpdConfig;
import com.bitmovin.bitcodin.api.media.EncodingProfile;
import com.bitmovin.bitcodin.api.media.EncodingProfileConfig;
import com.bitmovin.bitcodin.api.media.EncodingProfileList;
import com.bitmovin.bitcodin.api.notification.Event;
import com.bitmovin.bitcodin.api.notification.Subscription;
import com.bitmovin.bitcodin.api.notification.SubscriptionConfig;
import com.bitmovin.bitcodin.api.output.*;
import com.bitmovin.bitcodin.api.statistics.MonthlyStatistic;
import com.bitmovin.bitcodin.api.statistics.Statistic;
import com.bitmovin.bitcodin.api.thumbnail.Sprite;
import com.bitmovin.bitcodin.api.thumbnail.SpriteConfig;
import com.bitmovin.bitcodin.api.thumbnail.Thumbnail;
import com.bitmovin.bitcodin.api.thumbnail.ThumbnailConfig;
import com.bitmovin.bitcodin.api.transfer.Transfer;
import com.bitmovin.bitcodin.api.transfer.TransferConfig;
import com.bitmovin.network.http.JSONRestClient;
import com.bitmovin.network.http.RequestMethod;
import com.bitmovin.network.http.RestException;

public class BitcodinApi {

    private String apiKey;
    private String apiUrl;
    private HashMap<String, String> defaultHeaders = new HashMap<String, String>();

    public BitcodinApi(String apiKey) {
        this(apiKey, false);
    }

    public BitcodinApi(String apiKey, boolean useHttps) {
        this.apiKey = apiKey;
        this.apiUrl = (useHttps ? "https" : "http") + "://portal.bitcodin.com/api/";
        this.defaultHeaders.put("Content-Type", "application/json");
        this.defaultHeaders.put("bitcodin-api-version", "v1");
        this.defaultHeaders.put("bitcodin-api-key", this.apiKey);
    }

    public String getKey() {
        return this.apiKey;
    }
    
    public <T> T request (String resource, Map<String, String> headers, Object content, Class<T> classOfT, RequestMethod method) throws BitcodinApiException {
        JSONRestClient jRest;
        try {
            jRest = new JSONRestClient(new URI(this.apiUrl));
        } catch (URISyntaxException e) {
            throw new BitcodinApiException("API url not valid", e);
        }
        
        try {
            switch (method) {
                case POST:    return jRest.post(new URI(resource), this.defaultHeaders, content, classOfT);
                case GET:     return jRest.get(new URI(resource), this.defaultHeaders, classOfT);
                case DELETE:  return jRest.delete(new URI(resource), this.defaultHeaders, classOfT);
            }
        } catch (URISyntaxException e) {
            throw new BitcodinApiException("Resource url not valid", e);
        } catch (RestException e) {
            throw new BitcodinApiException("Request is not vaild: " + e.body, e);
        } catch (FileNotFoundException e) {
            throw new BitcodinApiException("Resource not available", e);
        } catch (IOException e) {
            throw new BitcodinApiException("Network problem", e);
        }
        
        throw new BitcodinApiException("Request method: " + method.name() + " is not supported");
    }
    public void request (String resource, Map<String, String> headers, Object content, RequestMethod method) throws BitcodinApiException {
        JSONRestClient jRest;
        try {
            jRest = new JSONRestClient(new URI(this.apiUrl));
        } catch (URISyntaxException e) {
            throw new BitcodinApiException("API url not valid", e);
        }
        
        try {
            switch (method) {
                case POST:    jRest.post(new URI(resource), this.defaultHeaders, content); return;
                case GET:     jRest.get(new URI(resource), this.defaultHeaders); return;
                case DELETE:  jRest.delete(new URI(resource), this.defaultHeaders); return;
            }
        } catch (URISyntaxException e) {
            throw new BitcodinApiException("Resource url not valid", e);
        } catch (RestException e) {
            throw new BitcodinApiException("Request is not vaild: " + e.body, e);
        } catch (FileNotFoundException e) {
            throw new BitcodinApiException("Resource not available", e);
        } catch (IOException e) {
            throw new BitcodinApiException("Network problem", e);
        }
        
        throw new BitcodinApiException("Request method: " + method.name() + " is not supported");
    }
    
    public <T> T post(String resource, Map<String, String> headers, Object content, Class<T> classOfT) throws BitcodinApiException{
        return this.request(resource, headers, content, classOfT, RequestMethod.POST);
    }
    public <T> T get(String resource, Map<String, String> headers, Class<T> classOfT) throws BitcodinApiException {
        return this.request(resource, headers, null, classOfT, RequestMethod.GET);
    }
    public <T> T delete(String resource, Map<String, String> headers, Class<T> classOfT) throws BitcodinApiException {
        return this.request(resource, headers, null, classOfT, RequestMethod.DELETE);
    }
    public void delete(String resource, Map<String, String> headers) throws BitcodinApiException {
        this.request(resource, headers, null, RequestMethod.DELETE);
    }
    public void post(String resource, Map<String, String> headers, Object content) throws BitcodinApiException {
        this.request(resource, headers, content, RequestMethod.POST);
    }

    public Input createInput(HTTPInputConfig httpInputConfig) throws BitcodinApiException {
        return this.post("input/create", this.defaultHeaders, httpInputConfig, Input.class);
    }

    public Input createAzureInput(AzureInputConfig azureInputConfig) throws BitcodinApiException {
        return this.post("input/create", this.defaultHeaders, azureInputConfig, Input.class);
    }

    public Input createS3Input(S3InputConfig s3InputConfig) throws BitcodinApiException {
        return this.post("input/create", this.defaultHeaders, s3InputConfig, Input.class);
    }

    public InputList listInputs(int pageNumber) throws BitcodinApiException {
        return this.get("inputs/" + Integer.toString(pageNumber), this.defaultHeaders, InputList.class);
    }

    public Input getInput(int id) throws BitcodinApiException {
        return this.get("input/" + Integer.toString(id), this.defaultHeaders, Input.class);
    }

    public void deleteInput(int id) throws BitcodinApiException {
        this.delete("input/" + Integer.toString(id), this.defaultHeaders);
    }

    public Output createS3Output(S3OutputConfig output) throws BitcodinApiException {
        return this.post("output/create", this.defaultHeaders, output, Output.class);
    }

    public Output createGCSOutput(GCSOutputConfig output) throws BitcodinApiException {
        return this.post("output/create", this.defaultHeaders, output, Output.class);
    }

    public Output createAzureOutput(AzureOutputConfig output) throws  BitcodinApiException {
        return this.post("output/create", this.defaultHeaders, output, Output.class);
    }

    public Output createFTPOutput(FTPOutputConfig output) throws BitcodinApiException {
        return this.post("output/create", this.defaultHeaders, output, Output.class);
    }

    public OutputList listOutputs(int pageNumber) throws BitcodinApiException {
        return this.get("outputs/" + Integer.toString(pageNumber), this.defaultHeaders, OutputList.class);
    }

    public Output getOutput(int id) throws BitcodinApiException {
        return this.get("output/" + Integer.toString(id), this.defaultHeaders, Output.class);
    }

    public void deleteOutput(int id) throws BitcodinApiException {
        this.delete("output/" + Integer.toString(id), this.defaultHeaders);
    }

    public EncodingProfile createEncodingProfile(EncodingProfileConfig profile) throws BitcodinApiException {
        return this.post("encoding-profile/create", this.defaultHeaders, profile, EncodingProfile.class);
    }

    public EncodingProfileList listEncodingProfiles(int pageNumber) throws BitcodinApiException {
        return this.get("encoding-profiles/" + Integer.toString(pageNumber), this.defaultHeaders, EncodingProfileList.class);
    }

    public EncodingProfile getEncodingProfile(int id) throws BitcodinApiException {
        return this.get("encoding-profile/" + Integer.toString(id), this.defaultHeaders, EncodingProfile.class);
    }

    public Job createJob(JobConfig jobConfig) throws BitcodinApiException {
        return this.post("job/create", this.defaultHeaders, jobConfig, Job.class);
    }

    public JobList listJobs(int pageNumber) throws BitcodinApiException {
        return this.get("jobs/" + Integer.toString(pageNumber), this.defaultHeaders, JobList.class);
    }

    public JobDetails getJobDetails(int id) throws BitcodinApiException {
        return this.get("job/" + Integer.toString(id), this.defaultHeaders, JobDetails.class);
    }

    public void transfer(TransferConfig transferConfig) throws BitcodinApiException {
        this.post("job/transfer", this.defaultHeaders, transferConfig);
    }

    public Transfer[] listTransfers(int jobId) throws BitcodinApiException {
        return this.get("job/" + Integer.toString(jobId) + "/transfers", this.defaultHeaders, Transfer[].class);
    }

    public MonthlyStatistic getMonthlyStatistics() throws BitcodinApiException {
        return this.get("statistics", this.defaultHeaders, MonthlyStatistic.class);
    }

    public Statistic getStatistics(String from, String to) throws BitcodinApiException {
        return this.get("statistics/jobs/" + from + "/" + to, this.defaultHeaders, Statistic.class);
    }
    
    public InvoiceInformation getInvoiceInfos() throws BitcodinApiException {
        return this.get("payment/invoiceinfo", this.defaultHeaders, InvoiceInformation.class);
    }
    
    public void updateInvoiceInfos(InvoiceInformation invoiceInfo) throws BitcodinApiException {
        this.post("payment/invoiceinfo", this.defaultHeaders, invoiceInfo);
    }

    public VttMpd createVttMpd(VttMpdConfig vttMpdConfig) throws BitcodinApiException {
        return this.post("manifest/mpd/vtt", this.defaultHeaders, vttMpdConfig, VttMpd.class);
    }

    public VttHls createVttHls(VttHlsConfig vttHlsConfig) throws BitcodinApiException {
        return this.post("manifest/hls/vtt", this.defaultHeaders, vttHlsConfig, VttHls.class);
    }

    public Thumbnail createThumbnail(ThumbnailConfig thumbnailConfig) throws BitcodinApiException {
        return this.post("thumbnail", this.defaultHeaders, thumbnailConfig, Thumbnail.class);
    }

    public Thumbnail getThumbnail(String thumbnailId) throws BitcodinApiException {
        return this.get("thumbnail/" + thumbnailId, this.defaultHeaders, Thumbnail.class);
    }

    public Sprite createSprite(SpriteConfig spriteConfig) throws BitcodinApiException {
        return this.post("sprite", this.defaultHeaders, spriteConfig, Sprite.class);
    }

    public Sprite getSprite(String spriteId) throws BitcodinApiException {
        return this.get("sprite/" + spriteId, this.defaultHeaders, Sprite.class);
    }

    public Event[] getAvailableEvents() throws BitcodinApiException {
        return this.get("notifications/events", this.defaultHeaders, Event[].class);
    }

    public Subscription createSubscription(SubscriptionConfig subscriptionConfig) throws BitcodinApiException {
        return this.post("notifications/subscriptions", this.defaultHeaders, subscriptionConfig, Subscription.class);
    }

    public Subscription getSubscription(String subscriptionId) throws BitcodinApiException {
        return this.get("notifications/subscriptions/" + subscriptionId, this.defaultHeaders, Subscription.class);
    }

    public Subscription[] listSubscriptions() throws BitcodinApiException {
        return this.get("notifications/subscriptions", this.defaultHeaders, Subscription[].class);
    }

    public void deleteSubscription(String subscriptionId) throws BitcodinApiException {
        this.delete("notifications/subscriptions/" + subscriptionId, this.defaultHeaders);
    }
}
