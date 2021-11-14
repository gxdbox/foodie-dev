package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESGEOTest {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Test
    public void createStu() {
        Stu stu = new Stu();
        stu.setStuId(123456L);
        stu.setName("tom");
        stu.setAge(10123);
        stu.setMoney(3.346F);
        stu.setDescription("  dddd save fff man bat hhh man");
        stu.setDesc("guitar is the most beautiful thing");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    @Test
    public void updateIndexStu() {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("name", "slash");
        sourceMap.put("description", "bat man save");

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("12e")
                .withIndexRequest(indexRequest)
                .build();

        esTemplate.update(updateQuery);
    }

    @Test
    public void getQuery() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("12");
        Stu stu = esTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu);
    }

    @Test
    public void deleteStuDoc() {
        esTemplate.delete(Stu.class, "1");
    }

    @Test
    public void searchStuDoc() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "save man"))
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> stus = esTemplate.queryForPage(searchQuery, Stu.class);
        int totalPages = stus.getTotalPages();
        long totalElements = stus.getTotalElements();
        System.out.println("检索后分页的总页数： " + totalPages + "   检索后分页的总数" + totalElements);
        List<Stu> content = stus.getContent();
        for (Stu stu : content) {
            System.out.println(stu);
        }
    }

    @Test
    public void highLightStuDoc() {
        String preTag = "<font color = red>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(0, 10);
        SortBuilder sortBuilder = new FieldSortBuilder("money")
                .order(SortOrder.ASC);
        SortBuilder ageSortBuilder = new FieldSortBuilder("age")
                .order(SortOrder.ASC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "save man"))
                .withHighlightFields(new HighlightBuilder.Field("description")
                        .preTags(preTag)
                        .postTags(postTag))
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> stus = esTemplate.queryForPage(searchQuery, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                List<Stu> stuList = new ArrayList<>();
                for (SearchHit hit : hits) {
                    Stu stu = new Stu();
                    HighlightField highlightField = hit.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();
                    Object stuId = (Object) hit.getSourceAsMap().get("stuId");
                    Object money = (Object) hit.getSourceAsMap().get("money");
                    Object age = hit.getSourceAsMap().get("age");

                    stu.setDescription(description);
                    stu.setStuId(Long.valueOf(stuId.toString()));
                    stu.setMoney(Float.parseFloat(String.valueOf(money)));
                    stu.setAge(Integer.parseInt(String.valueOf(age)));
                    stuList.add(stu);
                }

                if (stuList.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) stuList);
                }

                return null;
            }
        });
        int totalPages = stus.getTotalPages();
        long totalElements = stus.getTotalElements();
        System.out.println("检索后分页的总页数： " + totalPages + "   检索后分页的总数" + totalElements);
        List<Stu> content = stus.getContent();
        for (Stu stu : content) {
            System.out.println(stu);
        }
    }

}
