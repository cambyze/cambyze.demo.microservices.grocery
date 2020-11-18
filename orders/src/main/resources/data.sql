INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'), 'PO2045', 'SDW16' , sysdate, 3, true);
INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'),'PO2048', 'SDW16' , sysdate, 15, false);
INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'),'PO2049', 'SDW14' , sysdate, 20, false);