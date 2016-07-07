package elasticsearch;

import com.fasterxml.jackson.databind.*;
import org.elasticsearch.action.fieldstats.FieldStats;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Created by abel-sun on 16/6/23.
 */
public class ClientTest {


    public static void main(String[] args) throws UnknownHostException {

        Client client = null;
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", "elasticsearch").build();
            client = TransportClient.builder().settings(settings).build();
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.55.255.245"), 9300));
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));

//            String json = "{" +
//                    "\"user\":\"kimchy\"," +
//                    "\"postDate\":\"2013-01-30\"," +
//                    "\"message\":\"trying out Elasticsearch\"" +
//                    "}";
            Map<String, Object> json = new HashMap<String, Object>();
            json.put("user","kimchy");
            json.put("postDate",new FieldStats.Date());
            json.put("message","trying out Elasticsearch");


//
//            IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
//                    .setSource(jsonBuilder()
//                            .startObject()
//                            .field("user", "kimchy")
//                            .field("postDate", new Date())
//                            .field("message", "trying out Elasticsearch")
//                            .endObject()
//                    )
//                    .get();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.close();
    }



}

