/*
  * Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
  * All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package be.i8c.wso2.msf4j.lora.repositories.elasticsearch;
import be.i8c.wso2.msf4j.lora.models.SensorRecord;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.gson.Gson;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;

/**
 * An adapter class that communicates with elasticsearch server through elasticsearch JAVA API.
 *
 * @author yanglin
 */
@Component
@Profile("elasticsearch")
public class LoRaElasticsearchAdapter
{
     private static final Logger logger = LogManager.getLogger(LoRaElasticsearchAdapter.class);

    /**
     * The hostname of server where elasticseach can be found. It will be automatically read from application.properties
     */
    @Value("${elasticsearch.host}")
     private String esHost;
    /**
     * The name of index. It will be automatically read from application.properties
     */
     @Value("${elasticsearch.index}")
     private String esIndex;
     @Value("${elasticsearch.port}")
     /**
      * The port number of elasticsearch server. It will be automatically read from application.properties
      */
     private int esPort;
    /**
     * The name of field to be mapped as date. It will be automatically read from application.properties
     */
    @Value("${elasticsearch.timestampName}")
     private String esTimestampName;

    /**
     * An object which communicates with elasticsearch server
     */
    private TransportClient client;
    /**
     * a boolean indicate whether index is exist or not.
     */
    private boolean indexExist;
    /**
     * The id of last indexed document, it indicates which id should be given to the next document.
     */
    private long idSequences;

    private Gson gson;
     
     public LoRaElasticsearchAdapter()
     {
         
     }

    /**
     * This method is used to initialize the connection with elasticsearch server and check the existence of given index.
     * It will be automatically executed once this class is constructed.
     */
    @PostConstruct
     private void init()
     {
         
         logger.debug("initiating elasticsearchAdapter");
         logger.debug("connecting elasticsearch server at " + this.esHost + ":" + this.esPort);
         this.gson = new Gson();
         this.indexExist = false;
         try 
         {
            this.client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.esHost), this.esPort));
            logger.info("the connection with elasticsearch server successfully established");
            
            logger.debug("checking if index: [" + this.esIndex + "] exist.");
            IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(this.esIndex);

            this.indexExist = this.client.admin().indices()
                              .exists(indicesExistsRequest).actionGet().isExists();
            if(this.indexExist)
            {
                logger.debug("index: [" + this.esIndex + "] exist.");
                this.idSequences = getLastId();
            }
            else
            {
                logger.debug("index: [" + this.esIndex + "] doesn't exist, it will be created at first input, idSequence set to 1");
                this.idSequences = 0;
            }
            
             
         } catch (UnknownHostException e) {
             logger.error("connection to " + this.esHost + ":" + this.esPort + " fails." +"\n " + e.getMessage());
         }
         
         
         
         
     }

    /**
     * This method is used to disconnect the elasticsearch server.
     * It will be automatically called just before the destruction of this class.
     */
    @PreDestroy
     private void destroy()
     {
         logger.info("destroying elasticsearchAdapter");
         logger.debug("disconnecting elasticsearch server at " + this.esHost + ":" + this.esPort);
         this.client.close();
         logger.info("elasticsearch disconnected");
     }

    /**
     * This method is used to create index and add date mapping to it's timestamp field.
     * This method will be called by first insertion of lora packet if the given index doesn't exist.
     * @param sensorRecord lora packet which will be indexed after creation of index.
     */
    private void createAndMapIndex(SensorRecord sensorRecord)
     {
        logger.info("try creating index: [" + this. esIndex + "]");
         try {
             client.admin().indices().prepareCreate(this.esIndex)   
               .addMapping(sensorRecord.getClass().getSimpleName(), "{\n" +
               "    \"" + sensorRecord.getClass().getSimpleName() + "\": {\n" +
               "      \"properties\": {\n" +
               "        \"" + this.esTimestampName + "\": {\n" +
               "          \"type\": \"date\"\n" +
               "        }\n" +
               "      }\n" +
               "    }\n" +
               "  }")
                .get();
         } catch (ResourceAlreadyExistsException e) {
             logger.error("index: [" + this.esIndex + "] already exist, not created");
         }
         logger.info("index: [" + this.esIndex + "] created.");
        
     }

    /**
     * This method is used to index a object into a specified index.
     * @param sensorRecord a object of sensorRecord which will be indexed.
     * @return  An object of sensorRecord or null when index unsuccessfully.
     */
    public SensorRecord index(SensorRecord sensorRecord) {
        //create and map the given TimestampName into index as type date,
        //otherwise elasticseach can't recognise timestamps
        if (!indexExist)
        {
            createAndMapIndex(sensorRecord);
            indexExist = true;
        }
        sensorRecord.setId(++this.idSequences);
        logger.debug("trying to index doc into: " + this.esIndex + ". object: " + sensorRecord.simpleString());
        String docString = this.gson.toJson(sensorRecord, sensorRecord.getClass());
        IndexResponse u =
                client.prepareIndex(esIndex, sensorRecord.getClass().getSimpleName(),Long.toString(sensorRecord.getId()))
                        .setSource(docString)
                        .get();
        DocWriteResponse.Result r = u.getResult();
        if (r == DocWriteResponse.Result.CREATED)
        {
            logger.info("successful indexed object with id {}", sensorRecord.getId());
            return sensorRecord;
        }
        else
        {
            logger.error("index object into" + this.esIndex + " fails. object: " + sensorRecord.simpleString());
            return null;
        }

    }
    /**
     * This method is used to index a list of objects into a specified index.
     * @param records a list of objects of sensorRecord which will be indexed.
     * @return  a list of objects of sensorRecord or null when index unsuccessfully.
     */
    public List<SensorRecord> index(List<SensorRecord> records)
    {
        if (!indexExist)
        {
            createAndMapIndex(records.get(0));
            indexExist = true;
        }
        logger.debug("try to index records: \n{}", records.stream().map(SensorRecord::simpleString).collect(Collectors.joining(",\n ")));
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        logger.debug("add doc into bulk request");
        records.forEach(r ->
        {
            logger.debug("adding doc {}", r.simpleString());
            addDocToBulkRequest(bulkRequest,r);
        });
        logger.debug("executing bulk request");
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures())
        {
            logger.error("bulk index fails. failure message: {}",bulkResponse.buildFailureMessage());
            return null;
        }
        else
        {
            String indexedIds = records.stream()
                    .map(e -> Long.toString(e.getId()))
                    .collect(Collectors.joining(", "));
            logger.info("successful indexed {} object with ids: [{}].",records.size(), indexedIds);
            logger.debug("indexed objects are: ", records.toString());
            return records;
        }
    }

    private void addDocToBulkRequest(BulkRequestBuilder bulkRequest, SensorRecord record)
    {
        record.setId(++this.idSequences);
        String docString = this.gson.toJson(record, record.getClass());
        bulkRequest.add(
                client.prepareIndex(esIndex, record.getClass().getSimpleName(), Long.toString(record.getId()))
                        .setSource(docString));
    }

    /**
     * Find the id of the last indexed document within the specified index.
     * @return the id of the last indexed document.
     */
    private long getLastId()
    {
        logger.info("Trying get last id from index " + this.esIndex );
        SearchResponse response = client.prepareSearch(this.esIndex)
                .addAggregation(
                        AggregationBuilders
                                .max("maxId")
                                .field("id")
                )
        .execute().actionGet();
        Max max = response.getAggregations().get("maxId");
        long id =  Math.round(max.getValue());
        logger.info("last id is " + id);
        return id;
    }
}

