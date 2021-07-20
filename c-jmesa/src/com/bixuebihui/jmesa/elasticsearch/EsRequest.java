package com.bixuebihui.jmesa.elasticsearch;

import com.bixuebihui.jmesa.elasticsearch.query.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.google.common.collect.Maps;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jmesa.limit.RowSelect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.jmesa.elasticsearch.EsQueryBuilder.EMPTY_JSON_STRING;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class EsRequest {

    String host;

    // The first thing you need is a runtime. These objects can compile expressions
    // and they are specific to the kind of structure you want to search in.
    // For most purposes you want the Jackson runtime, it can search in JsonNode
    // structures created by Jackson.
    JmesPath<JsonNode> jmespath = new JacksonRuntime();
    // Expressions need to be compiled before you can search. Compiled expressions
    // are reusable and thread safe. Compile your expressions once, just like database
    // prepared statements.
    Expression<JsonNode> exp = jmespath.compile("{caption:hits.hits[0]._index, " +
            "titles:keys(hits.hits[0]._source),"+
            "data:hits.hits[*]._source,"+
            "paging:{totalRows:hits.total.value}}");

    // This you have to fill in yourself, you're probably using Jackson's ObjectMapper
    // to load JSON data, and that should fit right in here.
    ObjectMapper mapper = new ObjectMapper();

    /**
     * index name to search
     * 需要检索的索引
     */
    String indexName;
    private String username;
    private String password;



    public String getHost() {
        return host;
    }

    public  EsRequest(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String createDoc(String indexName, List<Document> docs) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpEntityEnclosingRequestBase post;
        post = new HttpPost(host +"/"+ EsQueryBuilder.ACTION_BULK);
        StringBuilder sb = new StringBuilder();

        for(Document doc : docs) {
            Map<String, Object> map1 = Maps.newHashMap();
            Map<String, Object> map2 = Maps.newHashMap();
            map2.put("_index", indexName);
            map2.put("_id", doc.id);
            map2.put("_type", "_doc");
            map1.put("index", map2);
            sb.append(objectMapper.writeValueAsString(map1)).append("\n");

            sb.append(objectMapper.writeValueAsString(doc.values)).append("\n");
        }
        post.setEntity(new StringEntity(sb.toString(), APPLICATION_JSON));

        return httpJson(post);
    }

    public String createIndex(String indexName) {

        HttpEntityEnclosingRequestBase post;
        post = new HttpPut(host + "/" + indexName);

        return httpJson(post);
    }

    private String httpJson(HttpEntityEnclosingRequestBase requestBase) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            if(this.username!=null && password!=null) {
                Header header = new BasicHeader("Authorization", "Basic "
                        + Base64.getUrlEncoder().encodeToString((this.username+":"+this.password).getBytes(StandardCharsets.UTF_8)));
                requestBase.addHeader(header);
            }

            HttpResponse response = httpClient.execute(requestBase);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode() +
                        " with message:" + getResponseString(response));
            }

            return getResponseString(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY_JSON_STRING;
    }

    public String query(Query query, int from, int size) {
        try {
            HttpGetWithJsonEntity getRequest =
                    HttpGetWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + EsQueryBuilder.ACTION_SEARCH)
                            .withBodyEntry(EsQueryBuilder.build(query, from, size))
                            .build();

            return httpJson(getRequest);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EMPTY_JSON_STRING;
    }

    public String query(String rawJson) {
        try {
            HttpGetWithJsonEntity getRequest =
                    HttpGetWithJsonEntity.builder()
                            .withUrl(host + "/" + indexName + "/" + EsQueryBuilder.ACTION_SEARCH)
                            .withBodyEntry(rawJson)
                            .build();

            return httpJson(getRequest);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            e.printStackTrace();
        }
        return EMPTY_JSON_STRING;
    }

    private String getResponseString(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));

        String output;
        StringBuilder sb = new StringBuilder();
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }

    /**
     {
     "took" : 17,
     "timed_out" : false,
     "_shards" : {
     "total" : 1,
     "successful" : 1,
     "skipped" : 0,
     "failed" : 0
     },
     "hits" : {
     "total" : {
     "value" : 2,
     "relation" : "eq"
     },
     "max_score" : 1.0,
     "hits" : [
     {
     "_index" : "my-index-000001",
     "_type" : "_doc",
     "_id" : "1",
     "_score" : 1.0,
     "_source" : {
     "color" : [
     "blue",
     "green"
     ]
     }
     },
     {
     "_index" : "my-index-000001",
     "_type" : "_doc",
     "_id" : "2",
     "_score" : 1.0,
     "_source" : {
     "color" : "blue"
     }
     }
     ]
     }
     }

     ==>

     {
      "caption":"t_config",
      "titles": [
      "C _key",
      "c_name",
      "c_value"],
      "data": [{
      "c_key":"key1",
      "c_name":"name1",
      "c_value":"value1"}
      ],

      "paging":{"page":2,"maxRows":1,"rowEnd":2,"rowStart":1,"totalRows":3}}

     * @param esJson
     * @return
     */
    protected   String esJsonToTableJson(String esJson, RowSelect rowSelect) throws JsonProcessingException {
        JsonNode input = mapper.readTree(esJson);
        // Finally this is how you search a structure. There's really not much more to it.
        JsonNode resultTotal = exp.search(input);
        JsonNode paging = resultTotal.path("paging");
        if(rowSelect!=null) {
            ((ObjectNode) paging).put("maxRows", rowSelect.getMaxRows());
            ((ObjectNode) paging).put("rowEnd", rowSelect.getRowEnd());
            ((ObjectNode) paging).put("rowStart", rowSelect.getRowStart());
            ((ObjectNode) paging).put("page", rowSelect.getPage());
        }


        String res = resultTotal.toPrettyString();
        return res;
    }

    static class Document{
        String id;
        Map<String, Object> values;

        public Document(String id, Map<String, Object> values) {
            this.id = id;
            this.values = values;
        }
    }


}
