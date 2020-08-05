package com.springvuegradle.team6.models;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedProfileRepositoryImpl implements CustomizedProfileRepository {
  @PersistenceContext private EntityManager em;

  /**
   * Uses Hibernate search and lucene queries to search for profiles that match the fullname
   *
   * @param terms The given fullname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return the query results
   */
  public org.hibernate.search.jpa.FullTextQuery searchFullnameQuery(
      String terms, String activityTypes, String method, int limit, int offset) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    try {
      fullTextEntityManager.createIndexer().startAndWait();
    } catch (Exception e) {
      System.out.println(e);
    }

    QueryBuilder queryBuilder =
            fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Profile.class).get();
    org.apache.lucene.search.Query luceneQuery;

    if (terms == null) {
      BooleanJunction activityQuery = addActivityTypeQuery(queryBuilder, activityTypes, method);
      luceneQuery = activityQuery.createQuery();
    } else {
      String[] splited = terms.split(" ");

      org.apache.lucene.search.Query firstnameQuery =
              queryBuilder
                      .keyword()
                      .onField("firstname")
                      .boostedTo(5)
                      .matching(splited[0])
                      .createQuery();

      org.apache.lucene.search.Query fullnameQuery =
              queryBuilder.simpleQueryString().onField("fullname").matching(terms).createQuery();

      if (splited.length > 1) {
        org.apache.lucene.search.Query lastnameQuery =
                queryBuilder
                        .keyword()
                        .onField("lastname")
                        .boostedTo(5)
                        .matching(splited[splited.length - 1])
                        .createQuery();

        BooleanJunction query =
                queryBuilder
                        .bool()
                        .should(firstnameQuery)
                        .should(lastnameQuery)
                        .should(fullnameQuery);
        if (activityTypes == null) {
          luceneQuery = query.createQuery();
        } else {
          BooleanJunction activityQuery = addActivityTypeQuery(queryBuilder, activityTypes, method);
          luceneQuery = queryBuilder.bool().must(query.createQuery()).must(activityQuery.createQuery()).createQuery();
        }
      } else {
        BooleanJunction query = queryBuilder.bool().should(firstnameQuery).should(fullnameQuery);
        if (activityTypes == null) {
          luceneQuery = query.createQuery();
        } else {
          BooleanJunction activityQuery = addActivityTypeQuery(queryBuilder, activityTypes, method);
          luceneQuery = queryBuilder.bool().must(query.createQuery()).must(activityQuery.createQuery()).createQuery();
        }
      }
    }
    org.apache.lucene.search.Sort sort = new Sort(
            SortField.FIELD_SCORE,
            new SortField("id", SortField.Type.STRING, true));

    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        fullTextEntityManager.createFullTextQuery(luceneQuery, Profile.class);
    jpaQuery.setSort(sort);

    // In the case where we want to implement server side pagination
    if (limit != -1) {
      jpaQuery.setMaxResults(limit);
    }
    if (offset != -1) {
      jpaQuery.setFirstResult(offset);
    }
    return jpaQuery;
  }

  /**
   * Helper function that builds and returns a query on activity types
   * @param queryBuilder the hibernate search query builder
   * @param activityTypes the activity types searched for
   * @param method the method used AND|OR
   * @return query a BooleanJunction
   */
  private BooleanJunction addActivityTypeQuery(QueryBuilder queryBuilder, String activityTypes, String method) {
    BooleanJunction query = queryBuilder.bool();
    String[] splitActivityTypes = activityTypes.split(" ");
    for (String activity : splitActivityTypes) {
      org.apache.lucene.search.Query activityQuery =
          queryBuilder
              .simpleQueryString()
              .onField("activityTypes")
              .matching(activity)
              .createQuery();
      if (method == null) {
        query.must(activityQuery);
      } else {
        if (method.equals("OR")) {
          query.should(activityQuery);
        } else {
          query.must(activityQuery);
        }
      }
    }
    return query;
  }

  /**
   * Searches the database using hibernate search to find a profile for a full name that matches
   *
   * @param terms The given query parameters
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return A list of profiles that matches the full name to some degree
   */
  @Override
  public List<Profile> searchFullname(String terms, String activityType, String method, int limit, int offset) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchFullnameQuery(terms, activityType, method, limit, offset);

    return jpaQuery.getResultList();
  }

  /**
   * Find the total number of profiles that matches the fullname
   *
   * @param terms The given nickname to search
   * @return A number of profiles that matches the nickname
   */
  @Override
  public Integer searchFullnameCount(String terms, String activityType, String method) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchFullnameQuery(terms, activityType, method, -1, -1);

    return jpaQuery.getResultSize();
  }

  /**
   * Uses Hibernate search and lucene queries to search for profiles that match the nickname
   *
   * @param terms The given nickname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return the query results
   */
  public org.hibernate.search.jpa.FullTextQuery searchNicknameQuery(
      String terms, String activityTypes, String method, int limit, int offset) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    try {
      fullTextEntityManager.createIndexer().startAndWait();
    } catch (Exception e) {
      System.out.println(e);
    }

    QueryBuilder queryBuilder =
        fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Profile.class).get();

    org.apache.lucene.search.Query nicknameQuery =
        queryBuilder.keyword().onField("nickname").matching(terms).createQuery();

    BooleanJunction query =
            queryBuilder
                    .bool()
                    .must(nicknameQuery);

    org.apache.lucene.search.Query luceneQuery;
    if (activityTypes == null) {
        luceneQuery =  queryBuilder.bool().must(query.createQuery()).createQuery();
    } else {
      BooleanJunction activityQuery = addActivityTypeQuery(queryBuilder, activityTypes, method);
      luceneQuery = queryBuilder.bool().must(query.createQuery()).must(activityQuery.createQuery()).createQuery();
    }

    org.apache.lucene.search.Sort sort = new Sort(
            SortField.FIELD_SCORE,
            new SortField("id", SortField.Type.STRING, true));

    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        fullTextEntityManager.createFullTextQuery(luceneQuery, Profile.class);
    jpaQuery.setSort(sort);
    // In the case where we want to implement server side pagination
    if (limit != -1) {
      jpaQuery.setMaxResults(limit);
    }
    if (offset != -1) {
      jpaQuery.setFirstResult(offset);
    }
    return jpaQuery;
  }

  /**
   * Searches the database using hibernate search to find a profile for a nickname that matches
   *
   * @param terms The given nickname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return A list of profiles that matches the nickname
   */
  @Override
  public List<Profile> searchNickname(String terms, String activityTypes, String method, int limit, int offset) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchNicknameQuery(terms, activityTypes, method, limit, offset);

    return jpaQuery.getResultList();
  }

  /**
   * Find the total number of profiles that matches the nickname
   *
   * @param terms The given nickname to search
   * @return A number of profiles that matches the nickname
   */
  @Override
  public Integer searchNicknameCount(String terms, String activityTypes, String method) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchNicknameQuery(terms, activityTypes, method,-1, -1);

    return jpaQuery.getResultSize();
  }
}
