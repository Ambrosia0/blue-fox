package com.ambrosia.content_service.search.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.search.model.entity.DocumentVector;

public interface DocumentVectorRepository extends CrudRepository<DocumentVector, Long> {
    @Modifying
    @Query("""
        INSERT INTO document_vector(id, search_vector)
        VALUES (
            :id, 
            setweight(
                to_tsvector(
                    'simple', 
                    coalesce(:title, '')
                ), 
                'A'
            ) ||
            setweight(
                to_tsvector(
                    'simple', 
                    coalesce(:content, '')
                ), 
                'D'
            ) ||
            setweight(
                to_tsvector(
                    'simple', 
                    coalesce(array_to_string(:tags::text[], ' '), '')
                ),
                'A'
            )
        )
    """)
    int insertDocument(long id, String content, List<String> tags, String title);

    @Modifying
    @Query("""
        UPDATE document_vector SET 
            search_vector = (
                setweight(
                    to_tsvector(
                        'simple', 
                        coalesce(:title, '')
                    ), 
                    'A'
                ) ||
                setweight(
                    to_tsvector(
                        'simple', 
                        coalesce(:content, '')
                    ), 
                    'D'
                ) ||
                setweight(
                    to_tsvector(
                        'simple', 
                        coalesce(array_to_string(:tags::text[], ' '), '')
                    ),
                    'A'
                )
            )
            WHERE id = :id
    """)
    int update(long id, String content, List<String> tags, String title);
}