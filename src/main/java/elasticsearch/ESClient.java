package elasticsearch;


import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by abel-sun on 16/6/23.
 */
public class ESClient {
    protected static Logger logger = LoggerFactory.getLogger(ESClient.class);
    private Client client;

    /**
     * 初始化
     */
    public void init() {
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", "elasticsearch").build();
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.55.255.245"), 9300));
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            logger.info("client{}", client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建index
     */
    public void creatIndex() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId((long) i);
            user.setName("huang fox " + i);
            user.setAge(i % 10);
            client.prepareIndex("users", "user").setSource(generateJson(user))
                    .execute().actionGet();
            logger.info("data {}", user);
        }
    }

    /**
     * 搜索indx
     */
    public void searchIndex() {
        SearchResponse response = client.prepareSearch("users").execute().actionGet();
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
        }
        logger.info("response{}", response);
    }

    /**
     * 更新index
     */
    public void updataIndex() {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index("users");
            updateRequest.type("user");
            updateRequest.id("AVV9H5FjR2u3W71hb2_G");
            updateRequest.doc(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("name", "sunzhenya")
                    .endObject());
            client.update(updateRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除index
     *
     * @return
     */
    public void DeleteIndex() {
        DeleteResponse response = client.prepareDelete("users", "user", "AVV8mDgNFhpaMrqq6Nnx").execute().actionGet();
        logger.info("删除结果{}", response);
    }

    private String generateJson(User user) {
        String json = "";
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                    .startObject();
            contentBuilder.field("_id", user.getId() + "");
            contentBuilder.field("id", user.getId() + "");
            contentBuilder.field("name", user.getName());
            contentBuilder.field("age", user.getAge() + "");
            json = contentBuilder.endObject().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 多索引Index
     */
    public void MultiIndex() {
        try {
            MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
//                    .add("users", "user", "1")
                    .add("users", "user", "AVV9H5ISR2u3W71hb2_N", "AVV9H5IER2u3W71hb2_K", "AVV9H5INR2u3W71hb2_M")
                    .add("users", "user", "AVV9H5IUR2u3W71hb2_O")
                    .get();

            for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    String json = response.getSourceAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 批量创建索引
     */
    public void bulkProcess() {

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            public void beforeBulk(long l, BulkRequest bulkRequest) {

            }

            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {

            }

            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                System.out.println("happen fail = " + throwable.getMessage() + " cause = " + throwable.getCause());
            }
        }).setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(20, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .build();
        for (int i = 0; i < 10; i++) {
            bulkProcessor.add(new IndexRequest("p2p", "p2phomelink").source("{\"test\":\"wangwangwan\"}"));
        }
        bulkProcessor.close();
    }

    /**
     * 批量搜索
     */

    public void MultiSearch() {

        SearchRequestBuilder srb1 = client
                .prepareSearch().setQuery(QueryBuilders.queryStringQuery("huang fox 1")).setSize(1);
        SearchRequestBuilder srb2 = client
                .prepareSearch().setQuery(QueryBuilders.matchQuery("test", "wangwangwang")).setSize(1);

        MultiSearchResponse sr = client.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .execute().actionGet();
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            SearchHits hits = response.getHits();
            nbHits += response.getHits().getTotalHits();

            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                System.out.println("json 数据" + json);
            }
        }

    }


    public void Count(){
        CountResponse response = client.prepareCount("test")
                .setQuery(termQuery("_type", "type1"))
                .execute()
                .actionGet();
    }
    /**
     * 关闭客户端
     */
    public void close() {
        client.close();
    }

    public static void main(String[] args) {
        ESClient esClient = new ESClient();
        esClient.init();

        //creatIndex
//        esClient.creatIndex();
//        esClient.searchIndex();
//        esClient.DeleteIndex();
        //updatIndex
//        esClient.updataIndex();
//        esClient.searchIndex();
//        esClient.MultiIndex();

//        esClient.bulkProcess();
        esClient.MultiSearch();
        esClient.close();

    }
}
