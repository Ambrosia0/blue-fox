INSERT INTO post(author_id, title, content, preview) VALUES(
    uuidv4(), 
    'testTitle', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent');

INSERT INTO post(author_id, title, content, preview) VALUES(
    uuidv4(), 
    'testTitle1', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent');

INSERT INTO post(author_id, title, content, preview, published, visible) VALUES(
    uuidv4(), 
    'testTitle2', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent',
    true,
    true);

INSERT INTO post(author_id, title, content, preview, published, visible) VALUES(
    uuidv4(), 
    'testTitle3', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent',
    true,
    true);


INSERT INTO post(author_id, title, content, preview, published, visible) VALUES(
    uuidv4(), 
    'testTitle4', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent',
    true,
    true);

INSERT INTO post(author_id, title, content, preview, published, visible) VALUES(
    uuidv4(), 
    'testTitle5', 
    'TestContentTestContentTestContentTestContentTestContent',
    'TestContentTestContentTestContentTestContentTestContent',
    true,
    false);