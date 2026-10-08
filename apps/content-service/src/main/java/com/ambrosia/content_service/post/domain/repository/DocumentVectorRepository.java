package com.ambrosia.content_service.post.domain.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.post.infrastructure.entity.DocumentVector;

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
                    coalesce(:tags, '')
                ),
                'A'
            )
        )
    """)
    int insertDocument(long id, String content, String tags, String title);

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
                        coalesce(:tags, '')
                    ),
                    'A'
                )
            )
            WHERE id = :id
    """)
    int update(long id, String content, String tags, String title);
}