-- 카테고리
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (0, '홈', null, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (1, '헬스용품', 0, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (2, '스포츠웨어', 0, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (3, '보조제', 0, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (4, '쉐이커&물통&케이스', 1, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (5, '홈트레이닝', 1, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (6, '티셔츠', 2, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (7, '나시', 2, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (8, '하의', 2, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (9, '모자', 2, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (10, '단백질 보충제', 3, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (11, '다이어트', 3, now(), now());
INSERT INTO CATEGORY(category_id, name, parent_id, created_date, last_modified_date) VALUES (12, '비타민', 3, now(), now());

-- 카탈로그
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (1, null, now(), null, now(), '쉐이커1', 10000, 100, 4);
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (2, null, now(), null, now(), '홈트레이닝1', 20000, 200, 5);
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (3, null, now(), null, now(), '티셔츠1', 30000, 300, 6);
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (4, null, now(), null, now(), '나시1', 40000, 400, 7);
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (5, null, now(), null, now(), '하의1', 50000, 500, 8);
INSERT INTO public.catalog(
    id, created_by, created_date, last_modified_by, last_modified_date, name, price, stock_quantity, category_id)
VALUES (6, null, now(), null, now(), '모자1', 60000, 600, 9);